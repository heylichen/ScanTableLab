package com.heylichen.scantable.iterate;

import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
public class RangeScanOptions<P extends WithLimit, T> {
  private final Function<T,P> paramFactory;
  private final QueryParamUpdater<P, T> paramUpdater;
  private final Function<P, List<T>> queryExecutor;

  public RangeScanOptions(Function<T, P> paramFactory,
                          QueryParamUpdater<P, T> paramUpdater,
                          Function<P, List<T>> queryExecutor) {
    this.paramFactory = paramFactory;
    this.paramUpdater = paramUpdater;
    this.queryExecutor = queryExecutor;
  }
}
