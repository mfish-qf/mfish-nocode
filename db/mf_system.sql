/**
  系统相关表（采用微服务启动，需要创建该库）
 */
DROP DATABASE IF EXISTS `mf_system`;
CREATE DATABASE  `mf_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_system`;

-- ----------------------------
-- Table structure for sys_code_build
-- ----------------------------
DROP TABLE IF EXISTS `sys_code_build`;
CREATE TABLE `sys_code_build`  (
                                   `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
                                   `connect_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库连接ID',
                                   `table_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名',
                                   `api_prefix` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口路径前缀 例如:/oauth2/user接口前缀为oauth2(不传会使用packageName，最底层包名 例如:cn.com.mfish.sys包会使用sys)',
                                   `entity_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实体类名(不传会使用表名驼峰化)',
                                   `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目包名(不传使用默认包名 cn.com.mfish.sys)',
                                   `table_comment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表描述(不传会获取数据库表中的中文描述，如果也为空则使用表名)',
                                   `query_params` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Form查询条件(json串)',
                                   `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                                   `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码构建' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_code_build
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
                               `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
                               `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置信息',
                               `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '配置类型(0 样式配置 1表格配置)',
                               `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统皮肤及配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_db_connect
-- ----------------------------
DROP TABLE IF EXISTS `sys_db_connect`;
CREATE TABLE `sys_db_connect`  (
                                   `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '库信息id',
                                   `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '租户ID',
                                   `db_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库标题',
                                   `db_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名',
                                   `db_type` tinyint(1) NULL DEFAULT NULL COMMENT '数据库类型（0 mysql 1 oracle 2 pgsql）',
                                   `pool_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '连接池类型(Druid,Hikari)',
                                   `host` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主机',
                                   `port` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '端口号',
                                   `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库登录用户名',
                                   `password` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库登录密码',
                                   `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源配置项(JSON格式）',
                                   `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                   `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库连接信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
                             `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                             `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
                             `dict_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典编码',
                             `status` int NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
                             `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`, `dict_code`) USING BTREE,
                             UNIQUE INDEX `dict_code_index`(`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('06907475960c4f544ccbe7f883b9387a', '租户状态', 'tenant_corp_status', 0, '租户状态', 'admin', '2023-06-13 17:33:43', '', NULL);
INSERT INTO `sys_dict` VALUES ('0bb92966ec419ed35b9cb46979867e69', '时区', 'sys_time_zone', 0, '所在时区', 'admin', '2023-02-21 18:05:37', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict` VALUES ('193585c2417cfb3e4865501c2e608602', '数据库类型', 'sys_db_type', 0, '支持的数据库类型', 'admin', '2023-01-03 11:45:12', 'admin', '2023-01-03 11:46:12');
INSERT INTO `sys_dict` VALUES ('24f5894e66ca34b9d4aafa2604c94091', '支付类型', 'mall_pay_type', 0, NULL, 'admin', '2024-04-20 18:17:29', 'admin', '2024-04-20 18:17:29');
INSERT INTO `sys_dict` VALUES ('2627066fc33d283c7a1ca4123a31be2d', '请求来源', 'sys_req_source', 0, '请求来源信息', 'admin', '2023-02-02 11:40:32', '', NULL);
INSERT INTO `sys_dict` VALUES ('2fceca32c5098a60071574c61e0327fa', '调度任务状态', 'sys_job_status', 0, '任务执行状态', 'admin', '2023-02-28 16:57:48', '', NULL);
INSERT INTO `sys_dict` VALUES ('3be670398cf94f18c2631ab4df1e714e', '租户类型', 'tenant_corp_type', 0, '租户类型', 'admin', '2023-06-13 17:36:15', '', NULL);
INSERT INTO `sys_dict` VALUES ('4cc4c6a6c90c5534d11ae9d2db12902f', '数据库连接池', 'sys_db_pool', 0, '数据库连接池类型', 'admin', '2023-03-13 11:06:00', '', NULL);
INSERT INTO `sys_dict` VALUES ('55ccc731fe3958c4afca60cbd852a55f', '营业年限', 'tenant_corp_years', 0, '企业营业年限', 'admin', '2023-06-13 17:18:11', 'admin', '2023-06-13 17:33:55');
INSERT INTO `sys_dict` VALUES ('5665dd400700ebea77fcc6f8e39fc355', '请求类型', 'sys_req_type', 0, 'http请求类型', 'admin', '2023-01-09 15:53:31', 'admin', '2023-01-10 17:50:52');
INSERT INTO `sys_dict` VALUES ('5ed7c51e261ce27109e5f4948d40d6e2', 'Hikari连接池配置', 'db_pool_hikari', 0, 'Hikari连接池相关配置信息', 'admin', '2023-03-13 11:18:45', '', NULL);
INSERT INTO `sys_dict` VALUES ('6ddca50d9ddad44806ef18c3bf4721c9', '公式类型', 'nc_formula_type', 0, '对运算公式分类', 'admin', '2023-10-27 09:09:25', 'admin', '2023-10-27 09:09:25');
INSERT INTO `sys_dict` VALUES ('75ce8aab0fca2be5183770260d145c17', '认证方式', 'sso_grant_type', 0, '统一认证grant_type', 'admin', '2023-05-16 21:49:41', '', NULL);
INSERT INTO `sys_dict` VALUES ('81e4b91e932f0a671021a7eca2f665b9', '登录模式', 'sys_login_mode', 0, '登录模式 0 浏览器 1 微信', 'admin', '2023-03-09 14:48:59', '', NULL);
INSERT INTO `sys_dict` VALUES ('8a992d89a43f27fa2dbdf75871bd7e74', '所属行业', 'tenant_corp_trade', 0, '租户所属行业', 'admin', '2023-06-13 17:04:44', 'admin', '2023-06-15 22:32:41');
INSERT INTO `sys_dict` VALUES ('9bb402e2c751b3bf9358ef9acb3caf49', '调度任务', 'sys_job_type', 0, '调度任务类型', 'admin', '2023-02-21 10:44:15', 'admin', '2023-03-13 11:06:05');
INSERT INTO `sys_dict` VALUES ('ad7336dda270e6430565b313a741ffb7', '用户性别', 'sys_user_sex', 0, '用户性别字典', 'admin', '2023-01-04 16:08:35', 'admin', '2023-01-05 14:47:40');
INSERT INTO `sys_dict` VALUES ('c352267b4ec618c1745bdda85f12402e', '组件类型', 'vue_com_type', 0, '前端VUE组件类型', 'admin', '2024-04-19 14:11:33', 'admin', '2024-04-19 14:11:33');
INSERT INTO `sys_dict` VALUES ('c64a91de3cff06e70128938d7cfc2af4', '配送方式', 'mall_delivery_type', 0, NULL, 'admin', '2024-04-20 18:18:52', 'admin', '2024-04-20 18:18:52');
INSERT INTO `sys_dict` VALUES ('c694f0f6feba27026044839b77d24caa', '日志操作', 'sys_log_type', 0, '日志操作类型', 'admin', '2023-01-09 11:27:05', 'admin', '2023-03-13 11:06:13');
INSERT INTO `sys_dict` VALUES ('d2d1bfb80a89199abdedae4874311b69', '订单状态', 'mall_order_status', 0, NULL, 'admin', '2024-04-20 18:11:02', 'admin', '2024-04-20 18:11:02');
INSERT INTO `sys_dict` VALUES ('d42259143fdd344b439fe39d3fffdefe', '企业规模', 'tenant_corp_size', 0, '企业公司规模', 'admin', '2023-06-13 17:10:14', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict` VALUES ('d42d4e365a7b3d43f8c5cf37a523dbd5', 'Druid连接池配置', 'db_pool_druid', 0, 'Druid连接池相关配置项', 'admin', '2023-03-13 11:24:35', '', NULL);
INSERT INTO `sys_dict` VALUES ('dce08f2d436e6582ec58301a76fe11fa', '任务过期策略', 'sys_job_misfire', 0, '任务过期处理策略 1立即处理一次 2放弃处理', 'admin', '2023-02-21 11:22:32', 'admin', '2023-02-21 18:07:12');
INSERT INTO `sys_dict` VALUES ('e8e6e4c3f8e2fb775d6d083883e41839', '代码生成查询条件', 'sys_code_condition', 0, '代码生成的查询条件', 'admin', '2023-05-10 16:11:44', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
                                  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                                  `dict_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典ID',
                                  `dict_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典编码',
                                  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
                                  `dict_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
                                  `value_type` tinyint(1) NULL DEFAULT 0 COMMENT '值类型(0字符 1数字 2布尔)',
                                  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
                                  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
                                  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配色(用于前端显示)',
                                  `status` int NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                                  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `dict_code_index`(`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES ('026de48a5c41ce981d0f941b510120b0', '6ddca50d9ddad44806ef18c3bf4721c9', 'nc_formula_type', '数字运算', 'num_op', 0, 2, NULL, '', 0, '', 'admin', '2023-10-27 09:17:47', 'admin', '2023-10-27 14:21:53');
INSERT INTO `sys_dict_item` VALUES ('027eb9340eafdcf78cc9b96cc211b6b3', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'maxEvictableIdleTimeMillis', '900000', 1, 7, NULL, NULL, 0, '配置一个连接在池中最大生存的时间，单位是毫秒', 'admin', '2023-03-13 11:27:11', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('0475df296a072fdb2584162443049aef', 'c352267b4ec618c1745bdda85f12402e', 'vue_com_type', '下拉选择框', 'ApiSelect', 0, 3, NULL, NULL, 0, NULL, 'admin', '2024-04-19 14:13:55', 'admin', '2024-04-19 14:14:58');
INSERT INTO `sys_dict_item` VALUES ('0625b88c24a5cd98e045a58679370249', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '新增', '新增', 0, 3, NULL, 'blue', 0, NULL, 'admin', '2023-01-09 11:27:52', 'admin', '2023-01-09 16:11:41');
INSERT INTO `sys_dict_item` VALUES ('0737910a2d8f6eb55fe12e2a9be5032b', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '日本/东京', 'Asia/Tokyo', 0, 3, NULL, '', 0, '', 'admin', '2023-02-21 18:09:59', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('07fb6036ee052bc745db8b58234159e9', 'e8e6e4c3f8e2fb775d6d083883e41839', 'sys_code_condition', '以开始', 'likeLeft', 0, 3, NULL, NULL, 0, NULL, 'admin', '2023-05-10 16:13:25', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('0844e38fdb4aabbac30f365812e8457f', '06907475960c4f544ccbe7f883b9387a', 'tenant_corp_status', '正常', '0', 1, 1, NULL, 'green', 0, NULL, 'admin', '2023-06-13 17:34:26', 'admin', '2023-06-13 17:37:06');
INSERT INTO `sys_dict_item` VALUES ('0cc6971a3031faf34525bafae3609f0d', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '中国/香港', 'Asia/Hong_Kong', 0, 2, NULL, '', 0, '', 'admin', '2023-02-21 18:13:04', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('0f11bb06b93b05e27f8a45d78765a243', '193585c2417cfb3e4865501c2e608602', 'sys_db_type', 'mysql', '0', 1, 1, NULL, 'blue', 0, 'mysql数据库', 'admin', '2023-01-03 16:40:55', 'admin', '2023-03-13 11:16:59');
INSERT INTO `sys_dict_item` VALUES ('11ea1ab1605e7c6271a0ccdfb750242e', '8a992d89a43f27fa2dbdf75871bd7e74', 'tenant_corp_trade', '公共设施管理业', '公共设施管理业', 0, 4, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:07:56', 'admin', '2023-06-15 22:32:40');
INSERT INTO `sys_dict_item` VALUES ('1622fbd6504ffe02790b63540b8e693f', 'c352267b4ec618c1745bdda85f12402e', 'vue_com_type', '数字输入框', 'InputNumber', 0, 2, NULL, NULL, 0, NULL, 'admin', '2024-04-19 14:14:54', 'admin', '2024-04-19 14:14:54');
INSERT INTO `sys_dict_item` VALUES ('194522e5912d137781d6c2659d2c7c65', '8a992d89a43f27fa2dbdf75871bd7e74', 'tenant_corp_trade', '科技推广和应用服务业', '科技推广和应用服务业', 0, 2, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:06:31', 'admin', '2023-06-15 22:32:40');
INSERT INTO `sys_dict_item` VALUES ('1f00232f374a51bb9e38df469e95ca59', 'd42259143fdd344b439fe39d3fffdefe', 'tenant_corp_size', '500人以上', 'l', 0, 4, NULL, '', 0, '', 'admin', '2023-06-13 17:12:05', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict_item` VALUES ('2065051b945805f8915ea68431c097ff', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '查询', '查询', 0, 2, NULL, 'green', 0, NULL, 'admin', '2023-01-09 11:27:46', 'admin', '2023-01-09 15:21:07');
INSERT INTO `sys_dict_item` VALUES ('21497ae862c8446d8721d21176fe677e', '2627066fc33d283c7a1ca4123a31be2d', 'sys_req_source', '其他', '0', 1, 3, NULL, 'orange', 0, '其他来源', 'admin', '2023-02-02 11:41:01', 'admin', '2023-02-21 18:00:30');
INSERT INTO `sys_dict_item` VALUES ('2495904b9a026be7eed628988cc584f7', '55ccc731fe3958c4afca60cbd852a55f', 'tenant_corp_years', '三年以内', '3', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:20:46', 'admin', '2023-06-13 17:31:53');
INSERT INTO `sys_dict_item` VALUES ('24e063bd1bd5e0dd6c433682c277d42e', 'dce08f2d436e6582ec58301a76fe11fa', 'sys_job_misfire', '放弃执行', '2', 1, 2, NULL, 'red', 0, '不执行过期任务', 'admin', '2023-02-21 11:23:42', 'admin', '2023-02-21 18:07:12');
INSERT INTO `sys_dict_item` VALUES ('261b1fdc50de63e495cdb757b6d430e2', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '删除', '删除', 0, 5, NULL, 'red', 0, NULL, 'admin', '2023-01-09 11:28:05', 'admin', '2023-01-09 15:23:00');
INSERT INTO `sys_dict_item` VALUES ('294a4554e02894c7dfe306df3051a03e', 'dce08f2d436e6582ec58301a76fe11fa', 'sys_job_misfire', '立即执行', '1', 1, 1, NULL, 'green', 0, '判断为过期后立即执行一次', 'admin', '2023-02-21 11:23:27', 'admin', '2023-02-21 18:07:12');
INSERT INTO `sys_dict_item` VALUES ('29cc4901b0f367b8f0f42582ae64c4c3', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'initialSize', '2', 1, 1, NULL, NULL, 0, '初始连接数', 'admin', '2023-03-13 11:25:14', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('2a0c3b13464ea03b8b9c3a9d07295dde', 'c64a91de3cff06e70128938d7cfc2af4', 'mall_delivery_type', '配送点', '0', 1, 0, NULL, 'green', 0, NULL, 'admin', '2024-04-20 18:19:18', 'admin', '2024-04-20 18:19:43');
INSERT INTO `sys_dict_item` VALUES ('2adbaf3195bbab50fbd100ed6185e513', '2627066fc33d283c7a1ca4123a31be2d', 'sys_req_source', '后台用户', '1', 1, 1, NULL, 'blue', 0, '后台用户', 'admin', '2023-02-02 11:41:19', 'admin', '2023-02-21 18:00:23');
INSERT INTO `sys_dict_item` VALUES ('31552cd65ae98d64265506ef05b00907', '4cc4c6a6c90c5534d11ae9d2db12902f', 'sys_db_pool', 'Hikari', 'db_pool_hikari', 0, 2, NULL, 'green', 0, 'spring默认连接池', 'admin', '2023-03-13 11:11:24', 'admin', '2023-03-17 10:20:23');
INSERT INTO `sys_dict_item` VALUES ('317328ed4f375a6591283f10e0e1cec6', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '美国/纽约', 'America/New_York', 0, 5, NULL, '', 0, '', 'admin', '2023-02-21 18:11:07', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('32438cecdad31c99e71000e3097d266f', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'maxActive', '10', 1, 3, NULL, NULL, 0, '最大连接池数', 'admin', '2023-03-13 11:25:47', 'admin', '2023-03-17 16:27:50');
INSERT INTO `sys_dict_item` VALUES ('327e9856baf76954f9a968c7ccc0eb02', 'c64a91de3cff06e70128938d7cfc2af4', 'mall_delivery_type', '物流', '2', 1, 2, NULL, 'cyan', 0, '', 'admin', '2024-04-20 18:20:00', 'admin', '2024-04-20 18:20:00');
INSERT INTO `sys_dict_item` VALUES ('32a0802dae2dfb32857efed0f4a6663f', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '执行成功', '3', 1, 4, NULL, 'green', 0, '执行成功', 'admin', '2023-03-01 17:59:34', 'admin', '2023-03-01 18:00:11');
INSERT INTO `sys_dict_item` VALUES ('34b308bff053bbff3f82554565e0f8f8', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'maxPoolPreparedStatementPerConnectionSize', '20', 1, 12, NULL, '', 0, '游标缓存大小', 'admin', '2023-03-13 11:30:31', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('3cd57f7e97d9bde410da9f2199dc35c5', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '调度成功', '1', 1, 2, NULL, 'green', 0, NULL, 'admin', '2023-02-28 16:58:32', 'admin', '2023-03-01 17:59:00');
INSERT INTO `sys_dict_item` VALUES ('3cd68c5687d190e7b0a6242b162424a7', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'OPTIONS', 'OPTIONS', 0, 5, NULL, 'cyan', 0, '测试服务器的功能性', 'admin', '2023-01-10 18:01:42', 'admin', '2023-02-21 18:02:30');
INSERT INTO `sys_dict_item` VALUES ('3e34820c81d8a62752188740b22a3ebc', '9bb402e2c751b3bf9358ef9acb3caf49', 'sys_job_type', '本地任务', '0', 1, 1, NULL, 'green', 0, '实现方法写在调度中心的任务', 'admin', '2023-02-21 10:44:46', 'admin', '2023-02-21 17:44:35');
INSERT INTO `sys_dict_item` VALUES ('429ef23795434df72a0800b93399dd58', '9bb402e2c751b3bf9358ef9acb3caf49', 'sys_job_type', '消息任务', '2', 1, 3, NULL, 'orange', 0, '通过MQ消息发送任务，消费端处理', 'admin', '2023-02-21 10:48:33', 'admin', '2023-02-21 17:44:42');
INSERT INTO `sys_dict_item` VALUES ('42c2db7dc772a5d4026a9d5b59348417', '193585c2417cfb3e4865501c2e608602', 'sys_db_type', 'postgresql', '1', 1, 2, NULL, 'cyan', 0, 'pg数据库', 'admin', '2023-03-13 11:03:25', 'admin', '2023-03-24 23:02:04');
INSERT INTO `sys_dict_item` VALUES ('4447b34d5b8ac40fb762fdcd71dfe0b2', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'testOnBorrow', 'false', 2, 9, NULL, '', 0, '当应用向连接池申请连接时，连接池会判断这条连接是否是可用的', 'admin', '2023-03-13 11:28:13', 'admin', '2023-03-14 19:41:21');
INSERT INTO `sys_dict_item` VALUES ('449f7f3ff1bf55ddb8159f5d122e29c4', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '其他操作', '其他操作', 0, 1, NULL, 'default', 0, NULL, 'admin', '2023-01-09 11:27:39', 'admin', '2023-02-21 18:03:01');
INSERT INTO `sys_dict_item` VALUES ('4795a7072f4213eeeee4d8bca2531dc8', 'e8e6e4c3f8e2fb775d6d083883e41839', 'sys_code_condition', '以结束', 'likeRight', 0, 4, NULL, NULL, 0, NULL, 'admin', '2023-05-10 16:13:39', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('4a84be1783171797e3cf1712f014b461', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '中国/上海', 'Asia/Shanghai', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-02-21 18:05:55', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('4f9003f0afecb5a68b22f05d1e1ea0e7', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'testOnReturn', 'false', 2, 10, NULL, '', 0, '归还连接时是否会进行检查，检查不通过，销毁', 'admin', '2023-03-13 11:28:34', 'admin', '2023-03-14 19:44:29');
INSERT INTO `sys_dict_item` VALUES ('515e0a34aec2beea8733d83083c53ea5', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '开始', '0', 1, 1, NULL, 'blue', 0, NULL, 'admin', '2023-02-28 16:58:12', 'admin', '2023-02-28 17:04:30');
INSERT INTO `sys_dict_item` VALUES ('52789011153ea396227659c1bc18d594', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '已废弃', '4', 1, 5, NULL, '#FF4D4F', 0, '', 'admin', '2024-04-20 18:13:34', 'admin', '2024-04-20 18:16:58');
INSERT INTO `sys_dict_item` VALUES ('577d84183407984f104f3b71509623b8', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'testWhileIdle', 'true', 2, 8, NULL, NULL, 0, '当应用向连接池申请连接，并且testOnBorrow为false时，连接池将会判断连接是否处于空闲状态，如果是，则验证这条连接是否可用。', 'admin', '2023-03-13 11:27:33', 'admin', '2023-03-14 19:41:17');
INSERT INTO `sys_dict_item` VALUES ('5bfc739984836091b77b298d9a605181', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'DELETE', 'DELETE', 0, 4, NULL, 'red', 0, '删除', 'admin', '2023-01-09 16:13:42', 'admin', '2023-02-21 18:02:06');
INSERT INTO `sys_dict_item` VALUES ('5c24ec491e462910d3123ac7efffdac2', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '登录', '登录', 0, 9, NULL, 'orange', 0, NULL, 'admin', '2023-01-09 11:28:39', 'admin', '2023-01-09 15:23:43');
INSERT INTO `sys_dict_item` VALUES ('60f7b458a56b372bcd4c8d797faf926e', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '登出', '登出', 0, 10, NULL, 'red', 0, NULL, 'admin', '2023-01-09 11:28:50', 'admin', '2023-01-09 15:23:36');
INSERT INTO `sys_dict_item` VALUES ('645349eb7dc061b452b1229c111eef20', '75ce8aab0fca2be5183770260d145c17', 'sso_grant_type', 'authorization_code', 'authorization_code', 0, 1, NULL, 'green', 0, 'authorization_code回调认证方式', 'admin', '2023-05-16 21:50:21', 'admin', '2024-04-15 10:18:40');
INSERT INTO `sys_dict_item` VALUES ('64bcbb1e944d09c0357e74d121e7f456', '55ccc731fe3958c4afca60cbd852a55f', 'tenant_corp_years', '十年以上', '100', 0, 4, NULL, '', 0, '', 'admin', '2023-06-13 17:22:07', 'admin', '2023-06-13 17:32:02');
INSERT INTO `sys_dict_item` VALUES ('655b8b163d3203409a2ef96ee34aa979', '8a992d89a43f27fa2dbdf75871bd7e74', 'tenant_corp_trade', '软件和信息技术服务业', '软件和信息技术服务业', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:05:33', 'admin', '2023-06-15 22:32:40');
INSERT INTO `sys_dict_item` VALUES ('6c4f687dc49bf0f4e881d5768fd9ed4d', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '修改', '修改', 0, 4, NULL, 'purple', 0, NULL, 'admin', '2023-01-09 11:27:58', 'admin', '2023-01-09 16:11:35');
INSERT INTO `sys_dict_item` VALUES ('6f45f21f466658f51bdbb942f3a679b7', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '导出', '导出', 0, 8, NULL, '#87d068', 0, NULL, 'admin', '2023-01-09 11:28:30', 'admin', '2023-01-09 15:26:03');
INSERT INTO `sys_dict_item` VALUES ('6f56bed8bc66030e416df6b4b5805e6e', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'autoCommit', 'true', 2, 3, NULL, '', 0, '自动提交', 'admin', '2023-03-13 11:21:18', 'admin', '2023-03-14 20:26:24');
INSERT INTO `sys_dict_item` VALUES ('6f685f59734340254967d4dbb9bfdfba', '3be670398cf94f18c2631ab4df1e714e', 'tenant_corp_type', '企业', '1', 1, 2, NULL, 'green', 0, NULL, 'admin', '2023-06-13 17:36:56', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('71ffe8bcad36849000f6e5e5124914ef', '0bb92966ec419ed35b9cb46979867e69', 'sys_time_zone', '英国/伦敦', 'Europe/London', 0, 4, NULL, '', 0, '', 'admin', '2023-02-21 18:10:31', 'admin', '2023-02-21 18:16:33');
INSERT INTO `sys_dict_item` VALUES ('731a23dbfd6ec285c9c81ab2a29b43b1', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '授权', '授权', 0, 6, NULL, 'cyan', 0, NULL, 'admin', '2023-01-09 11:28:16', 'admin', '2023-01-09 16:11:27');
INSERT INTO `sys_dict_item` VALUES ('738f704265393a8ac4cb5c536a5bc6e4', '4cc4c6a6c90c5534d11ae9d2db12902f', 'sys_db_pool', '无连接池', 'db_no_pool', 0, 1, NULL, 'red', 0, '不使用连接池', 'admin', '2023-03-13 11:12:14', 'admin', '2023-03-17 10:20:17');
INSERT INTO `sys_dict_item` VALUES ('750f4ff9eb1bdce42b04690d0abd929d', '81e4b91e932f0a671021a7eca2f665b9', 'sys_login_mode', '浏览器', '0', 1, 1, NULL, 'blue', 0, 'web浏览器登录', 'admin', '2023-03-09 14:49:31', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('75d8887dbeb5a0ca4d20ea445139e97a', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '导入', '导入', 0, 7, NULL, '#2db7f5', 0, NULL, 'admin', '2023-01-09 11:28:23', 'admin', '2023-01-09 15:25:53');
INSERT INTO `sys_dict_item` VALUES ('77a3eb2b9e104aded10cd904bd7a6de9', 'c64a91de3cff06e70128938d7cfc2af4', 'mall_delivery_type', '自提', '1', 1, 1, NULL, 'blue', 0, NULL, 'admin', '2024-04-20 18:19:38', 'admin', '2024-04-20 18:19:38');
INSERT INTO `sys_dict_item` VALUES ('83bb1fc1addafe499342df7033c957ce', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'minIdle', '1', 1, 2, NULL, NULL, 0, '最小连接池数', 'admin', '2023-03-13 11:25:31', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('86353dd3e85438e846b704c3af0e7726', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'minimumIdle', '2', 1, 1, NULL, NULL, 0, '连接池中允许的最小连接数最小连接数', 'admin', '2023-03-13 11:19:29', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('873f7ffd87720589519d1aded006d13f', '06907475960c4f544ccbe7f883b9387a', 'tenant_corp_status', '注销', '1', 1, 2, NULL, 'red', 0, NULL, 'admin', '2023-06-13 17:34:43', 'admin', '2023-06-13 17:37:10');
INSERT INTO `sys_dict_item` VALUES ('8afe69f996943d66179ff9063248f4f2', 'c352267b4ec618c1745bdda85f12402e', 'vue_com_type', '文本框', 'Input', 0, 1, NULL, NULL, 0, NULL, 'admin', '2024-04-19 14:12:22', 'admin', '2024-04-19 14:12:22');
INSERT INTO `sys_dict_item` VALUES ('8c75eb1f6f1cddafc1e39f53e0195221', '8a992d89a43f27fa2dbdf75871bd7e74', 'tenant_corp_trade', '医药制造业', '医药制造业', 0, 3, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:07:27', 'admin', '2023-06-15 22:32:40');
INSERT INTO `sys_dict_item` VALUES ('8caa80cc0c116e6407caa1efc5c567d5', 'e8e6e4c3f8e2fb775d6d083883e41839', 'sys_code_condition', '等于', 'eq', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-05-10 16:12:10', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('8d0e789e6e63e994b31750ad9cb637b4', '193585c2417cfb3e4865501c2e608602', 'sys_db_type', 'oracle', '2', 1, 3, NULL, 'green', 0, 'oracle数据库', 'admin', '2023-01-04 17:32:59', 'admin', '2023-03-24 23:02:09');
INSERT INTO `sys_dict_item` VALUES ('90bb0f58b82df5d13c66169fd748faf8', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '调度失败', '2', 1, 3, NULL, 'red', 0, NULL, 'admin', '2023-02-28 16:58:45', 'admin', '2023-03-01 17:59:08');
INSERT INTO `sys_dict_item` VALUES ('93a1cdf50fe9f17e399cf663193a7a1f', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'GET', 'GET', 0, 1, NULL, 'green', 0, '查询', 'admin', '2023-01-09 16:10:49', 'admin', '2023-02-21 18:01:56');
INSERT INTO `sys_dict_item` VALUES ('9641bcf7cdd3f2625633d8fb9837f398', '3be670398cf94f18c2631ab4df1e714e', 'tenant_corp_type', '个人', '0', 1, 1, NULL, 'blue', 0, NULL, 'admin', '2023-06-13 17:36:41', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('99c1c3f642ddee2846220373b30d892d', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'validationQuery', 'SELECT 1 FROM DUAL', 0, 15, NULL, '', 0, '配置检测连接是否有效', 'admin', '2023-03-13 11:33:22', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('9b97258d3f744dfa941c1b1137ce7c86', '4cc4c6a6c90c5534d11ae9d2db12902f', 'sys_db_pool', 'Druid', 'db_pool_druid', 0, 3, NULL, 'blue', 0, '阿里德鲁伊连接池', 'admin', '2023-03-13 11:07:07', 'admin', '2023-03-17 10:20:25');
INSERT INTO `sys_dict_item` VALUES ('a1e906c9654de787edafbd05a147eed3', '9bb402e2c751b3bf9358ef9acb3caf49', 'sys_job_type', 'RPC远程任务', '1', 1, 2, NULL, 'blue', 0, 'RPC调度尽量不要调用时间过长的任务，一般用于服务之间简单通知', 'admin', '2023-02-21 10:47:13', 'admin', '2023-02-21 17:44:39');
INSERT INTO `sys_dict_item` VALUES ('a3095db9883574674d2f9fb366f10f52', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'connectionTestQuery', 'SELECT 1', 0, 7, NULL, '', 0, '数据库连接测试语句', 'admin', '2023-03-13 11:23:36', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('a8061138a5828b8fe18d302475928f2b', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'timeBetweenEvictionRunsMillis', '60000', 1, 5, NULL, NULL, 0, '配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒', 'admin', '2023-03-13 11:26:27', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('acbac95a5e0990f96ae6308ff68d693c', '75ce8aab0fca2be5183770260d145c17', 'sso_grant_type', 'refresh_token', 'refresh_token', 0, 3, NULL, 'cyan', 0, 'refresh_token刷新token方式', 'admin', '2023-05-16 21:51:01', 'admin', '2023-05-18 10:20:18');
INSERT INTO `sys_dict_item` VALUES ('ad248d1177e8e8ff9c775a0f2757dfec', '24f5894e66ca34b9d4aafa2604c94091', 'mall_pay_type', '微信', '1', 1, 2, NULL, '#52C41A', 0, '', 'admin', '2024-04-20 18:18:13', 'admin', '2024-04-20 18:18:24');
INSERT INTO `sys_dict_item` VALUES ('afe58b597c6c86edb328ab47e334d329', '24f5894e66ca34b9d4aafa2604c94091', 'mall_pay_type', '支付宝', '0', 1, 1, NULL, '#1677FF', 0, NULL, 'admin', '2024-04-20 18:17:50', 'admin', '2024-04-20 18:18:01');
INSERT INTO `sys_dict_item` VALUES ('b875ec52ef28c8e1c787e0b71a4c3b0f', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '已完成', '3', 1, 4, NULL, '#52C41A', 0, '', 'admin', '2024-04-20 18:13:09', 'admin', '2024-04-20 18:16:18');
INSERT INTO `sys_dict_item` VALUES ('be3c4fd10bb7a6691ca9cc2dc55c038d', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'connectionProperties', 'druid.stat.mergeSql=false;druid.stat.slowSqlMillis=5000', 0, 14, NULL, '', 0, '打开mergeSql功能；慢SQL记录', 'admin', '2023-03-13 11:32:33', 'admin', '2023-03-13 11:32:58');
INSERT INTO `sys_dict_item` VALUES ('bf8ded2aadd49b2822ce6799070f8164', '81e4b91e932f0a671021a7eca2f665b9', 'sys_login_mode', '微信', '1', 1, 2, NULL, 'green', 0, '微信小程序登录', 'admin', '2023-03-09 14:49:57', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('bfc4e183ac3bd15a3cbc9b5cd5116763', '2627066fc33d283c7a1ca4123a31be2d', 'sys_req_source', '手机用户', '2', 1, 2, NULL, 'pink', 0, '手机用户', 'admin', '2023-02-02 11:41:49', 'admin', '2023-02-21 18:00:26');
INSERT INTO `sys_dict_item` VALUES ('c1135debd21467001fd69c21df6c6c10', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'connectionTimeout', '30000', 0, 6, NULL, '', 0, '待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException', 'admin', '2023-03-13 11:23:15', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('c1770d660a44ff2275615a8a329ee6db', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '已支付', '1', 1, 2, NULL, '#ECC12B', 0, NULL, 'admin', '2024-04-20 18:12:07', 'admin', '2024-04-20 18:16:50');
INSERT INTO `sys_dict_item` VALUES ('c9ff23502c59bff661479755f842105b', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'maximumPoolSize', '10', 1, 2, NULL, NULL, 0, '最大连接数，包括闲置和使用中的连接， 如果maxPoolSize小于1，则会被重置，当minIdle0则重置为minIdle的值', 'admin', '2023-03-13 11:20:24', 'admin', '2023-03-17 16:44:36');
INSERT INTO `sys_dict_item` VALUES ('ce8059c1fb3b533339f6bdccc17e0046', 'ad7336dda270e6430565b313a741ffb7', 'sys_user_sex', '男', '1', 1, 1, NULL, 'green', 0, '男性', 'admin', '2023-01-04 17:31:35', 'admin', '2023-01-09 16:09:49');
INSERT INTO `sys_dict_item` VALUES ('cf2899ae2d41fd29b5cef8de005098e7', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'PUT', 'PUT', 0, 3, NULL, 'purple', 0, '修改', 'admin', '2023-01-09 16:12:11', 'admin', '2023-02-21 18:02:03');
INSERT INTO `sys_dict_item` VALUES ('d48596048bd2bd62c5844e4bc5d70208', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'maxWait', '60000', 1, 4, NULL, NULL, 0, '获取连接等待超时的时间', 'admin', '2023-03-13 11:26:06', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('d53cec8854c1a92c33059caffcb6af00', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'maxLifetime', '300000', 1, 5, NULL, '', 0, '一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired）； 缺省:30分钟，建议设置比数据库超时时长少30秒', 'admin', '2023-03-13 11:22:15', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('d563a2b3f99baff573193c81a96bacf6', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'filters', 'stat,wall', 0, 13, NULL, '', 0, '配置监控统计拦截的filters，去掉后监控界面sql无法统计，\'wall\'用于防火墙', 'admin', '2023-03-13 11:31:47', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('d781dcc93f41b65dd5f44bb48a69a57c', '55ccc731fe3958c4afca60cbd852a55f', 'tenant_corp_years', '五年以内', '5', 0, 2, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:21:00', 'admin', '2023-06-13 17:31:57');
INSERT INTO `sys_dict_item` VALUES ('d82a25d50542f74cd2fef5cc7f04e9aa', '6ddca50d9ddad44806ef18c3bf4721c9', 'nc_formula_type', '高级功能', 'advance_op', 0, 4, NULL, NULL, 0, NULL, 'admin', '2023-12-24 16:40:20', 'admin', '2023-12-24 16:40:20');
INSERT INTO `sys_dict_item` VALUES ('df72218dba18e67c91dc2e12493b15ed', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'poolPreparedStatements', 'true', 2, 11, NULL, '', 0, '是否缓存游标', 'admin', '2023-03-13 11:30:08', 'admin', '2023-03-14 20:26:08');
INSERT INTO `sys_dict_item` VALUES ('e18da53336b068c23d1b144e38d122b2', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '待支付', '0', 1, 1, NULL, '#8F8F8F', 0, NULL, 'admin', '2024-04-20 18:11:43', 'admin', '2024-04-20 18:15:52');
INSERT INTO `sys_dict_item` VALUES ('e1ce7b2d2de8f28789a30cce64ebd5d3', 'd42259143fdd344b439fe39d3fffdefe', 'tenant_corp_size', '100到300人', 's', 0, 2, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:11:27', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict_item` VALUES ('e27a2f5ecad5a50a7d788756da095245', 'd42d4e365a7b3d43f8c5cf37a523dbd5', 'db_pool_druid', 'minEvictableIdleTimeMillis', '300000', 1, 6, NULL, NULL, 0, '配置一个连接在池中最小生存的时间，单位是毫秒', 'admin', '2023-03-13 11:26:48', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('e27c1ecf50054eb7e0263d3b9580f469', '6ddca50d9ddad44806ef18c3bf4721c9', 'nc_formula_type', '日期处理', 'date_op', 0, 3, NULL, '', 0, '', 'admin', '2023-10-27 09:19:16', 'admin', '2023-10-27 14:22:00');
INSERT INTO `sys_dict_item` VALUES ('e3431f70eafa566d0aa0ced7a9b11552', 'd42259143fdd344b439fe39d3fffdefe', 'tenant_corp_size', '300到500人', 'm', 0, 3, NULL, '', 0, '', 'admin', '2023-06-13 17:11:48', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict_item` VALUES ('e534f060f0969be49a93254796a2a24b', 'd42259143fdd344b439fe39d3fffdefe', 'tenant_corp_size', '100人以下', 'xs', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-06-13 17:10:45', 'admin', '2023-06-13 17:29:14');
INSERT INTO `sys_dict_item` VALUES ('eadbf511b6dfe30b72c4fcad6f10680c', '2fceca32c5098a60071574c61e0327fa', 'sys_job_status', '执行失败', '4', 0, 5, NULL, 'red', 0, '异步执行失败', 'admin', '2023-03-01 18:00:03', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('ec9cfc538a91de2f61829e61064d636b', 'ad7336dda270e6430565b313a741ffb7', 'sys_user_sex', '女', '1', 1, 2, NULL, 'red', 0, '女性', 'admin', '2023-01-04 17:31:53', 'admin', '2023-01-09 16:09:52');
INSERT INTO `sys_dict_item` VALUES ('f12c3e6c95f16dfd35e80c8c7101504e', '5ed7c51e261ce27109e5f4948d40d6e2', 'db_pool_hikari', 'idleTimeout', '60000', 1, 4, NULL, '', 0, '一个连接idle状态的最大时长（毫秒），超时则被释放（retired）', 'admin', '2023-03-13 11:21:46', 'admin', '2023-03-13 11:22:47');
INSERT INTO `sys_dict_item` VALUES ('f3b5faffba629cc8e07e5d57d76e4e49', '55ccc731fe3958c4afca60cbd852a55f', 'tenant_corp_years', '十年以内', '10', 0, 3, NULL, '', 0, '', 'admin', '2023-06-13 17:21:32', 'admin', '2023-06-13 17:32:00');
INSERT INTO `sys_dict_item` VALUES ('f442a8f38c5ce32b6b05c640cc3ecc2b', 'e8e6e4c3f8e2fb775d6d083883e41839', 'sys_code_condition', '包含', 'like', 0, 2, NULL, NULL, 0, NULL, 'admin', '2023-05-10 16:12:34', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('f485da6d28850874150e6bda1d565907', 'd2d1bfb80a89199abdedae4874311b69', 'mall_order_status', '已发货', '2', 0, 3, NULL, '#1677FF', 0, NULL, 'admin', '2024-04-20 18:12:35', 'admin', '2024-04-20 18:16:36');
INSERT INTO `sys_dict_item` VALUES ('f84c6e754d183f40a4b1d7a5b72371c7', '75ce8aab0fca2be5183770260d145c17', 'sso_grant_type', 'password', 'password', 0, 2, NULL, 'blue', 0, '帐号密码认证方式', 'admin', '2023-05-16 21:50:40', 'admin', '2023-05-18 10:20:12');
INSERT INTO `sys_dict_item` VALUES ('f88a8a4a391e3ae455ba6d8157f22260', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'POST', 'POST', 0, 2, NULL, 'blue', 0, '新增', 'admin', '2023-01-09 16:12:41', 'admin', '2023-02-21 18:02:00');
INSERT INTO `sys_dict_item` VALUES ('fae13a8680a44565b4ffab652c7c9d6b', '6ddca50d9ddad44806ef18c3bf4721c9', 'nc_formula_type', '字符处理', 'char_op', 0, 1, NULL, NULL, 0, NULL, 'admin', '2023-10-27 09:12:06', 'admin', '2023-10-27 14:21:43');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
                            `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '中文标题',
                            `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法',
                            `req_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求类型',
                            `req_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求路径',
                            `req_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
                            `req_source` int(1) NULL DEFAULT 0 COMMENT '请求来源（0其它 1后台用户 2手机端用户）',
                            `oper_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '操作类型（0其它 1查询 2新增 3修改 4删除 5授权 6导入 7导出...）',
                            `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作IP',
                            `oper_status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
                            `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回信息',
                            `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                            `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `create_time_index`(`create_time`) USING BTREE COMMENT '操作时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_storage
-- ----------------------------
DROP TABLE IF EXISTS `sys_storage`;
CREATE TABLE `sys_storage`  (
                                `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                                `file_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件的唯一索引',
                                `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
                                `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型',
                                `file_size` int(11) NULL DEFAULT NULL COMMENT '文件大小',
                                `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件访问链接',
                                `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存储路径',
                                `is_private` tinyint(1) NULL DEFAULT 0 COMMENT '是否私密文件 0为公开的  1为私密文件',
                                `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
                                `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                                `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `key`(`file_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件存储表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_dict_category
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_category`;
CREATE TABLE `sys_dict_category`  (
                                      `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
                                      `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父分类id',
                                      `category_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类编码',
                                      `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
                                      `tree_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类树编码（系统自动编码）',
                                      `tree_level` tinyint(6) NOT NULL COMMENT '分类树层级（自动生成）',
                                      `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
                                      `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新用户',
                                      `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `tree_code_index`(`tree_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '树形分类字典' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_category
-- ----------------------------
INSERT INTO `sys_dict_category` VALUES ('063eafb8b1b481c1f2f99799ebc7ada7', '26c427a7125dd186ddc238647a7f4ba1', '', '苹果笔记本', '0000100003', 2, 3, 'admin', '2024-03-14 14:36:17', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('0e55dc203886fbce5cdb0dd3ba2023b3', '26c427a7125dd186ddc238647a7f4ba1', '', '联想笔记本', '0000100006', 2, 4, 'admin', '2024-03-14 14:36:43', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('2207ba37d3071c1e87a344c3f61a5155', '926abe03a1935946b43fdcc031c9d3c8', '', '小米', '0000200006', 2, 1, 'admin', '2024-03-14 14:38:19', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('26c427a7125dd186ddc238647a7f4ba1', '', 'notebook', '笔记本', '00001', 1, 0, 'admin', '2024-03-14 14:35:15', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('47eba0ed867cba5909503642bf10f2b4', '26c427a7125dd186ddc238647a7f4ba1', '', '华为笔记本', '0000100005', 2, 1, 'admin', '2024-03-14 14:36:31', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('6577e71c515e8014297ac7a9b2fb17af', '926abe03a1935946b43fdcc031c9d3c8', 'iPhone', '苹果', '0000200007', 2, 2, 'admin', '2024-03-14 14:38:23', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('90329d2cd947b18ef46a02509873f81c', '926abe03a1935946b43fdcc031c9d3c8', '', 'vivio', '0000200008', 2, 4, 'admin', '2024-03-14 14:38:36', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('926abe03a1935946b43fdcc031c9d3c8', '', 'phone', '手机', '00002', 1, 1, 'admin', '2024-03-14 14:36:08', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('964b67d7277654e21a7e3b03e1363300', '926abe03a1935946b43fdcc031c9d3c8', '', '华为', '0000200005', 2, 0, 'admin', '2024-03-14 14:38:15', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('af9d4abecbd728a49170f8f8ac46dfff', '26c427a7125dd186ddc238647a7f4ba1', 'mi', '小米笔记本', '0000100007', 2, 2, 'admin', '2024-03-14 14:36:59', NULL, NULL);
INSERT INTO `sys_dict_category` VALUES ('d90b48828eaeda14e1ec6daca7fb20e7', '926abe03a1935946b43fdcc031c9d3c8', '', 'oppo', '0000200009', 2, 5, 'admin', '2024-03-14 14:38:48', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;