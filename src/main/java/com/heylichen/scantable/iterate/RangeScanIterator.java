package com.heylichen.scantable.iterate;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

@Slf4j
/* default */ class RangeScanIterator<P extends WithLimit, T> implements Iterator<List<T>> {
  private final P param;
  private final QueryParamUpdater<P, T> paramUpdater;
  private final Function<P, List<T>> queryExecutor;
  //internal state
  private boolean hasNext;

  public RangeScanIterator(RangeScanOptions<P, T> options) {
    this.queryExecutor = options.getQueryExecutor();
    this.paramUpdater = options.getParamUpdater();
    this.param = options.getParamFactory().apply(null);
    this.hasNext = true;
  }

  public RangeScanIterator(P param, QueryParamUpdater<P, T> paramUpdater, Function<P, List<T>> queryExecutor) {
    this.param = param;
    this.paramUpdater = paramUpdater;
    this.queryExecutor = queryExecutor;
    this.hasNext = true;
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
    List<T> list = queryExecutor.apply(param);
    hasNext = list != null && list.size() >= param.getLimit();
    if (hasNext) {
      T previousRow = list.get(list.size() - 1);
      paramUpdater.updateNextQuery(param, previousRow);
    }
    return list;
  }
}
