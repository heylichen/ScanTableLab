package com.heylichen.scantable;

import com.heylichen.scantable.biz.DirectCodedRangeScan;
import com.heylichen.scantable.biz.ThreeLevelMultiRangeScan;
import com.heylichen.scantable.biz.TwoLevelMultiRangeScan;
import com.heylichen.scantable.biz.model.BusinessContext;
import com.heylichen.scantable.biz.model.ShopIdScanParam;
import com.heylichen.scantable.biz.model.UpdateTimeScanParam;
import com.heylichen.scantable.range.StringRange;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class ApplicationTest extends BaseTest {
  @Resource
  private DirectCodedRangeScan directCodedRangeScan;
  @Resource
  private TwoLevelMultiRangeScan twoLevelMultiRangeScan;
  @Resource
  private ThreeLevelMultiRangeScan threeLevelMultiRangeScan;

  private static final Integer LIMIT = 1000;
  private static final StringRange TIME_RANGE = new StringRange("2023-02-13 23:59:59", "2023-02-15 00:00:00");

  @Test
  public void testDirectIterateAll() {
    UpdateTimeScanParam p = genUpdateTimeParam();

    BusinessContext bc = directCodedRangeScan.scanProcess(p);
    log.info("total rows: {}", bc.getTotal());
  }

  @Test
  public void test2LevelMultiRangeScanByUpdateTime() {
    UpdateTimeScanParam p = genUpdateTimeParam();

    BusinessContext bc = twoLevelMultiRangeScan.scanByUpdateTimeRange(p);
    log.info("total rows: {}", bc.getTotal());
  }

  private static UpdateTimeScanParam genUpdateTimeParam() {
    UpdateTimeScanParam p = new UpdateTimeScanParam();
    p.setLimit(LIMIT);
    p.setUpdateTimeRange(TIME_RANGE);
    return p;
  }

  @Test
  public void test3LevelMultiRangeScanByShopIds() {
    ShopIdScanParam p = new ShopIdScanParam();
    p.setShopIds(Arrays.asList(1001, 10002, 10003, 10004, 10005,
        10006, 10007, 10012, 10013).stream().map(BigInteger::valueOf).collect(Collectors.toList()));
    p.setLimit(LIMIT);

    BusinessContext bc = threeLevelMultiRangeScan.scanByShopIds(p);
    Assert.assertEquals(540060, bc.getTotal());
    log.info("total rows: {}", bc.getTotal());
  }
}