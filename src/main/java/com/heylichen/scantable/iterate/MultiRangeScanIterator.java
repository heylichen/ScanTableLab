package com.heylichen.scantable.iterate;

import java.util.*;

/* default */ class MultiRangeScanIterator<P extends WithLimit, T> implements Iterator<List<T>> {
  private final Deque<Iterator<List<T>>> stack;
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
      //push all sub index iterators
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