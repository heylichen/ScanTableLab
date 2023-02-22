package com.heylichen.scantable;

import com.heylichen.scantable.biz.ThreeLevelMultiRangeScan;
import com.heylichen.scantable.biz.model.BusinessContext;
import com.heylichen.scantable.biz.model.ShopIdScanParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class MultiRangeScanTest extends BaseTest {
  @Resource
  private ThreeLevelMultiRangeScan threeLevelMultiRangeScan;

  @Test
  public void test3LevelScanLogic() {
    long start = System.currentTimeMillis();
    ShopIdScanParam p = new ShopIdScanParam();
    p.setShopIds(Arrays.asList(2001, 2002).stream().map(BigInteger::valueOf).collect(Collectors.toList()));
    p.setLimit(2);

    BusinessContext bc = threeLevelMultiRangeScan.scanByShopIds(p);
    Assert.assertEquals(8, bc.getTotal());
    long end = System.currentTimeMillis();
    log.info("total rows: {} , use {} ms", bc.getTotal(), end - start);
  }
}