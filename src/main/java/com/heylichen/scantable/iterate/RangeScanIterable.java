package com.heylichen.scantable.iterate;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class RangeScanIterable<P extends WithLimit, T> implements Iterable<List<T>> {
  private final RangeScanIterator<P, T> scanIterator;

  public RangeScanIterable(RangeScanOptions<P, T> options) {
    this.scanIterator = new RangeScanIterator<>(options);
  }

  public RangeScanIterable(P param, QueryParamUpdater<P, T> paramUpdater, Function<P, List<T>> queryExecutor) {
    this.scanIterator = new RangeScanIterator<>(param, paramUpdater, queryExecutor);
  }

  @Override
  public Iterator<List<T>> iterator() {
    return scanIterator;
  }
}
