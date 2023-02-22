package com.heylichen.scantable.biz.model;

import com.heylichen.scantable.iterate.WithLimit;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class ShopIdScanParam implements Serializable, WithLimit {
  private static final long serialVersionUID = 1L;
  //for case 1
  private List<BigInteger> shopIds;
  private BigInteger previousShopId;
  //for case 2
  private BigInteger shopId;
  private List<Integer> isDels;
  private Integer previousIsDel;
  //for case 3
  private Integer isDel;
  private BigInteger previousId;

  private Integer limit;
}
