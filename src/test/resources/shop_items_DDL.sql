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