/**
  DEMO样例表（采用微服务启动，如果需要使用演示样例需要创建该库）
  根据个人使用场景选择是否初始化
 */
DROP DATABASE IF EXISTS `mf_demo`;
CREATE DATABASE  `mf_demo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_demo`;

-- ----------------------------
-- Table structure for demo_data_scope
-- ----------------------------
DROP TABLE IF EXISTS `demo_data_scope`;
CREATE TABLE `demo_data_scope`  (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `role_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色id',
                                    `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
                                    `org_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织id',
                                    `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
                                    `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                                    `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of demo_data_scope
-- ----------------------------
INSERT INTO `demo_data_scope` VALUES (1, NULL, NULL, '1', '系统默认', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (2, NULL, NULL, '404542be62d21014451808aa67f1f5df', '南京XXX公司', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (3, NULL, NULL, '45ee8344ffb942d6f2ee1f868b584a99', 'XXX运维部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (4, NULL, NULL, '624edadc0562c7fe9b54f0e3ba5e658b', '摸鱼开发部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (5, NULL, NULL, '76f3d1d15272858c2523f287aea955f9', '摸鱼测试部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (6, NULL, NULL, '8b7bbf1cac1af7faca6dd2686981fb22', '摸鱼事业部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (7, NULL, NULL, 'a2569f85d5cd316f5c7011e43dc86271', 'XXX开发部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (8, NULL, NULL, 'af30f104a622b5de76add210dd33b361', '摸鱼运维部', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (9, NULL, '1', NULL, '系统默认', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (11, NULL, 'a480b6861ca4a44631af794a99e77265', NULL, '南京XXX公司', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (12, '1', NULL, NULL, '超级管理员', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (13, '210297727b74ecb505c1b4d97f76daee', NULL, NULL, '测试', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (14, '351591b8df6f3eed5d1c613aac6c5bc8', NULL, NULL, 'XXX管理员', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (15, '4063404b06e967bcf619bf86e7fe6359', NULL, NULL, 'XXX运维', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (16, '4b423f7b1ac0ed0b46a8e5ec3389ac14', NULL, NULL, '管理', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (17, '67e95f5e81b8da9a8f70db7540b7409d', NULL, NULL, '运维', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (18, '86ec59a2a3261fd6ba4098da034965ad', NULL, NULL, 'XXX开发', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (19, 'a787082d9b7c177439a114995e4caff1', NULL, NULL, '开发', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (20, 'e77a579aaf0844ba493cde1811b137a9', NULL, NULL, '个人', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (21, '4b423f7b1ac0ed0b46a8e5ec3389ac14', '1', NULL, '租户下的管理', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (22, '4b423f7b1ac0ed0b46a8e5ec3389ac14', 'a480b6861ca4a44631af794a99e77265', NULL, '租户下的管理', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (23, '1', '1', NULL, '租户下超级管理员', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (24, '1', 'a480b6861ca4a44631af794a99e77265', NULL, '租户下超级管理员', '', NULL, '', NULL);
INSERT INTO `demo_data_scope` VALUES (25, '351591b8df6f3eed5d1c613aac6c5bc8', 'a480b6861ca4a44631af794a99e77265', NULL, '租户下XXX管理员', '', NULL, '', NULL);

-- ----------------------------
-- Table structure for demo_import_export
-- ----------------------------
DROP TABLE IF EXISTS `demo_import_export`;
CREATE TABLE `demo_import_export`  (
                                       `id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单ID',
                                       `user_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
                                       `user_phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '联系电话',
                                       `user_address` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '收货地址',
                                       `order_status` tinyint(1) NULL DEFAULT NULL COMMENT '订单状态',
                                       `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总价',
                                       `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
                                       `express_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '快递费用',
                                       `order_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单描述',
                                       `trade_no` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付流水',
                                       `pay_type` tinyint(1) NULL DEFAULT NULL COMMENT '支付类型',
                                       `pay_cert` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付凭证',
                                       `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                                       `delivery_type` tinyint(1) NULL DEFAULT NULL COMMENT '配送方式',
                                       `confirm_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
                                       `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                                       `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                                       `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '导入导出Demo' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of demo_import_export
-- ----------------------------
INSERT INTO `demo_import_export` VALUES ('O16093782342730001', '金XX', '13851883538', 'XXX光里社XXXXXXX', 3, 117.30, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2020-12-31 09:30:34', '', '2021-01-16 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16093788550160001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 4, 168.40, 168.40, 0.00, NULL, NULL, 0, NULL, '2020-12-31 09:41:39', 0, NULL, '', '2020-12-31 09:40:55', '', '2020-12-31 10:30:00');
INSERT INTO `demo_import_export` VALUES ('O16093788565220001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 4, 168.40, 168.40, 0.00, NULL, NULL, 0, NULL, '2020-12-31 09:40:56', 0, NULL, '', '2020-12-31 09:40:56', '', '2020-12-31 10:30:00');
INSERT INTO `demo_import_export` VALUES ('O16093791675560001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 3, 168.40, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2020-12-31 09:46:07', '', '2021-01-07 12:31:04');
INSERT INTO `demo_import_export` VALUES ('O16094872427670001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 33.60, 20.16, 0.00, NULL, NULL, 1, NULL, '2021-01-01 15:47:22', 0, NULL, '', '2021-01-01 15:47:22', '', '2021-01-17 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16094879186140001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 20.00, 12.00, 0.00, NULL, NULL, 1, NULL, '2021-01-01 15:58:38', 0, NULL, '', '2021-01-01 15:58:38', '', '2021-01-17 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16094880739450001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 24.20, 14.52, 0.00, NULL, NULL, 1, NULL, '2021-01-01 16:01:14', 0, NULL, '', '2021-01-01 16:01:13', '', '2021-01-17 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097258989420001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 77.80, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 10:04:58', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097266717440001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 24.50, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 10:17:51', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097340136770001', '候XX', '18801588030', 'XXX花街道XXXXXXX', 3, 404.20, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 12:20:13', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097378326300001', '候XX', '18801588030', 'XXX花街道XXXXXXX', 3, 31.00, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 13:23:52', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097419847650001', '黑XX', '15105181188', 'XXX苏省南XXXXXXX', 3, 210.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-04 14:33:04', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16097456825210001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 26.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 15:34:42', '', '2021-01-20 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098118603040001', '蔡XX', '13918885385', 'XXX苏省南XXXXXXX', 3, 244.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 09:57:40', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098122518450001', '蔡XX', '18388838889', 'XXX苏省南XXXXXXX', 3, 252.70, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:04:11', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098137908580001', '毛XX', '13818038589', 'XXX苏省南XXXXXXX', 3, 512.10, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:29:50', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098154108420001', '沈XX', '13951951893', 'XXX苏省南XXXXXXX', 3, 338.30, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:56:50', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098160954260001', '李XX', '13951088900', 'XXX苏省南XXXXXXX', 3, 255.50, 0.50, 0.00, NULL, NULL, 1, NULL, '2021-01-05 11:19:45', 2, NULL, '', '2021-01-05 11:08:15', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098165908470001', '李XX', '13951088900', 'XXX苏省南XXXXXXX', 3, 239.50, 239.50, 0.00, NULL, NULL, 1, NULL, '2021-01-05 11:16:30', 2, NULL, '', '2021-01-05 11:16:30', '', '2021-01-21 01:00:00');
INSERT INTO `demo_import_export` VALUES ('O16098343426360001', '许XX', '15150588888', 'XXX翔雅苑XXXXXXX', 3, 1245.40, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-05 16:12:22', '', '2021-01-21 01:00:00');

-- ----------------------------
-- Table structure for demo_order
-- ----------------------------
DROP TABLE IF EXISTS `demo_order`;
CREATE TABLE `demo_order`  (
                               `id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单ID',
                               `user_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
                               `user_phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '联系电话',
                               `user_address` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '收货地址',
                               `order_status` tinyint(1) NULL DEFAULT NULL COMMENT '订单状态',
                               `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总价',
                               `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
                               `express_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '快递费用',
                               `order_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单描述',
                               `trade_no` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付流水',
                               `pay_type` tinyint(1) NULL DEFAULT NULL COMMENT '支付类型',
                               `pay_cert` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付凭证',
                               `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                               `delivery_type` tinyint(1) NULL DEFAULT NULL COMMENT '配送方式',
                               `confirm_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
                               `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                               `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '导入导出Demo' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of demo_order
-- ----------------------------
INSERT INTO `demo_order` VALUES ('O16093782342730001', '金XX', '13851883538', 'XXX光里社XXXXXXX', 3, 117.30, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2020-12-31 09:30:34', '', '2021-01-16 01:00:00');
INSERT INTO `demo_order` VALUES ('O16093788550160001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 4, 168.40, 168.40, 0.00, NULL, NULL, 0, NULL, '2020-12-31 09:41:39', 0, NULL, '', '2020-12-31 09:40:55', '', '2020-12-31 10:30:00');
INSERT INTO `demo_order` VALUES ('O16093788565220001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 4, 168.40, 168.40, 0.00, NULL, NULL, 0, NULL, '2020-12-31 09:40:56', 0, NULL, '', '2020-12-31 09:40:56', '', '2020-12-31 10:30:00');
INSERT INTO `demo_order` VALUES ('O16093791675560001', '高XX', '15380890081', 'XXX光里社XXXXXXX', 3, 168.40, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2020-12-31 09:46:07', '', '2021-01-07 12:31:04');
INSERT INTO `demo_order` VALUES ('O16094872427670001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 33.60, 20.16, 0.00, NULL, NULL, 1, NULL, '2021-01-01 15:47:22', 0, NULL, '', '2021-01-01 15:47:22', '', '2021-01-17 01:00:00');
INSERT INTO `demo_order` VALUES ('O16094879186140001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 20.00, 12.00, 0.00, NULL, NULL, 1, NULL, '2021-01-01 15:58:38', 0, NULL, '', '2021-01-01 15:58:38', '', '2021-01-17 01:00:00');
INSERT INTO `demo_order` VALUES ('O16094880739450001', '张XX', '13951858098', 'XXX光里社XXXXXXX', 3, 24.20, 14.52, 0.00, NULL, NULL, 1, NULL, '2021-01-01 16:01:14', 0, NULL, '', '2021-01-01 16:01:13', '', '2021-01-17 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097258989420001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 77.80, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 10:04:58', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097266717440001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 24.50, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 10:17:51', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097340136770001', '候XX', '18801588030', 'XXX花街道XXXXXXX', 3, 404.20, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 12:20:13', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097378326300001', '候XX', '18801588030', 'XXX花街道XXXXXXX', 3, 31.00, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 13:23:52', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097419847650001', '黑XX', '15105181188', 'XXX苏省南XXXXXXX', 3, 210.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-04 14:33:04', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16097456825210001', '吴XX', '13818098030', 'XXX光里社XXXXXXX', 3, 26.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-04 15:34:42', '', '2021-01-20 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098118603040001', '蔡XX', '13918885385', 'XXX苏省南XXXXXXX', 3, 244.90, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 09:57:40', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098122518450001', '蔡XX', '18388838889', 'XXX苏省南XXXXXXX', 3, 252.70, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:04:11', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098137908580001', '毛XX', '13818038589', 'XXX苏省南XXXXXXX', 3, 512.10, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:29:50', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098154108420001', '沈XX', '13951951893', 'XXX苏省南XXXXXXX', 3, 338.30, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 2, NULL, '', '2021-01-05 10:56:50', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098160954260001', '李XX', '13951088900', 'XXX苏省南XXXXXXX', 3, 255.50, 0.50, 0.00, NULL, NULL, 1, NULL, '2021-01-05 11:19:45', 2, NULL, '', '2021-01-05 11:08:15', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098165908470001', '李XX', '13951088900', 'XXX苏省南XXXXXXX', 3, 239.50, 239.50, 0.00, NULL, NULL, 1, NULL, '2021-01-05 11:16:30', 2, NULL, '', '2021-01-05 11:16:30', '', '2021-01-21 01:00:00');
INSERT INTO `demo_order` VALUES ('O16098343426360001', '许XX', '15150588888', 'XXX翔雅苑XXXXXXX', 3, 1245.40, 0.00, 0.00, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2021-01-05 16:12:22', '', '2021-01-21 01:00:00');

-- ----------------------------
-- Table structure for demo_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `demo_order_detail`;
CREATE TABLE `demo_order_detail`  (
                                      `id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '明细主键',
                                      `order_id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单号',
                                      `goods_name` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                      `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品货品图片或者商品图片',
                                      `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品单价',
                                      `goods_price_pro` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品原价',
                                      `goods_count` smallint NULL DEFAULT NULL COMMENT '购买数量',
                                      `send_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
                                      `receive_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
                                      `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际支付金额',
                                      `discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品优惠总金额',
                                      `coupon_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠券扣减金额',
                                      `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                                      `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '销售订单明细' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of demo_order_detail
-- ----------------------------
INSERT INTO `demo_order_detail` VALUES ('08776afd4e4d11eb820300163e11f4a0', 'O16097378326300001', '天堂牌桂花莲子西湖藕粉 570克/袋', 'https://www.ecishan.com.cn/storage/681108-1.png', 31.00, 31.00, 1, NULL, NULL, 0.00, 31.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('0c0f30724e3311eb820300163e11f4a0', 'O16097266717440001', '李锦记草菇老抽1.75升/桶', 'https://www.ecishan.com.cn/storage/987076-1.png', 24.50, 24.50, 1, NULL, NULL, 0.00, 24.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c0fa4e4411eb820300163e11f4a0', 'O16097340136770001', '高露洁冰霜冰凉薄荷牙膏180克/支', 'https://www.ecishan.com.cn/storage/1141501-1.png', 13.80, 13.80, 1, NULL, NULL, 0.00, 13.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c12b4e4411eb820300163e11f4a0', 'O16097340136770001', '玉棠白砂糖1kg/袋', 'https://www.ecishan.com.cn/storage/file16057821768520001.png', 10.90, 10.90, 1, NULL, NULL, 0.00, 10.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c13c4e4411eb820300163e11f4a0', 'O16097340136770001', '淮牌低钠盐400克/袋', 'https://www.ecishan.com.cn/storage/1848905-1.png', 2.70, 2.70, 3, NULL, NULL, 0.00, 8.10, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1484e4411eb820300163e11f4a0', 'O16097340136770001', '黑人超白牙膏190克/支', 'https://www.ecishan.com.cn/storage/20200324105431.jpg', 14.20, 14.20, 1, NULL, NULL, 0.00, 14.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c15a4e4411eb820300163e11f4a0', 'O16097340136770001', '黑人茶倍健牙膏 190克/支', 'https://www.ecishan.com.cn/storage/20200324105600.jpg', 14.50, 14.50, 1, NULL, NULL, 0.00, 14.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1654e4411eb820300163e11f4a0', 'O16097340136770001', '金沙河富强高筋小麦粉 5千克/袋', 'https://www.ecishan.com.cn/storage/20200402131140.jpg', 31.70, 31.70, 1, NULL, NULL, 0.00, 31.70, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1704e4411eb820300163e11f4a0', 'O16097340136770001', '燕庄黑芝麻香油 160毫升/瓶', 'https://www.ecishan.com.cn/storage/20191015154514.png', 18.30, 18.30, 1, NULL, NULL, 0.00, 18.30, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c17a4e4411eb820300163e11f4a0', 'O16097340136770001', '丹玉镇江香醋(一级) 550毫升/瓶', 'https://www.ecishan.com.cn/storage/20200318115045.jpg', 8.50, 8.50, 1, NULL, NULL, 0.00, 8.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1864e4411eb820300163e11f4a0', 'O16097340136770001', '玉兰油美白润肤霜50g/瓶', 'https://www.ecishan.com.cn/storage/file16025749211490001.png', 69.00, 69.00, 2, NULL, NULL, 0.00, 138.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1934e4411eb820300163e11f4a0', 'O16097340136770001', '丝宝宝无根肉厚黑木耳 160克/袋', 'https://www.ecishan.com.cn/storage/file16026434707580001.png', 39.80, 39.80, 1, NULL, NULL, 0.00, 39.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c19d4e4411eb820300163e11f4a0', 'O16097340136770001', '太古单晶冰糖 300g/袋', 'https://www.ecishan.com.cn/storage/file16027396632980001.png', 9.50, 9.50, 1, NULL, NULL, 0.00, 9.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('2431c1a74e4411eb820300163e11f4a0', 'O16097340136770001', '鲁花压榨葵花仁油5升/瓶', 'https://www.ecishan.com.cn/storage/file16049105729540001.png', 96.90, 96.90, 1, NULL, NULL, 0.00, 96.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('281ae28e4c0711eb820300163e11f4a0', 'O16094879186140001', '汤达人日式豚骨拉面55克/杯', 'https://www.ecishan.com.cn/storage/file16039393209460001.png', 5.00, 5.00, 4, NULL, NULL, 12.00, 8.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3921ff044b0911eb820300163e11f4a0', 'O16093788550160001', '雕牌超效加酶无磷洗衣粉2.68千克/袋', 'https://www.ecishan.com.cn/storage/714991-1.png', 24.30, 24.30, 2, NULL, NULL, 48.60, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3921ff394b0911eb820300163e11f4a0', 'O16093788550160001', '福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 2, NULL, NULL, 119.80, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3a07a7ce4b0911eb820300163e11f4a0', 'O16093788565220001', '雕牌超效加酶无磷洗衣粉2.68千克/袋', 'https://www.ecishan.com.cn/storage/714991-1.png', 24.30, 24.30, 2, NULL, NULL, 48.60, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3a07a7fb4b0911eb820300163e11f4a0', 'O16093788565220001', '福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 2, NULL, NULL, 119.80, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('3f6f0ee84e3111eb820300163e11f4a0', 'O16097258989420001', '福临门东北优质大米', 'https://www.ecishan.com.cn/storage/file16024886579790001.jpg', 38.90, 38.90, 2, NULL, NULL, 0.00, 77.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('40baccd04f0311eb820300163e11f4a0', 'O16098160954260001', '【物流配送仅至楼下】金健东北水晶米 5kg/袋', 'https://www.ecishan.com.cn/storage/20200325112026.jpg', 39.10, 39.10, 1, NULL, NULL, 0.08, 39.02, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('40bacd034f0311eb820300163e11f4a0', 'O16098160954260001', '【物流配送仅至楼下】福临门东北优质大米', 'https://www.ecishan.com.cn/storage/file16024886579790001.jpg', 38.90, 38.90, 2, NULL, NULL, 0.15, 77.65, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('40bacd0f4f0311eb820300163e11f4a0', 'O16098160954260001', '【物流配送仅至楼下】金沙河富强高筋小麦粉 2.5千克/袋', 'https://www.ecishan.com.cn/storage/file16027314720870001.png', 18.80, 18.80, 1, NULL, NULL, 0.04, 18.76, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('40bacd1a4f0311eb820300163e11f4a0', 'O16098160954260001', '【物流配送仅至楼下】福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 2, NULL, NULL, 0.23, 119.57, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5944efa11eb820300163e11f4a0', 'O16098122518450001', '洽洽山核桃味瓜子 108克/袋', 'https://www.ecishan.com.cn/storage/20200324095821.jpg', 6.50, 6.50, 5, NULL, NULL, 0.00, 32.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5ce4efa11eb820300163e11f4a0', 'O16098122518450001', '中华健齿白清新薄荷味牙膏 200克/支', 'https://www.ecishan.com.cn/storage/20200324104151.jpg', 7.10, 7.10, 5, NULL, NULL, 0.00, 35.50, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5de4efa11eb820300163e11f4a0', 'O16098122518450001', '上海防酸牙膏 178克/支', 'https://www.ecishan.com.cn/storage/20191028104552.jpg', 5.00, 5.00, 2, NULL, NULL, 0.00, 10.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5e94efa11eb820300163e11f4a0', 'O16098122518450001', '洽洽小而香西瓜子奶油味 180克/袋', 'https://www.ecishan.com.cn/storage/20191128093507.jpg', 14.10, 14.10, 1, NULL, NULL, 0.00, 14.10, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5f44efa11eb820300163e11f4a0', 'O16098122518450001', '洽洽香瓜子 380克/袋', 'https://www.ecishan.com.cn/storage/20191021101330.png', 17.90, 17.90, 1, NULL, NULL, 0.00, 17.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a5fe4efa11eb820300163e11f4a0', 'O16098122518450001', '华味亨纸皮核桃 225克/袋', 'https://www.ecishan.com.cn/storage/20191021101625.png', 23.70, 23.70, 1, NULL, NULL, 0.00, 23.70, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4dc6a6094efa11eb820300163e11f4a0', 'O16098122518450001', '【66折】洽洽每日坚果礼盒30日装', 'https://www.ecishan.com.cn/storage/file16088578690590001.jpg', 119.00, 119.00, 1, NULL, NULL, 0.00, 119.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('4f5d98f94e5f11eb820300163e11f4a0', 'O16097456825210001', '旺旺仙贝经济装', 'https://www.ecishan.com.cn/storage/file16023185382890001.jpg', 26.90, 26.90, 1, NULL, NULL, 0.00, 26.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('646631954ef911eb820300163e11f4a0', 'O16098118603040001', '洽洽山核桃味瓜子 108克/袋', 'https://www.ecishan.com.cn/storage/20200324095821.jpg', 6.50, 6.50, 4, NULL, NULL, 0.00, 26.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('646631c64ef911eb820300163e11f4a0', 'O16098118603040001', '【87折】费列罗榛果威化巧克力（30粒装）375g/盒', 'https://www.ecishan.com.cn/storage/file16087866486720001.png', 99.90, 99.90, 1, NULL, NULL, 0.00, 99.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('646631d14ef911eb820300163e11f4a0', 'O16098118603040001', '【66折】洽洽每日坚果礼盒30日装', 'https://www.ecishan.com.cn/storage/file16088578690590001.jpg', 119.00, 119.00, 1, NULL, NULL, 0.00, 119.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('680527214f0411eb820300163e11f4a0', 'O16098165908470001', '【物流配送仅至楼下】香满园美味富强小麦粉 5千克/袋', 'https://www.ecishan.com.cn/storage/20191015143706.png', 29.90, 29.90, 2, NULL, NULL, 59.80, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('680527544f0411eb820300163e11f4a0', 'O16098165908470001', '【物流配送仅至楼下】福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 3, NULL, NULL, 179.70, 0.00, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('84b0ed0f4c0711eb820300163e11f4a0', 'O16094880739450001', '洁云绒触感抽取式面巾S纤巧装', 'https://www.ecishan.com.cn/storage/20191028095402.jpg', 12.10, 12.10, 2, NULL, NULL, 14.52, 9.68, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('9544cdbf4c0511eb820300163e11f4a0', 'O16094872427670001', '香飘飘蓝莓奶茶 76克/杯', 'https://www.ecishan.com.cn/storage/file16040201903970001.png', 5.60, 5.60, 6, NULL, NULL, 20.16, 13.44, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af974e4f0111eb820300163e11f4a0', 'O16098154108420001', '金达日美锅铲', 'https://www.ecishan.com.cn/storage/20191022103807.jpg', 30.10, 30.10, 2, NULL, NULL, 0.00, 60.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af978b4f0111eb820300163e11f4a0', 'O16098154108420001', '太太乐蔬之鲜调味包 400克/包', 'https://www.ecishan.com.cn/storage/20191015152759.png', 15.90, 15.90, 1, NULL, NULL, 0.00, 15.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97984f0111eb820300163e11f4a0', 'O16098154108420001', '特种兵生榨椰子汁植物蛋白饮料 245克*12罐/箱', 'https://www.ecishan.com.cn/storage/20191018104621.png', 48.60, 48.60, 1, NULL, NULL, 0.00, 48.60, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97a54f0111eb820300163e11f4a0', 'O16098154108420001', '丝宝宝香菇150克/袋', 'https://www.ecishan.com.cn/storage/770796-1.png', 37.80, 37.80, 1, NULL, NULL, 0.00, 37.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97b04f0111eb820300163e11f4a0', 'O16098154108420001', '【物流配送仅至楼下】福临门东北优质大米', 'https://www.ecishan.com.cn/storage/file16024886579790001.jpg', 38.90, 38.90, 1, NULL, NULL, 0.00, 38.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97ba4f0111eb820300163e11f4a0', 'O16098154108420001', '丝宝宝无根肉厚黑木耳 160克/袋', 'https://www.ecishan.com.cn/storage/file16026434707580001.png', 39.80, 39.80, 1, NULL, NULL, 0.00, 39.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97c44f0111eb820300163e11f4a0', 'O16098154108420001', '多力芥花油 5l/瓶', 'https://www.ecishan.com.cn/storage/file16027269216550001.png', 83.90, 83.90, 1, NULL, NULL, 0.00, 83.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('a8af97d04f0111eb820300163e11f4a0', 'O16098154108420001', '双灯高级平版卫生纸 420张/包', 'https://www.ecishan.com.cn/storage/file16039555429720001.png', 6.60, 6.60, 2, NULL, NULL, 0.00, 13.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('b3550d5d4e5611eb820300163e11f4a0', 'O16097419847650001', '特种兵生榨椰子汁植物蛋白饮料 245克*12罐/箱', 'https://www.ecishan.com.cn/storage/20191018104621.png', 48.60, 48.60, 1, NULL, NULL, 0.00, 48.60, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('b3550d8b4e5611eb820300163e11f4a0', 'O16097419847650001', '蒙牛特仑苏纯牛奶 250ml*12盒/箱', 'https://www.ecishan.com.cn/storage/file16028316318400001.png', 65.40, 65.40, 1, NULL, NULL, 0.00, 65.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('b3550d9a4e5611eb820300163e11f4a0', 'O16097419847650001', '鲁花压榨葵花仁油5升/瓶', 'https://www.ecishan.com.cn/storage/file16049105729540001.png', 96.90, 96.90, 1, NULL, NULL, 0.00, 96.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985864f2d11eb820300163e11f4a0', 'O16098343426360001', '康师傅经典红烧牛肉面量贩装5包入', 'https://www.ecishan.com.cn/storage/1032211-1.png', 12.30, 12.30, 8, NULL, NULL, 0.00, 98.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985c04f2d11eb820300163e11f4a0', 'O16098343426360001', '康师傅老坛酸菜牛肉面量贩装5包入', 'https://www.ecishan.com.cn/storage/1061365-1.png', 12.30, 12.30, 8, NULL, NULL, 0.00, 98.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985ce4f2d11eb820300163e11f4a0', 'O16098343426360001', '王府煮花生 560克/袋', 'https://www.ecishan.com.cn/storage/20191021102626.png', 19.10, 19.10, 22, NULL, NULL, 0.00, 420.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985dd4f2d11eb820300163e11f4a0', 'O16098343426360001', '双汇王中王优级火腿肠400g', 'https://www.ecishan.com.cn/storage/20200318105550.jpg', 19.90, 19.90, 12, NULL, NULL, 0.00, 238.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985e84f2d11eb820300163e11f4a0', 'O16098343426360001', '乐事马铃薯片（美国经典原味）75克/包', 'https://www.ecishan.com.cn/storage/file16023203489860001.jpg', 6.40, 6.40, 8, NULL, NULL, 0.00, 51.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985f24f2d11eb820300163e11f4a0', 'O16098343426360001', '乐事马铃薯片（黄瓜味）75克/包', 'https://www.ecishan.com.cn/storage/file16023209673510001.jpg', 6.40, 6.40, 8, NULL, NULL, 0.00, 51.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('bce985fc4f2d11eb820300163e11f4a0', 'O16098343426360001', '【物流配送仅至楼下】荟尚东北珍珠米5kg/袋', 'https://www.ecishan.com.cn/storage/file16027286764320001.png', 35.90, 35.90, 8, NULL, NULL, 0.00, 287.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('c724864a4b0711eb820300163e11f4a0', 'O16093782342730001', '金健东北水晶米 5kg/袋', 'https://www.ecishan.com.cn/storage/20200325112026.jpg', 39.10, 39.10, 3, NULL, NULL, 0.00, 117.30, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c64b4efd11eb820300163e11f4a0', 'O16098137908580001', '李锦记纯香芝麻油 410毫升/瓶', 'https://www.ecishan.com.cn/storage/20191015154621.png', 27.60, 27.60, 1, NULL, NULL, 0.00, 27.60, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c67f4efd11eb820300163e11f4a0', 'O16098137908580001', '海天鲜味生抽 1.9升/桶', 'https://www.ecishan.com.cn/storage/20191015153445.png', 18.80, 18.80, 1, NULL, NULL, 0.00, 18.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c68c4efd11eb820300163e11f4a0', 'O16098137908580001', '52度绵竹大曲 500ml/瓶', 'https://www.ecishan.com.cn/storage/20191021092400.jpg', 8.60, 8.60, 4, NULL, NULL, 0.00, 34.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c6974efd11eb820300163e11f4a0', 'O16098137908580001', '海天草菇老抽 500ml/瓶', 'https://www.ecishan.com.cn/storage/file16026427299100001.png', 9.40, 9.40, 1, NULL, NULL, 0.00, 9.40, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c6a24efd11eb820300163e11f4a0', 'O16098137908580001', '【物流配送仅至楼下】荟尚东北珍珠米5kg/袋', 'https://www.ecishan.com.cn/storage/file16027286764320001.png', 35.90, 35.90, 2, NULL, NULL, 0.00, 71.80, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c6ae4efd11eb820300163e11f4a0', 'O16098137908580001', '鲁花压榨葵花仁油5升/瓶', 'https://www.ecishan.com.cn/storage/file16049105729540001.png', 96.90, 96.90, 1, NULL, NULL, 0.00, 96.90, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('e319c6b94efd11eb820300163e11f4a0', 'O16098137908580001', '【8折】皇冠丹麦曲奇饼干特别礼盒装 1.01kg/盒', 'https://www.ecishan.com.cn/storage/file16087791143760001.png', 126.60, 126.60, 2, NULL, NULL, 0.00, 253.20, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('f36c3a074b0911eb820300163e11f4a0', 'O16093791675560001', '雕牌超效加酶无磷洗衣粉2.68千克/袋', 'https://www.ecishan.com.cn/storage/714991-1.png', 24.30, 24.30, 2, NULL, NULL, 0.00, 48.60, 0.00, '', NULL, '', NULL);
INSERT INTO `demo_order_detail` VALUES ('f36c3a2f4b0911eb820300163e11f4a0', 'O16093791675560001', '福临门苏软香 10kg/袋', 'https://www.ecishan.com.cn/storage/file16049116832980001.png', 59.90, 59.90, 2, NULL, NULL, 0.00, 119.80, 0.00, '', NULL, '', NULL);

SET FOREIGN_KEY_CHECKS = 1;
