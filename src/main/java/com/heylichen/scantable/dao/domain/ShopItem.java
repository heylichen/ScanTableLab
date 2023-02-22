package com.heylichen.scantable.dao.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * table of items selling at all shops.
 *
 * @TableName shop_items
 */
@Getter
@Setter
public class ShopItem implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   *
   */
  private BigInteger id;

  /**
   *
   */
  private String itemName;

  /**
   *
   */
  private BigInteger shopId;

  /**
   *
   */
  private Integer isDel;

  /**
   *
   */
  private LocalDateTime createTime;

  /**
   *
   */
  private LocalDateTime updateTime;


}