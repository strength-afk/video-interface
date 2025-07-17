/*
 Navicat Premium Data Transfer

 Source Server         : fb_video
 Source Server Type    : MySQL
 Source Server Version : 90300 (9.3.0)
 Source Host           : localhost:3306
 Source Schema         : fb_video

 Target Server Type    : MySQL
 Target Server Version : 90300 (9.3.0)
 File Encoding         : 65001

 Date: 17/07/2025 17:17:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activation_codes
-- ----------------------------
DROP TABLE IF EXISTS `activation_codes`;
CREATE TABLE `activation_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '激活码ID，主键，自增',
  `code` varchar(32) NOT NULL COMMENT '激活码，唯一，不可为空',
  `code_type` enum('VIP','RECHARGE') NOT NULL COMMENT '激活码类型：VIP-会员激活码，RECHARGE-充值激活码',
  `code_status` enum('UNUSED','USED','EXPIRED','DISABLED') NOT NULL COMMENT '激活码状态：UNUSED-未使用，USED-已使用，EXPIRED-已过期，DISABLED-已禁用',
  `vip_duration` int DEFAULT NULL COMMENT 'VIP时长（天），仅会员激活码有效',
  `recharge_amount` decimal(10,2) DEFAULT NULL COMMENT '充值金额，仅充值激活码有效',
  `used_by` bigint DEFAULT NULL COMMENT '使用用户ID',
  `used_at` datetime DEFAULT NULL COMMENT '使用时间',
  `expire_at` datetime DEFAULT NULL COMMENT '过期时间',
  `batch_number` varchar(50) DEFAULT NULL COMMENT '批次号，用于批量管理',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注信息',
  `created_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `recharge_package_id` bigint DEFAULT NULL COMMENT '充值套餐ID，关联充值套餐表',
  `recharge_package_name` varchar(50) DEFAULT NULL COMMENT '充值套餐名称，冗余字段便于查询',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='激活码表';

-- ----------------------------
-- Table structure for flyway_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for movie_categories
-- ----------------------------
DROP TABLE IF EXISTS `movie_categories`;
CREATE TABLE `movie_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(200) DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(200) DEFAULT NULL COMMENT '分类图标URL',
  `weight` int NOT NULL DEFAULT '0' COMMENT '排序权重',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电影分类表';

-- ----------------------------
-- Table structure for movies
-- ----------------------------
DROP TABLE IF EXISTS `movies`;
CREATE TABLE `movies` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '电影ID，主键，自增',
  `title` varchar(200) NOT NULL COMMENT '电影标题，不可为空',
  `description` text COMMENT '剧情简介',
  `cover` varchar(500) DEFAULT NULL COMMENT '封面图片相对路径',
  `banner` varchar(500) DEFAULT NULL COMMENT 'Banner图片相对路径',
  `duration` varchar(20) DEFAULT NULL COMMENT '电影时长，格式：HH:mm:ss',
  `rating` decimal(3,1) DEFAULT '0.0' COMMENT '评分，0.0-10.0',
  `views` bigint NOT NULL DEFAULT '0' COMMENT '观看次数',
  `likes` bigint NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `favorites` bigint NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `release_date` datetime DEFAULT NULL COMMENT '发布日期',
  `release_year` int DEFAULT NULL COMMENT '发行年份',
  `category_id` bigint DEFAULT NULL COMMENT '电影分类ID',
  `region_id` bigint DEFAULT NULL COMMENT '电影地区ID',
  `quality` varchar(50) DEFAULT NULL COMMENT '画质：720P、1080P、4K等',
  `tags` json DEFAULT NULL COMMENT '标签，JSON格式存储',
  `is_vip` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为VIP专享',
  `is_free` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否为免费电影',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '价格，收费电影的价格',
  `trial_duration` int NOT NULL DEFAULT '0' COMMENT '试看时长（秒），0表示不允许试看',
  `file_path` varchar(500) DEFAULT NULL COMMENT '视频文件路径',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_format` varchar(20) DEFAULT NULL COMMENT '文件格式：mp4、mkv、avi等',
  `charge_type` enum('FREE','VIP') NOT NULL COMMENT '收费类型：FREE-免费，VIP-VIP专享',
  `status` enum('ACTIVE','INACTIVE','DELETED') NOT NULL COMMENT '状态：ACTIVE-上架，INACTIVE-下架，DELETED-已删除',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序权重，数字越大越靠前',
  `is_super_recommended` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为超级推荐电影（轮播图显示）',
  `is_recommended` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为推荐电影',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `region_id` (`region_id`),
  CONSTRAINT `movies_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `movie_categories` (`id`) ON DELETE SET NULL,
  CONSTRAINT `movies_ibfk_2` FOREIGN KEY (`region_id`) REFERENCES `regions` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电影表';

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键，自增',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `created_at` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `order_no` varchar(64) NOT NULL COMMENT '订单号，唯一标识',
  `order_status` enum('PENDING','PAID','CANCELLED','REFUNDED') NOT NULL COMMENT '订单状态：PENDING-待支付，PAID-已支付，CANCELLED-已取消，REFUNDED-已退款',
  `order_type` enum('VIP_PURCHASE','MOVIE_PURCHASE','MANGA_PURCHASE','NOVEL_PURCHASE','ACTIVATION_CODE','RECHARGE') NOT NULL COMMENT '订单类型：VIP_PURCHASE-VIP购买，MOVIE_PURCHASE-电影购买，ACTIVATION_CODE-激活码购买，RECHARGE-充值',
  `paid_time` datetime(6) DEFAULT NULL COMMENT '支付时间',
  `payment_method` enum('BALANCE','WECHAT','ALIPAY') NOT NULL COMMENT '支付方式：BALANCE-余额，WECHAT-微信，ALIPAY-支付宝',
  `product_id` bigint DEFAULT NULL COMMENT '产品ID，关联具体产品（VIP包ID、电影ID、激活码ID等）',
  `product_name` varchar(200) DEFAULT NULL COMMENT '产品名称',
  `remark` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `updated_at` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `user_id` bigint NOT NULL COMMENT '用户ID，关联用户表',
  `out_no` varchar(64) DEFAULT NULL COMMENT '第三方订单编号',
  `pay_no` varchar(64) DEFAULT NULL COMMENT '支付编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g8pohnngqi5x1nask7nff2u7w` (`order_no`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ----------------------------
-- Table structure for recharge_packages
-- ----------------------------
DROP TABLE IF EXISTS `recharge_packages`;
CREATE TABLE `recharge_packages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '套餐ID，主键，自增',
  `created_at` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `description` varchar(200) DEFAULT NULL COMMENT '套餐描述',
  `name` varchar(50) NOT NULL COMMENT '套餐名称，如30元充值卡、50元充值卡',
  `recharge_amount` decimal(10,2) NOT NULL COMMENT '充值金额，单位元',
  `status` enum('ACTIVE','INACTIVE','DELETED') NOT NULL COMMENT '状态：ACTIVE-上架，INACTIVE-下架，DELETED-已删除',
  `updated_at` datetime(6) DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='充值套餐表';

-- ----------------------------
-- Table structure for regions
-- ----------------------------
DROP TABLE IF EXISTS `regions`;
CREATE TABLE `regions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地区ID',
  `name` varchar(50) NOT NULL COMMENT '地区名称',
  `description` varchar(200) DEFAULT NULL COMMENT '地区描述',
  `icon` varchar(200) DEFAULT NULL COMMENT '地区图标URL',
  `weight` int NOT NULL DEFAULT '0' COMMENT '排序权重',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='地区表';

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_name` varchar(100) NOT NULL COMMENT '网站名称',
  `site_desc` varchar(255) DEFAULT NULL COMMENT '网站描述',
  `wechat` varchar(100) DEFAULT NULL COMMENT '微信',
  `telegram` varchar(100) DEFAULT NULL COMMENT 'Telegram',
  `system_email` varchar(100) DEFAULT NULL COMMENT '系统邮箱',
  `version` varchar(50) DEFAULT NULL COMMENT '系统版本号',
  `copyright` varchar(255) DEFAULT NULL COMMENT '版权信息',
  `allow_register` tinyint(1) DEFAULT '1' COMMENT '是否允许注册',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `mail_pass` varchar(255) DEFAULT NULL COMMENT '发件邮箱密码/授权码',
  `mail_user` varchar(255) DEFAULT NULL COMMENT '发件邮箱账号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统全局设置表';

-- ----------------------------
-- Table structure for user_favorites
-- ----------------------------
DROP TABLE IF EXISTS `user_favorites`;
CREATE TABLE `user_favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID，主键，自增',
  `content_cover` varchar(500) DEFAULT NULL COMMENT '内容封面（冗余字段，便于查询）',
  `content_id` bigint NOT NULL COMMENT '内容ID（电影ID、漫画ID、小说ID等）',
  `content_title` varchar(200) DEFAULT NULL COMMENT '内容标题（冗余字段，便于查询）',
  `content_type` enum('MOVIE','MANGA','NOVEL') NOT NULL COMMENT '内容类型：MOVIE-电影，MANGA-漫画，NOVEL-小说',
  `created_at` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `sort_order` int NOT NULL COMMENT '排序权重，数字越大越靠前',
  `status` enum('ACTIVE','INACTIVE') NOT NULL COMMENT '状态：ACTIVE-有效，INACTIVE-无效',
  `updated_at` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_content` (`user_id`,`content_id`,`content_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏表';

-- ----------------------------
-- Table structure for user_movie_purchases
-- ----------------------------
DROP TABLE IF EXISTS `user_movie_purchases`;
CREATE TABLE `user_movie_purchases` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购买记录ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `created_at` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `movie_id` bigint NOT NULL COMMENT '电影ID',
  `order_id` varchar(64) DEFAULT NULL COMMENT '订单ID',
  `payment_method` enum('BALANCE','WECHAT','ALIPAY') NOT NULL COMMENT '支付方式：BALANCE-余额，WECHAT-微信，ALIPAY-支付宝',
  `purchase_time` datetime(6) DEFAULT NULL COMMENT '购买时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `status` enum('SUCCESS','FAILED') DEFAULT NULL COMMENT '状态：SUCCESS-成功，FAILED-失败',
  `updated_at` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `FKgukdkjjkiqw8cf3pxtjmbyp31` (`movie_id`),
  KEY `FKiv1a9apbrorqo2t3ew5jeg6ys` (`user_id`),
  CONSTRAINT `FKgukdkjjkiqw8cf3pxtjmbyp31` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
  CONSTRAINT `FKiv1a9apbrorqo2t3ew5jeg6ys` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户电影购买记录表';

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键，自增',
  `username` varchar(255) NOT NULL COMMENT '用户名，唯一，不可为空',
  `password` varchar(255) NOT NULL COMMENT '密码，加密存储，不可为空',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱地址，唯一，可为空',
  `phone_number` varchar(20) DEFAULT NULL COMMENT '手机号，最大长度20，可为空',
  `avatar` varchar(200) DEFAULT NULL COMMENT '用户头像URL，最大长度200，可为空',
  `is_vip` tinyint(1) DEFAULT '0' COMMENT '是否为VIP用户',
  `vip_expire_time` datetime DEFAULT NULL COMMENT 'VIP过期时间',
  `account_balance` decimal(10,2) DEFAULT '0.00' COMMENT '账户余额',
  `watch_time` bigint DEFAULT '0' COMMENT '观看时长（分钟）',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP地址',
  `is_locked` tinyint(1) DEFAULT '0' COMMENT '账户是否被锁定',
  `lock_reason` varchar(200) DEFAULT NULL COMMENT '账户锁定原因',
  `failed_login_attempts` int DEFAULT '0' COMMENT '连续登录失败次数',
  `last_failed_login_time` datetime DEFAULT NULL COMMENT '最后一次登录失败时间',
  `lock_time` datetime DEFAULT NULL COMMENT '账户锁定时间',
  `unlock_time` datetime DEFAULT NULL COMMENT '账户自动解锁时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `status` enum('ACTIVE','INACTIVE','LOCKED','DELETED') NOT NULL COMMENT '用户状态：ACTIVE-正常，INACTIVE-未激活，LOCKED-锁定，DELETED-已删除',
  `role` enum('ADMIN','USER','VIP') NOT NULL COMMENT '用户角色：ADMIN-管理员，USER-普通用户，VIP-VIP用户',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- ----------------------------
-- Table structure for vip_packages
-- ----------------------------
DROP TABLE IF EXISTS `vip_packages`;
CREATE TABLE `vip_packages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '套餐ID，主键，自增',
  `name` varchar(50) NOT NULL COMMENT '套餐名称，如月卡、年卡',
  `duration_days` int NOT NULL COMMENT '套餐时长（天）',
  `price` decimal(10,2) NOT NULL COMMENT '套餐价格，单位元',
  `description` varchar(200) DEFAULT NULL COMMENT '套餐描述',
  `status` enum('ACTIVE','INACTIVE','DELETED') NOT NULL COMMENT '状态：ACTIVE-上架，INACTIVE-下架，DELETED-已删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='VIP套餐表';

SET FOREIGN_KEY_CHECKS = 1;
