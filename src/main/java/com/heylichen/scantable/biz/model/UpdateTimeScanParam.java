package com.heylichen.scantable.biz.model;

import com.heylichen.scantable.iterate.WithLimit;
import com.heylichen.scantable.range.StringRange;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
public class UpdateTimeScanParam implements Serializable, WithLimit {
  private static final long serialVersionUID = 1L;
  //for case 1
  private StringRange updateTimeRange;
  //for case 2
  private String updateTime;
  private BigInteger previousId;
  private Integer limit;
}
