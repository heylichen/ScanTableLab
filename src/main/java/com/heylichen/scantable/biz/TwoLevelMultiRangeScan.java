package com.heylichen.scantable.biz;

import com.heylichen.scantable.biz.model.BusinessContext;
import com.heylichen.scantable.biz.model.UpdateTimeScanParam;
import com.heylichen.scantable.dao.domain.ShopItem;
import com.heylichen.scantable.dao.mapper.ShopItemsMapper;
import com.heylichen.scantable.iterate.MultiRangeScanIterable;
import com.heylichen.scantable.iterate.RangeScanOptions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static com.heylichen.scantable.biz.model.DateConstant.DATE_TIME_FORMATTER;

/**
 * test using single field secondary index to scan: update_time, (id implicitly).
 * Comparing this class to DirectCodedRangeScan.
 */
@Component
public class TwoLevelMultiRangeScan {
  @Resource
  private ShopItemsMapper shopItemsMapper;

  public BusinessContext scanByUpdateTimeRange(UpdateTimeScanParam p) {
    if (p == null || p.getUpdateTimeRange() == null) {
      throw new IllegalArgumentException("getUpdateTimeRange must not be null!");
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
  private Iterable<List<ShopItem>> genIterable(UpdateTimeScanParam p) {
    int limit = p.getLimit();
    return new MultiRangeScanIterable<>(Arrays.asList(
        new RangeScanOptions<>((p1) -> p, this::updateTimeRange, shopItemsMapper::iterateByUpdateTimeRange),
        new RangeScanOptions<>(previous -> this.genByUpdateTime(previous, limit), this::updateId, shopItemsMapper::iterateAtUpdateTime)
    ));
  }

  //level 1, scan by update_time ranges
  private void updateTimeRange(UpdateTimeScanParam p, ShopItem i) {
    p.getUpdateTimeRange().setMin(i.getUpdateTime().format(DATE_TIME_FORMATTER));
  }

  //level2 ,by id range
  private UpdateTimeScanParam genByUpdateTime(ShopItem previous, int limit) {
    UpdateTimeScanParam localParam = new UpdateTimeScanParam();
    localParam.setUpdateTime(previous.getUpdateTime().format(DATE_TIME_FORMATTER));
    //must set previous id, or previous row would be retrieved again.
    localParam.setPreviousId(previous.getId());
    localParam.setLimit(limit);
    return localParam;
  }

  private void updateId(UpdateTimeScanParam localParam, ShopItem previous) {
    localParam.setPreviousId(previous.getId());
  }

}
