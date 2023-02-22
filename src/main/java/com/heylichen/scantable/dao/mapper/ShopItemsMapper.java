package com.heylichen.scantable.dao.mapper;


import com.heylichen.scantable.biz.model.ShopIdScanParam;
import com.heylichen.scantable.dao.domain.ShopItem;
import com.heylichen.scantable.biz.model.UpdateTimeScanParam;

import java.util.List;

/**
* @author lc
* @description 针对表【shop_items(table of items selling at all shops.)】的数据库操作Mapper
* @createDate 2023-02-18 20:14:44
* @Entity com.heylichen.scantablelab.dao.domain.ShopItemsMapper
*/
public interface ShopItemsMapper {

  List<ShopItem> iterateByUpdateTimeRange(UpdateTimeScanParam param);

  List<ShopItem> iterateAtUpdateTime(UpdateTimeScanParam param);


  List<ShopItem> iterateByShopIdRange(ShopIdScanParam param);

  List<ShopItem> iterateEqShopId(ShopIdScanParam param);

  List<ShopItem> iterateEqShopIdDel(ShopIdScanParam param);
}