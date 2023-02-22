package com.heylichen.scantable.iterate;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class MultiRangeScanIterable<P extends WithLimit, T> implements Iterable<List<T>> {
  private final MultiRangeScanIterator<P, T> scanIterator;

  public MultiRangeScanIterable(List<RangeScanOptions<P, T>> multiRangeScanOptions) {
    this.scanIterator = new MultiRangeScanIterator<>(multiRangeScanOptions);
  }

  @Override
  public Iterator<List<T>> iterator() {
    return scanIterator;
  }
}