# Scan table by index range
## a learning lab and tool
by heylichen@qq.com

## what's this for?
Say a multiple column index is idx_multi(f1, f2) and the primary index field is id.
The index contains 3 fields : f1, f2, id.

InnoDB automatically extends each secondary index by appending the primary key columns to it.
It can use the primary key for filtering and sorting.
(https://dev.mysql.com/doc/refman/5.7/en/index-extensions.html).

Given a range on the leftmost field of the index, how to scan all rows for business processing?
For example, given a f1 range, say In (1,2,3,4), how to scan all rows with f1 column in that range?

The basic idea is in ApplicationTest.testDirectIterateAll.

For a variable count of composite index columns, we can use MultiRangeScanIterable. The test case 
is MultiRangeScanTest.test3LevelScanLogic and ApplicationTest.test3LevelMultiRangeScanByShopIds.

## Test Table
test table shop_items. It contains 780108 rows.

DDL: src/test/resources/shop_items_DDL.sql
```
CREATE TABLE `shop_item`
(
    `id`          bigint unsigned  NOT NULL AUTO_INCREMENT,
    `item_name`   varchar(30)      NOT NULL DEFAULT '',
    `shop_id`     bigint unsigned  NOT NULL DEFAULT '0',
    `is_del`      tinyint unsigned NOT NULL DEFAULT '0',
    `create_time` datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_update_time` (`update_time`),
    KEY `idx_shop_id_del` (`shop_id`, `is_del`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='table of items selling at all shops.'
```

data: src/test/resources/shop_items.sql

data distribution.

| shop\_id | count\(\*\) |
| :--- | :--- |
| 1001 | 60000 |
| 2001 | 4 |
| 2002 | 4 |
| 10002 | 60010 |
| 10003 | 60010 |
| 10004 | 60010 |
| 10005 | 60010 |
| 10006 | 60010 |
| 10007 | 60010 |
| 10008 | 60010 |
