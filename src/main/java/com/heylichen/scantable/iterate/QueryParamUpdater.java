package com.heylichen.scantable.iterate;

public interface QueryParamUpdater<P, T> {

    void updateNextQuery(P param, T row);
}