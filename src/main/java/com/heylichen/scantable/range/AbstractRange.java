package com.heylichen.scantable.range;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbstractRange <T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private T min;
  private T max;
}
