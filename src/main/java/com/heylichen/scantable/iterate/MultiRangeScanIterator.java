package com.heylichen.scantable.iterate;

import java.util.*;

/**
 * As an example for all comments in this class. The test table named test_table. <br>
 * It has a composite index named idx_f1_f2 on (f1, f2), and primary key is id column.<br>
 *
 * Given a range of f1, this is a helper tool to scan all rows in that range using index idx_f1_f2 and<br>
 * wrap the scan logic as an Iterator<List<T>>.<br>
 *
 * Actually, you can use it to scan a composite index of variable columns. See TwoLevelMultiRangeScan
 * and ThreeLevelMultiRangeScan as usage examples.
 * @param <P>
 * @param <T>
 */
/* default */ class MultiRangeScanIterator<P extends WithLimit, T> implements Iterator<List<T>> {
  /**
   * always peek the first Iterator which hasNext from top and issue a query.
   */
  private final Deque<Iterator<List<T>>> stack;
  /**
   *
   * level 0 correspond to the Iterator of SQL: <br/>
   *         select f1,f2,id from test_table where f1 > #{previousF1} order by f1, f2, id limit N;<br/>
   * the RangeScanOptions of level 0 is stored at index o in multiRangeScanOptions.<br/>
   *
   * level 1 correspond to the Iterator of SQL:<br/>
   *         select f1,f2,id from test_table where f1 =#{previousF1} AND f2 > #{previousF2}<br/>
   *                order by f1, f2, id limit N;<br/>
   * the RangeScanOptions of level 1 is stored at index 1 in multiRangeScanOptions.<br/>
   *
   * level 2 correspond to the Iterator of SQL:<br/>
   *         select f1,f2,id from test_table where f1 =#{previousF1} AND f2 = #{previousF2}<br/>
   *                AND id>#{previousId} order by f1, f2, id limit N;<br/>
   * the RangeScanOptions of level 2 is stored at index 2 in multiRangeScanOptions.<br/>
   *
   * ...
   */
  private final List<RangeScanOptions<P, T>> multiRangeScanOptions;
  private boolean hasNext;

  public MultiRangeScanIterator(List<RangeScanOptions<P, T>> multiRangeScanOptions) {
    if (multiRangeScanOptions == null || multiRangeScanOptions.isEmpty()) {
      throw new IllegalArgumentException("multiRangeScanOptions must not be empty!");
    }
    stack = new ArrayDeque<>(multiRangeScanOptions.size());
    this.multiRangeScanOptions = multiRangeScanOptions;
    hasNext = true;

    RangeScanOptions<P, T> firstLevelOption = multiRangeScanOptions.get(0);
    RangeScanIterable<P, T> firstIt = new RangeScanIterable<>(firstLevelOption);
    stack.push(firstIt.iterator());
  }

  @Override
  public boolean hasNext() {
    return hasNext;
  }

  @Override
  public List<T> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    if (stack.isEmpty()) {
      throw new IllegalArgumentException("stack is empty while hasNext() returns true!");
    }
    //Iterator on top may have no next. eg, if the first Iterator has no next.
    PositionedIterator<T> nonEmptyTop = peekNonEmptyTop();
    if (nonEmptyTop == null) {
      this.hasNext = false;
      return Collections.emptyList();
    }

    Iterator<List<T>> iterator = nonEmptyTop.iterator;
    // issue a database query
    List<T> rows = iterator.next();
    if (iterator.hasNext()) {
      T previous = rows.get(rows.size() - 1);
      pushAllSubIterators(previous, nonEmptyTop.optionsPosition + 1);
    } else {
      //pop current iterator
      stack.pop();
      if (stack.isEmpty()) {
        //current is first level index and !hasNext(), over.
        this.hasNext = false;
      }
    }
    return rows;
  }

  /**
   * for example. if the table has a composite index on (f1, f2), and primary key is id column.<br/>
   * QueryLevel1: select f1,f2,id from test_table where f1 in (???) order by f1, f2, id limit N;<br>
   * returned N rows. set the last row as previousRow. Say current query level is QueryLevel1.<br/>
   *
   * if resultSet.count < N, there are no more rows to query. We can pop the Iterator from stack.<br/>
   *
   * Because it returned N rows, we know there can be more rows match previousRow.f1 and previousRow.f2,<br/>
   * with different ids. We need to exhaust all rows match previousRow.f1 and previousRow.f2, use QueryLevel3.<br/>
   *
   * QueryLevel3 select f1,f2,id from test_table where f1 =#{previousF1} AND f2 = #{previousF2}<br/>
   *        AND id>#{previousId} order by f1, f2, id limit N;<br/>
   *
   * When all rows match previousRow.f1 and previousRow.f2 exhausted. There may be rows with previousRow.f1
   * but different f2. We need to exhaust all rows match previousRow.f1 with different f2, use QueryLevel2:<br/>
   *
   * QueryLevel2 select f1,f2,id from test_table where f1 =#{previousF1} AND f2 > #{previousF2}<br/>
   *        order by f1, f2, id limit N; <br/>
   *
   * So we push all QueryLevels above current level(eg. QueryLevel1), in the order QueryLevel2, QueryLevel3.
   * Because we peek Iterator from stack top and issue query. <br/>
   * This is a recursive process, so we push all sub-level Iterators onto the stack each time the current
   * query returned N rows.
   * @param previous
   * @param nextLevelOffset
   */
  private void pushAllSubIterators(T previous, int nextLevelOffset) {
    int currentOffset = nextLevelOffset;
    while (currentOffset < multiRangeScanOptions.size()) {
      Iterator<List<T>> nextLevelIterator = genNextLevelIterator(previous, currentOffset);
      stack.push(nextLevelIterator);
      currentOffset++;
    }
  }

  private Iterator<List<T>> genNextLevelIterator(T previous, int offset) {
    RangeScanOptions<P, T> options = multiRangeScanOptions.get(offset);

    P nextLevelParam = options.getParamFactory().apply(previous);
    RangeScanIterable<P, T> nextLevelIt = new RangeScanIterable<>(nextLevelParam,
        options.getParamUpdater(), options.getQueryExecutor());
    return nextLevelIt.iterator();
  }

  private PositionedIterator<T> peekNonEmptyTop() {
    Iterator<List<T>> nonEmptyTop = stack.peek();
    // the offset of RangeScanOptions corresponding to nonEmptyTop in multiRangeScanOptions
    int offset = stack.size() - 1;
    while (!nonEmptyTop.hasNext()) {
      stack.pop();
      offset--;
      if (stack.isEmpty()) {
        break;
      }
      nonEmptyTop = stack.peek();
    }
    return nonEmptyTop.hasNext() ? new PositionedIterator<>(nonEmptyTop, offset) : null;
  }

  private static class PositionedIterator<T> {
    private final Iterator<List<T>> iterator;
    private final int optionsPosition;

    public PositionedIterator(Iterator<List<T>> iterator, int optionsPosition) {
      this.iterator = iterator;
      this.optionsPosition = optionsPosition;
    }
  }

}