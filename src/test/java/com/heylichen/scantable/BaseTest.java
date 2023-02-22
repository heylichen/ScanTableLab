package com.heylichen.scantable;

import com.heylichen.scantable.biz.model.ShopIdScanParam;
import com.heylichen.scantable.dao.mapper.ShopItemsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigInteger;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScanTableLabApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {
  @Resource
  private ShopItemsMapper shopItemsMapper;

  @PostConstruct
  public void setUp() {
    ShopIdScanParam p = new ShopIdScanParam();
    p.setShopId(BigInteger.ONE);
    p.setLimit(1);
    log.info("------------------------ TEST SETUP BEGIN");
    log.info("before all test, force getting connection before test");
    //force getting connection before test
    shopItemsMapper.iterateEqShopId(p);
    log.info("------------------------ TEST SETUP END");
  }
}
