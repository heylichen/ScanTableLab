package com.heylichen.scantable.range;

public class StringRange extends AbstractRange<String> {
  private static final long serialVersionUID = 1L;

  public StringRange() {
  }

  public StringRange(String min, String max) {
    super(min, max);
  }
}