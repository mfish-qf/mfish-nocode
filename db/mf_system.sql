DROP DATABASE IF EXISTS `mf_system`;
CREATE DATABASE  `mf_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_system`;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '唯一ID',
  `dict_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_code` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '字典编码',
  `status` int(1) NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `dict_code`) USING BTREE,
  UNIQUE INDEX `dict_code_index`(`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('193585c2417cfb3e4865501c2e608602', '数据库类型', 'sys_db_type', 0, '支持的数据库类型', 'admin', '2023-01-03 11:45:12', 'admin', '2023-01-03 11:46:12');
INSERT INTO `sys_dict` VALUES ('5665dd400700ebea77fcc6f8e39fc355', '请求类型', 'sys_req_type', 0, 'http请求类型', 'admin', '2023-01-09 15:53:31', 'admin', '2023-01-10 17:50:52');
INSERT INTO `sys_dict` VALUES ('ad7336dda270e6430565b313a741ffb7', '用户性别', 'sys_user_sex', 0, '用户性别字典', 'admin', '2023-01-04 16:08:35', 'admin', '2023-01-05 14:47:40');
INSERT INTO `sys_dict` VALUES ('c694f0f6feba27026044839b77d24caa', '日志操作类型', 'sys_log_type', 0, '日志操作类型', 'admin', '2023-01-09 11:27:05', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '唯一ID',
  `dict_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典ID',
  `dict_code` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '字典编码',
  `dict_label` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_sort` int(11) NULL DEFAULT 0 COMMENT '字典排序',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配色(用于前端显示)',
  `status` int(1) NULL DEFAULT 0 COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dict_code_index`(`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典项' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES ('0625b88c24a5cd98e045a58679370249', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '新增', '新增', 3, 'blue', 0, NULL, 'admin', '2023-01-09 11:27:52', 'admin', '2023-01-09 16:11:41');
INSERT INTO `sys_dict_item` VALUES ('0f11bb06b93b05e27f8a45d78765a243', '193585c2417cfb3e4865501c2e608602', 'sys_db_type', 'mysql', 'mysql', 1, NULL, 0, 'mysql数据库', 'admin', '2023-01-03 16:40:55', 'admin', '2023-01-04 16:17:07');
INSERT INTO `sys_dict_item` VALUES ('2065051b945805f8915ea68431c097ff', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '查询', '查询', 2, 'green', 0, NULL, 'admin', '2023-01-09 11:27:46', 'admin', '2023-01-09 15:21:07');
INSERT INTO `sys_dict_item` VALUES ('261b1fdc50de63e495cdb757b6d430e2', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '删除', '删除', 5, 'red', 0, NULL, 'admin', '2023-01-09 11:28:05', 'admin', '2023-01-09 15:23:00');
INSERT INTO `sys_dict_item` VALUES ('3cd68c5687d190e7b0a6242b162424a7', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'OPTIONS', 'OPTIONS', 5, 'cyan', 0, '测试服务器的功能性', 'admin', '2023-01-10 18:01:42', 'admin', '2023-01-10 18:03:29');
INSERT INTO `sys_dict_item` VALUES ('449f7f3ff1bf55ddb8159f5d122e29c4', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '其他操作', '其他操作', 1, 'default', 0, NULL, 'admin', '2023-01-09 11:27:39', 'admin', '2023-01-09 15:43:04');
INSERT INTO `sys_dict_item` VALUES ('5bfc739984836091b77b298d9a605181', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'DELETE', 'DELETE', 4, 'red', 0, '删除', 'admin', '2023-01-09 16:13:42', 'admin', '2023-01-10 17:50:52');
INSERT INTO `sys_dict_item` VALUES ('5c24ec491e462910d3123ac7efffdac2', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '登录', '登录', 9, 'orange', 0, NULL, 'admin', '2023-01-09 11:28:39', 'admin', '2023-01-09 15:23:43');
INSERT INTO `sys_dict_item` VALUES ('60f7b458a56b372bcd4c8d797faf926e', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '登出', '登出', 10, 'red', 0, NULL, 'admin', '2023-01-09 11:28:50', 'admin', '2023-01-09 15:23:36');
INSERT INTO `sys_dict_item` VALUES ('6c4f687dc49bf0f4e881d5768fd9ed4d', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '修改', '修改', 4, 'purple', 0, NULL, 'admin', '2023-01-09 11:27:58', 'admin', '2023-01-09 16:11:35');
INSERT INTO `sys_dict_item` VALUES ('6f45f21f466658f51bdbb942f3a679b7', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '导出', '导出', 8, '#87d068', 0, NULL, 'admin', '2023-01-09 11:28:30', 'admin', '2023-01-09 15:26:03');
INSERT INTO `sys_dict_item` VALUES ('731a23dbfd6ec285c9c81ab2a29b43b1', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '授权', '授权', 6, 'cyan', 0, NULL, 'admin', '2023-01-09 11:28:16', 'admin', '2023-01-09 16:11:27');
INSERT INTO `sys_dict_item` VALUES ('75d8887dbeb5a0ca4d20ea445139e97a', 'c694f0f6feba27026044839b77d24caa', 'sys_log_type', '导入', '导入', 7, '#2db7f5', 0, NULL, 'admin', '2023-01-09 11:28:23', 'admin', '2023-01-09 15:25:53');
INSERT INTO `sys_dict_item` VALUES ('8d0e789e6e63e994b31750ad9cb637b4', '193585c2417cfb3e4865501c2e608602', 'sys_db_type', 'oracle', 'oracle', 2, NULL, 0, 'oracle数据库', 'admin', '2023-01-04 17:32:59', '', NULL);
INSERT INTO `sys_dict_item` VALUES ('93a1cdf50fe9f17e399cf663193a7a1f', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'GET', 'GET', 1, 'green', 0, '查询', 'admin', '2023-01-09 16:10:49', 'admin', '2023-01-10 17:50:52');
INSERT INTO `sys_dict_item` VALUES ('ce8059c1fb3b533339f6bdccc17e0046', 'ad7336dda270e6430565b313a741ffb7', 'sys_user_sex', '男', '1', 1, 'green', 0, '男性', 'admin', '2023-01-04 17:31:35', 'admin', '2023-01-09 16:09:49');
INSERT INTO `sys_dict_item` VALUES ('cf2899ae2d41fd29b5cef8de005098e7', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'PUT', 'PUT', 3, 'purple', 0, '修改', 'admin', '2023-01-09 16:12:11', 'admin', '2023-01-10 17:50:52');
INSERT INTO `sys_dict_item` VALUES ('ec9cfc538a91de2f61829e61064d636b', 'ad7336dda270e6430565b313a741ffb7', 'sys_user_sex', '女', '1', 2, 'red', 0, '女性', 'admin', '2023-01-04 17:31:53', 'admin', '2023-01-09 16:09:52');
INSERT INTO `sys_dict_item` VALUES ('f88a8a4a391e3ae455ba6d8157f22260', '5665dd400700ebea77fcc6f8e39fc355', 'sys_req_type', 'POST', 'POST', 2, 'blue', 0, '新增', 'admin', '2023-01-09 16:12:41', 'admin', '2023-01-10 17:50:52');

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
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `create_time_index`(`create_time`) USING BTREE COMMENT '操作时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统日志' ROW_FORMAT = Dynamic;

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
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型',
  `file_size` int(11) NULL DEFAULT NULL COMMENT '文件大小',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件访问链接',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存储路径',
  `is_private` tinyint(1) NULL DEFAULT 0 COMMENT '是否私密文件 0为公开的  1为私密文件',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key`(`file_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件存储表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_storage
-- ----------------------------
INSERT INTO `sys_storage` VALUES ('4711045c2b114813b5ccde4f3a6dce3b', '4711045c2b114813b5ccde4f3a6dce3b.png', '资源 1@3x.png', 'image/png', 37202, 'http://localhost:8888/storage/file/4711045c2b114813b5ccde4f3a6dce3b.png', '2023/01/07', 1, 0, 'admin', '2023-01-07 16:29:55', '', NULL);
INSERT INTO `sys_storage` VALUES ('950e8e4a20d54f7b82a2bf4846ea2c66', '950e8e4a20d54f7b82a2bf4846ea2c66.png', 'blob', 'image/png', 37061, 'http://localhost:8888/storage/file/950e8e4a20d54f7b82a2bf4846ea2c66.png', '2023/01/11', 1, 0, 'admin', '2023-01-11 20:56:40', '', NULL);
INSERT INTO `sys_storage` VALUES ('9fbe3f618cd14019903dd5cecd1a57ed', '9fbe3f618cd14019903dd5cecd1a57ed.png', '资源 2@3x.png', 'image/png', 35625, 'http://localhost:8888/storage/file/9fbe3f618cd14019903dd5cecd1a57ed.png', '2023/01/15', 1, 0, 'admin', '2023-01-15 11:50:41', '', NULL);

SET FOREIGN_KEY_CHECKS = 1;
