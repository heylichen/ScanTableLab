package com.heylichen.scantable.biz;

import com.heylichen.scantable.biz.model.BusinessContext;
import com.heylichen.scantable.biz.model.ShopIdScanParam;
import com.heylichen.scantable.dao.domain.ShopItem;
import com.heylichen.scantable.dao.mapper.ShopItemsMapper;
import com.heylichen.scantable.iterate.MultiRangeScanIterable;
import com.heylichen.scantable.iterate.RangeScanOptions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * test using multi-field combined index to scan: shop_id, is_del, (id implicitly)
 */
@Component
public class ThreeLevelMultiRangeScan {
  @Resource
  private ShopItemsMapper shopItemsMapper;

  public BusinessContext scanByShopIds(ShopIdScanParam p) {
    if (p == null || p.getShopIds() == null || p.getShopIds().isEmpty()) {
      throw new IllegalArgumentException("shopIds must not be null!");
    }
    Iterable<List<ShopItem>> it = genIterable(p);

    BusinessContext bc = new BusinessContext();
    for (List<ShopItem> shopItems : it) {
      bc.add(shopItems.size());
    }
    return bc;
  }

  /**
   * More methods
   *
   * @param p
   * @return
   */
  private Iterable<List<ShopItem>> genIterable(ShopIdScanParam p) {
    int limit = p.getLimit();
    return new MultiRangeScanIterable<>(Arrays.asList(
        new RangeScanOptions<>((p1) -> p, this::updatePreviousShopId, shopItemsMapper::iterateByShopIdRange),
        new RangeScanOptions<>((ShopItem previous) -> genByShopId(previous, limit), this::updateIsDel, shopItemsMapper::iterateEqShopId),
        new RangeScanOptions<>((ShopItem previous) -> genByShopIdDel(previous, limit), this::updateId, shopItemsMapper::iterateEqShopIdDel)
    ));
  }

  //level 1, scan by shopId ranges
  private void updatePreviousShopId(ShopIdScanParam p, ShopItem previous) {
    p.setPreviousShopId(previous.getShopId());
  }

  //level2, eq shopId, by isDel ranges
  private ShopIdScanParam genByShopId(ShopItem previous, int limit) {
    ShopIdScanParam p = new ShopIdScanParam();
    p.setShopId(previous.getShopId());
    p.setPreviousIsDel(previous.getIsDel());
    p.setLimit(limit);
    return p;
  }

  private void updateIsDel(ShopIdScanParam p, ShopItem previous) {
    p.setPreviousIsDel(previous.getIsDel());
  }

  //level3, eq shopId, isDel, by id ranges
  private ShopIdScanParam genByShopIdDel(ShopItem previous, int limit) {
    ShopIdScanParam p = new ShopIdScanParam();
    p.setShopId(previous.getShopId());
    p.setIsDel(previous.getIsDel());
    p.setPreviousId(previous.getId());
    p.setLimit(limit);
    return p;
  }

  private void updateId(ShopIdScanParam p, ShopItem previous) {
    p.setPreviousId(previous.getId());
  }

}
