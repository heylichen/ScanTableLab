package com.heylichen.scantable.biz;

import com.heylichen.scantable.biz.model.BusinessContext;
import com.heylichen.scantable.biz.model.UpdateTimeScanParam;
import com.heylichen.scantable.dao.domain.ShopItem;
import com.heylichen.scantable.dao.mapper.ShopItemsMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.heylichen.scantable.biz.model.DateConstant.DATE_TIME_FORMATTER;

/**
 * illustrate basic idea, how to iterate by hand-written code
 */
@Component
public class DirectCodedRangeScan {
  @Resource
  private ShopItemsMapper shopItemsMapper;

  public BusinessContext scanProcess(UpdateTimeScanParam p) {
    BusinessContext bc = new BusinessContext();
    while (true) {
      //empty results will be empty collection. No NPE can occur.
      List<ShopItem> items = shopItemsMapper.iterateByUpdateTimeRange(p);
      businessProcess(items, bc);

      //not enough rows to fill a full page, no need to issue another query
      if (items.size() < p.getLimit()) {
        break;
      }
      ShopItem previous = getPrevious(items);
      iterateAtUpdateTime(previous, p.getLimit(), bc);

      // update param for case 1
      p.getUpdateTimeRange().setMin(previous.getUpdateTime().format(DATE_TIME_FORMATTER));
    }
    return bc;
  }

  private void iterateAtUpdateTime(ShopItem previous, int limit, BusinessContext bc) {
    UpdateTimeScanParam localParam = new UpdateTimeScanParam();
    localParam.setLimit(limit);
    localParam.setUpdateTime(previous.getUpdateTime().format(DATE_TIME_FORMATTER));
    //must set previous id, or previous row would be retrieved again.
    localParam.setPreviousId(previous.getId());

    while (true) {
      List<ShopItem> items = shopItemsMapper.iterateAtUpdateTime(localParam);
      businessProcess(items, bc);

      //not enough rows to fill a full page, no need to issue another query
      if (items.size() < limit) {
        break;
      }
      previous = getPrevious(items);
      localParam.setPreviousId(previous.getId());
    }
  }

  private void businessProcess(List<ShopItem> items, BusinessContext bc) {
    bc.add(items.size());
  }

  private static <T> T getPrevious(List<T> items) {
    return items.get(items.size() - 1);
  }
}
