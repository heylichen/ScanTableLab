package com.heylichen.scantable.biz.model;

import lombok.Getter;

@Getter
public class BusinessContext {
  private int total = 0;

  public void add(int delta) {
    total += delta;
  }
}
