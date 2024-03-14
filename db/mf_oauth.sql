DROP DATABASE IF EXISTS `mf_oauth`;
CREATE DATABASE  `mf_oauth` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_oauth`;

-- ----------------------------
-- Table structure for sso_client_details
-- ----------------------------
DROP TABLE IF EXISTS `sso_client_details`;
CREATE TABLE `sso_client_details`  (
                                       `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                                       `client_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
                                       `client_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端名称',
                                       `resource_ids` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端能访问的资源集合',
                                       `client_secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端密钥',
                                       `scope` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指定客户端权限范围',
                                       `grant_types` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '认证方式 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token',
                                       `redirect_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端重定向url，authorization_code认证回调地址',
                                       `authorities` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指定用户的权限范围',
                                       `auto_approve` tinyint(1) NULL DEFAULT NULL COMMENT '跳过授权页,默认true,适用于authorization_code模式',
                                       `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                       `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新用户',
                                       `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                       `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户端信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_client_details
-- ----------------------------
INSERT INTO `sso_client_details` VALUES ('1', 'system', '系统', NULL, 'system', 'all', 'authorization_code,password,refresh_token', 'http://localhost:5281/oauth2.*,http://localhost:11119/oauth2.*', NULL, 1, NULL, NULL, 'admin', '2023-10-13 16:02:05', 0);

-- ----------------------------
-- Table structure for sso_menu
-- ----------------------------
DROP TABLE IF EXISTS `sso_menu`;
CREATE TABLE `sso_menu`  (
                             `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID',
                             `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '父菜单ID',
                             `menu_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单编码',
                             `menu_level` tinyint(4) NULL DEFAULT NULL COMMENT '菜单级别',
                             `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
                             `menu_icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
                             `menu_sort` int(4) NULL DEFAULT 0 COMMENT '菜单顺序',
                             `menu_type` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '菜单类型（0目录 1菜单 2按钮）',
                             `route_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
                             `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
                             `permissions` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识(多个标识逗号隔开)',
                             `is_external` tinyint(1) NULL DEFAULT 0 COMMENT '是否为外部链接（1是 0否）',
                             `is_visible` tinyint(1) NULL DEFAULT 1 COMMENT '菜单状态（1显示 0隐藏）',
                             `active_menu` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当前激活菜单(当菜单隐藏时，设置当前激活的菜单项)',
                             `is_keepalive` tinyint(1) NULL DEFAULT NULL COMMENT '是否缓存(1是 0否)',
                             `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',
                             `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `index_menu_code`(`menu_code`) USING BTREE COMMENT '菜单编码索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_menu
-- ----------------------------
INSERT INTO `sso_menu` VALUES ('503e3ac379a2e17e99105b77a727e6db', '', '00001', 1, '驾驶舱', 'ant-design:appstore-outlined', 1, 0, '/dashboard', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:53:57', 'admin', '2024-02-02 10:11:31');
INSERT INTO `sso_menu` VALUES ('76f68d05f5054818762718ee85d6d0fe', '503e3ac379a2e17e99105b77a727e6db', '0000100001', 2, '工作台', 'ant-design:calendar-outlined', 1, 1, '/workbench', '/dashboard/workbench/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-10 17:15:10', 'test', '2023-06-23 20:12:21');
INSERT INTO `sso_menu` VALUES ('268d140daddc00dc77823c7d7c2025fb', '76f68d05f5054818762718ee85d6d0fe', '000010000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:workbench:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:15:10', 'admin', '2023-03-10 17:15:25');
INSERT INTO `sso_menu` VALUES ('234dc900ad6502579a51784f9ddb05d5', '76f68d05f5054818762718ee85d6d0fe', '000010000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:workbench:insert', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:15:10', '', NULL);
INSERT INTO `sso_menu` VALUES ('6a38a3847b66cc690c3a2eacedb4e81f', '76f68d05f5054818762718ee85d6d0fe', '000010000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:workbench:update', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:15:10', '', NULL);
INSERT INTO `sso_menu` VALUES ('7e87849f80699ad24292fd9908f5aeb8', '76f68d05f5054818762718ee85d6d0fe', '000010000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:workbench:delete', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:15:10', 'admin', '2023-03-10 17:15:39');
INSERT INTO `sso_menu` VALUES ('2a4e024fdc76063da32926c63ca9ead2', '', '00002', 1, '系统管理', 'ant-design:setting-outlined', 3, 0, '/system', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:59:57', 'admin', '2023-09-01 15:13:58');
INSERT INTO `sso_menu` VALUES ('a988f38821885f8f8aaffa49d681aaac', '2a4e024fdc76063da32926c63ca9ead2', '0000200001', 2, '菜单管理', 'ion:ios-menu', 1, 1, '/menu', '/sys/menu/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-08 17:02:02', 'admin', '2022-12-14 14:25:17');
INSERT INTO `sso_menu` VALUES ('75882dc140444e061741fbd9f026dd2b', 'a988f38821885f8f8aaffa49d681aaac', '000020000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:04:16', 'admin', '2022-11-30 16:59:33');
INSERT INTO `sso_menu` VALUES ('967795af502129d318899a60716da84f', 'a988f38821885f8f8aaffa49d681aaac', '000020000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:menu:insert,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:04:45', 'admin', '2022-12-20 11:30:37');
INSERT INTO `sso_menu` VALUES ('6fd5cdaf86772d4db0587f3b9281f99b', 'a988f38821885f8f8aaffa49d681aaac', '000020000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:menu:update,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:05:12', 'admin', '2022-12-20 11:33:37');
INSERT INTO `sso_menu` VALUES ('1a73215261f568088e9adeef2dbd8e44', 'a988f38821885f8f8aaffa49d681aaac', '000020000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:menu:delete,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:05:36', 'admin', '2022-12-20 11:34:00');
INSERT INTO `sso_menu` VALUES ('4ef7029abe93c11601678ba16dac406f', '2a4e024fdc76063da32926c63ca9ead2', '0000200002', 2, '帐号管理', 'ion:people-sharp', 4, 1, '/account', '/sys/account/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-30 16:40:28', 'admin', '2023-01-04 21:03:33');
INSERT INTO `sso_menu` VALUES ('6ac6bc8054107436e24356e3466f00db', '4ef7029abe93c11601678ba16dac406f', '000020000200001', 3, '查询', '#', 1, 2, '', NULL, 'sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 16:54:15', 'admin', '2022-12-20 11:46:51');
INSERT INTO `sso_menu` VALUES ('9f46c219e3fc35b1c2ef3a95438b16bf', '4ef7029abe93c11601678ba16dac406f', '000020000200002', 3, '新增', '#', 2, 2, '', NULL, 'sys:account:insert,sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:02', 'admin', '2022-12-20 11:47:06');
INSERT INTO `sso_menu` VALUES ('c46042d6e6d16ea95df6461648833675', '4ef7029abe93c11601678ba16dac406f', '000020000200003', 3, '修改', '#', 3, 2, '', NULL, 'sys:account:update,sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:23', 'admin', '2022-12-17 22:26:35');
INSERT INTO `sso_menu` VALUES ('a27822a74728632e0e0ed10d8285bf54', '4ef7029abe93c11601678ba16dac406f', '000020000200004', 3, '删除', '#', 4, 2, '', NULL, 'sys:account:delete,  sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:48', 'admin', '2022-12-20 11:47:15');
INSERT INTO `sso_menu` VALUES ('addeaf01bc278e216de75ad26a8f27b6', '2a4e024fdc76063da32926c63ca9ead2', '0000200003', 2, '组织管理', 'ion:ios-people', 2, 1, '/org', '/sys/org/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-30 17:22:12', 'admin', '2023-01-03 20:27:42');
INSERT INTO `sso_menu` VALUES ('76f149981f1c86fce81f2f4cdb9674b9', 'addeaf01bc278e216de75ad26a8f27b6', '000020000300001', 3, '查询', '#', 1, 2, '', NULL, 'sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:26:51', '', NULL);
INSERT INTO `sso_menu` VALUES ('f87d8b297eb3650834048dba7c8d2d89', 'addeaf01bc278e216de75ad26a8f27b6', '000020000300002', 3, '新增', '#', 2, 2, '', NULL, 'sys:org:insert,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:27:41', 'admin', '2022-12-20 11:34:47');
INSERT INTO `sso_menu` VALUES ('58efbcc5f46b95aeab069076031959e7', 'addeaf01bc278e216de75ad26a8f27b6', '000020000300003', 3, '修改', '#', 3, 2, '', NULL, 'sys:org:update,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:28:45', 'admin', '2022-12-20 11:45:25');
INSERT INTO `sso_menu` VALUES ('ee3ae3a2161e8d58e2c62f340c3d7b55', 'addeaf01bc278e216de75ad26a8f27b6', '000020000300004', 3, '删除', '#', 4, 2, '', NULL, 'sys:org:delete,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:29:49', 'admin', '2022-12-20 11:45:40');
INSERT INTO `sso_menu` VALUES ('0f5a85a6fd5bdc9df26b826eec3c17f1', '2a4e024fdc76063da32926c63ca9ead2', '0000200004', 2, '角色管理', 'ion:ios-key', 3, 1, '/role', '/sys/role/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-11-30 17:32:14', 'admin', '2022-12-14 14:25:37');
INSERT INTO `sso_menu` VALUES ('f4a0ed4ca7a609aa8268399bdffcecfb', '0f5a85a6fd5bdc9df26b826eec3c17f1', '000020000400001', 3, '查询', '#', 1, 2, '', NULL, 'sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:53:00', '', NULL);
INSERT INTO `sso_menu` VALUES ('fb5dac5b0b9b610ed1e996108d6445b0', '0f5a85a6fd5bdc9df26b826eec3c17f1', '000020000400002', 3, '新增', '#', 2, 2, '', NULL, 'sys:role:insert,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:54:39', 'admin', '2022-12-20 11:46:05');
INSERT INTO `sso_menu` VALUES ('9b9139c09668bb22888201b7e8a812c4', '0f5a85a6fd5bdc9df26b826eec3c17f1', '000020000400003', 3, '修改', '#', 3, 2, '', NULL, 'sys:role:update,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:58:08', 'admin', '2022-12-20 11:46:23');
INSERT INTO `sso_menu` VALUES ('c487023e85c9aaf5510a03e8017b768c', '0f5a85a6fd5bdc9df26b826eec3c17f1', '000020000400004', 3, '删除', '#', 4, 2, '', NULL, 'sys:role:delete,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 18:02:10', 'admin', '2022-12-20 11:46:13');
INSERT INTO `sso_menu` VALUES ('c9eb585420911ee18335d935d3872934', '2a4e024fdc76063da32926c63ca9ead2', '0000200005', 2, '字典管理', 'ion:ios-list', 5, 1, '/dict', '/sys/dict/index.vue', NULL, 0, 1, NULL, NULL, '', 'admin', '2022-11-30 18:08:11', 'admin', '2022-12-14 14:26:05');
INSERT INTO `sso_menu` VALUES ('80526081fb00ce5dbe629ef358231909', 'c9eb585420911ee18335d935d3872934', '000020000500001', 3, '查询', '#', 1, 2, '', NULL, 'sys:dict:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:00:35', '', NULL);
INSERT INTO `sso_menu` VALUES ('f634ba1d6d840fb1f945b4f811dd928d', 'c9eb585420911ee18335d935d3872934', '000020000500002', 3, '新增', '#', 2, 2, '', NULL, 'sys:dict:insert,sys:dict:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:01:26', '', NULL);
INSERT INTO `sso_menu` VALUES ('ce71591e16d47d6b4ff1d52c2bb83ae7', 'c9eb585420911ee18335d935d3872934', '000020000500003', 3, '修改', '#', 3, 2, '', NULL, 'sys:dict:update,sys:dict:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:01:46', '', NULL);
INSERT INTO `sso_menu` VALUES ('e48e7b98d2c0331ff6241514e97dad8b', 'c9eb585420911ee18335d935d3872934', '000020000500004', 3, '删除', '#', 4, 2, '', NULL, 'sys:dict:delete,sys:dict:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:02:10', '', NULL);
INSERT INTO `sso_menu` VALUES ('67dfbce31013ada62800425f72997962', '2a4e024fdc76063da32926c63ca9ead2', '0000200006', 2, '字典项', 'ion:ios-menu', 6, 1, '/dict/:dictCode', '/sys/dict-item/index.vue', NULL, 0, 0, '0000200005', 0, '', 'admin', '2023-01-03 17:07:39', 'admin', '2023-04-11 22:22:19');
INSERT INTO `sso_menu` VALUES ('cfbdf3ce5297cebf806ac116fc239558', '2a4e024fdc76063da32926c63ca9ead2', '0000200008', 2, '日志管理', 'ion:ios-compose-outline', 8, 1, '/log', '/sys/sys-log/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-01-08 22:17:26', 'admin', '2023-04-11 22:21:36');
INSERT INTO `sso_menu` VALUES ('25a5783ea03e26d7844b9b7370576236', 'cfbdf3ce5297cebf806ac116fc239558', '000020000800001', 3, '查询', '#', 1, 2, '', NULL, 'sys:log:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:06:31', 'mfish', '2023-07-31 14:52:22');
INSERT INTO `sso_menu` VALUES ('9a293c164762776e0a876323a3363dec', 'cfbdf3ce5297cebf806ac116fc239558', '000020000800002', 3, '删除', '#', 2, 2, '', NULL, 'sys:log:delete,sys:log:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:07:06', 'mfish', '2023-07-31 14:52:36');
INSERT INTO `sso_menu` VALUES ('60efb66a88ab33b339718eb0d052a033', '2a4e024fdc76063da32926c63ca9ead2', '0000200009', 2, '文件管理', 'ion:file-tray-full-outline', 9, 1, '/file', '/storage/sys-file/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-02 14:58:44', 'admin', '2023-04-11 22:23:20');
INSERT INTO `sso_menu` VALUES ('731738ed9bbd2e36456b790dfadcb84e', '60efb66a88ab33b339718eb0d052a033', '000020000900001', 3, '查询', '#', 1, 2, '', NULL, 'sys:file:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:15:47', 'admin', '2023-03-10 10:17:31');
INSERT INTO `sso_menu` VALUES ('ce1a05cdedf2d0684574a30dd3ed14f9', '60efb66a88ab33b339718eb0d052a033', '000020000900002', 3, '上传', '#', 2, 2, '', NULL, 'sys:file:upload,sys:file:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:16:27', 'admin', '2023-03-10 10:17:40');
INSERT INTO `sso_menu` VALUES ('a7145a05342033be0caa4a8f1e262f8a', '60efb66a88ab33b339718eb0d052a033', '000020000900003', 3, '删除', '#', 3, 2, '', NULL, 'sys:file:delete,sys:file:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:16:57', 'admin', '2023-03-10 10:17:49');
INSERT INTO `sso_menu` VALUES ('3a74d165bcd286f102e10a1be8c23eef', '60efb66a88ab33b339718eb0d052a033', '000020000900004', 3, '状态修改', '#', 4, 2, '', NULL, 'sys:file:status,sys:file:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:51:47', '', NULL);
INSERT INTO `sso_menu` VALUES ('c52a49da263d57d2c89edcbc9ca70a0a', '2a4e024fdc76063da32926c63ca9ead2', '0000200010', 2, '在线用户', 'ion:wifi', 10, 1, '/online', '/sys/online/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-09 11:34:20', 'admin', '2023-03-09 15:19:05');
INSERT INTO `sso_menu` VALUES ('101cb161536a5a80731a4d6db0b5eeac', 'c52a49da263d57d2c89edcbc9ca70a0a', '000020001000001', 3, '查询', '#', 1, 2, '', NULL, 'sys:online:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:20:38', '', NULL);
INSERT INTO `sso_menu` VALUES ('2628fb3c4166d6469f06fcea9b9c0c55', 'c52a49da263d57d2c89edcbc9ca70a0a', '000020001000002', 3, '强制退出', '#', 2, 2, '', NULL, 'sys:online:revoke,sys:online:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 10:21:07', '', NULL);
INSERT INTO `sso_menu` VALUES ('3d1efa154266719e6322808064df4b13', '2a4e024fdc76063da32926c63ca9ead2', '0000200011', 2, '数据库', 'ion:server-outline', 12, 1, '/db-connect', '/sys/db-connect/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-04-05 21:55:09', 'admin', '2023-05-18 09:04:51');
INSERT INTO `sso_menu` VALUES ('3c4b2d0f7558d7f45a29fd9c6a7edea7', '3d1efa154266719e6322808064df4b13', '000020001100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:database:query', 0, 1, NULL, NULL, '', 'admin', '2023-04-05 21:55:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('79cbc9e257ee8f44db6b133c584ff86a', '3d1efa154266719e6322808064df4b13', '000020001100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:database:query,sys:database:insert', 0, 1, NULL, NULL, '', 'admin', '2023-04-05 21:55:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('5271166ced06a95d787dc049d3f19bd2', '3d1efa154266719e6322808064df4b13', '000020001100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:database:query,sys:database:update', 0, 1, NULL, NULL, '', 'admin', '2023-04-05 21:55:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('43ca0b3ba70c7ba5a2f91882f618208b', '3d1efa154266719e6322808064df4b13', '000020001100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:database:query,sys:database:delete', 0, 1, NULL, NULL, '', 'admin', '2023-04-05 21:55:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('cfc0ca0b3c9220fa3cae26420cf0c51f', '2a4e024fdc76063da32926c63ca9ead2', '0000200013', 2, '应用管理', 'ion:apps-outline', 11, 1, '/client', '/sys/sso-client-details/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-05-12 22:51:01', 'admin', '2023-05-18 09:04:46');
INSERT INTO `sso_menu` VALUES ('bf2e6661d9a4d115f018ffd4ff202d92', 'cfc0ca0b3c9220fa3cae26420cf0c51f', '000020001300001', 3, '查询', '#', 1, 2, '', NULL, 'sys:client:query', 0, 1, NULL, NULL, '', 'admin', '2023-05-17 22:24:48', '', NULL);
INSERT INTO `sso_menu` VALUES ('93190e5d426f69cd2712aac373542698', 'cfc0ca0b3c9220fa3cae26420cf0c51f', '000020001300002', 3, '新增', '#', 2, 2, '', NULL, 'sys:client:query,sys:client:insert', 0, 1, NULL, NULL, '', 'admin', '2023-05-17 22:26:36', '', NULL);
INSERT INTO `sso_menu` VALUES ('b40f985b74d1dd6e311e21369beea8ea', 'cfc0ca0b3c9220fa3cae26420cf0c51f', '000020001300003', 3, '修改', '#', 3, 2, '', NULL, 'sys:client:query,sys:client:update', 0, 1, NULL, NULL, '', 'admin', '2023-05-17 22:27:08', '', NULL);
INSERT INTO `sso_menu` VALUES ('9840528e4a1efd3a510d8a087baf4bdb', 'cfc0ca0b3c9220fa3cae26420cf0c51f', '000020001300004', 3, '删除', '#', 4, 2, '', NULL, 'sys:client:query,sys:client:delete', 0, 1, NULL, NULL, '', 'admin', '2023-05-17 23:30:20', '', NULL);
INSERT INTO `sso_menu` VALUES ('f16ec999ec440bee68dd4e01a650fd54', '2a4e024fdc76063da32926c63ca9ead2', '0000200014', 2, '分类管理', 'ant-design:cluster-outlined', 7, 1, '/category', '/sys/dict-category/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-03-14 14:21:35', '', NULL);
INSERT INTO `sso_menu` VALUES ('4d1e926fd6767a786b1cab58e3bc5624', 'f16ec999ec440bee68dd4e01a650fd54', '000020001400001', 3, '查询', '#', 1, 2, '', NULL, 'sys:dictCategory:query', 0, 1, NULL, NULL, '', 'admin', '2024-03-14 14:22:44', '', NULL);
INSERT INTO `sso_menu` VALUES ('b54e001d27b804b769564f35430193b2', 'f16ec999ec440bee68dd4e01a650fd54', '000020001400002', 3, '新增', '#', 2, 2, '', NULL, 'sys:dictCategory:query,sys:dictCategory:insert', 0, 1, NULL, NULL, '', 'admin', '2024-03-14 14:23:40', '', NULL);
INSERT INTO `sso_menu` VALUES ('43f57faab0a1d54cc5130d8a3cc9594a', 'f16ec999ec440bee68dd4e01a650fd54', '000020001400003', 3, '修改', '#', 3, 2, '', NULL, 'sys:dictCategory:query,sys:dictCategory:update', 0, 1, NULL, NULL, '', 'admin', '2024-03-14 14:24:22', '', NULL);
INSERT INTO `sso_menu` VALUES ('f4a9dee8dbff56cd15ffafa815c84a27', 'f16ec999ec440bee68dd4e01a650fd54', '000020001400004', 3, '删除', '#', 4, 2, '', NULL, 'sys:dictCategory:query,sys:dictCategory:delete', 0, 1, NULL, NULL, '', 'admin', '2024-03-14 14:24:57', '', NULL);
INSERT INTO `sso_menu` VALUES ('6e491486dc4cb475e4bd037d06ab2801', '', '00003', 1, '项目文档', 'ion:book-outline', 5, 0, '/doc', NULL, NULL, 1, 1, NULL, NULL, '', 'admin', '2022-11-08 17:08:24', 'admin', '2023-02-14 17:13:51');
INSERT INTO `sso_menu` VALUES ('1a0aee8380c525e7c4802b1c4d587fa8', '6e491486dc4cb475e4bd037d06ab2801', '0000300001', 2, '接口文档', 'simple-icons:swagger', 1, 1, '/swagger', 'http://app.mfish.com.cn:11116/swagger-ui/index.html', NULL, 0, 1, NULL, 0, '', 'admin', '2022-12-14 10:25:31', 'admin', '2023-09-01 14:58:24');
INSERT INTO `sso_menu` VALUES ('1ce2ac44228e37c063e9cd55ed8f0a49', '6e491486dc4cb475e4bd037d06ab2801', '0000300002', 2, 'GitHub地址', 'ion:logo-github', 2, 1, '/git', 'https://github.com/mfish-qf/mfish-nocode', NULL, 1, 1, NULL, 1, '', 'admin', '2022-12-14 15:27:03', 'admin', '2023-04-30 11:34:12');
INSERT INTO `sso_menu` VALUES ('5c723efc132b50c0284d79eaafed5a0f', '6e491486dc4cb475e4bd037d06ab2801', '0000300003', 2, 'AntDesign文档', 'ion:social-angular-outline', 4, 1, '/ant', 'https://www.antdv.com/docs/vue/introduce-cn/', NULL, 1, 1, NULL, 1, '', 'admin', '2022-12-14 15:38:22', 'admin', '2023-11-20 15:43:46');
INSERT INTO `sso_menu` VALUES ('0526179a70fca38a69dd709dec2f1a81', '6e491486dc4cb475e4bd037d06ab2801', '0000300004', 2, 'Vben文档', 'ion:social-vimeo-outline', 5, 1, '/vben', 'https://doc.vvbin.cn/guide/introduction.html', NULL, 0, 1, NULL, 1, '', 'admin', '2022-12-15 09:14:09', 'admin', '2023-03-09 10:36:52');
INSERT INTO `sso_menu` VALUES ('fa2211276b7b84a141667ec9ea8d33a4', '6e491486dc4cb475e4bd037d06ab2801', '0000300005', 2, 'Gitee地址', 'simple-icons:gitee', 3, 1, '/gitee', 'https://gitee.com/qiufeng9862/mfish-nocode', NULL, 1, 1, NULL, 0, '', 'admin', '2023-03-09 10:36:36', '', NULL);
INSERT INTO `sso_menu` VALUES ('ee34f5aec6a2220f57fa151a147ede3c', '6e491486dc4cb475e4bd037d06ab2801', '0000300006', 2, '开发文档', 'ion:fish-outline', 0, 1, '/mfish', 'https://www.mfish.com.cn', NULL, 0, 1, NULL, 1, '', 'admin', '2023-04-30 11:33:21', '', NULL);
INSERT INTO `sso_menu` VALUES ('4527c6c05549e3594f135ac056faaece', '', '00004', 1, '引导页', 'ant-design:format-painter-outlined', 19, 1, '/setup', '/demo/setup/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-11-08 17:11:09', 'test', '2023-03-10 11:24:41');
INSERT INTO `sso_menu` VALUES ('9c6f4eff70d7b2048f63adf229c5d30d', '', '00006', 1, '多级目录', 'ion:folder-open-outline', 8, 0, '/level', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-12-14 17:03:01', 'admin', '2023-09-01 15:14:30');
INSERT INTO `sso_menu` VALUES ('5b543a83371c766788047a1a1907cffd', '9c6f4eff70d7b2048f63adf229c5d30d', '0000600001', 2, '目录1', 'ion:folder-open-outline', 1, 0, '/menu1', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-12-14 17:03:39', 'admin', '2023-03-10 17:14:41');
INSERT INTO `sso_menu` VALUES ('8ad60664c7060f811559bde09a79dae5', '5b543a83371c766788047a1a1907cffd', '000060000100001', 3, '目录2', 'ion:folder-open-outline', 1, 0, '/menu2', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-12-14 17:04:20', 'admin', '2023-01-03 20:27:11');
INSERT INTO `sso_menu` VALUES ('bcd18784374699438a215a9ab1e9b351', '8ad60664c7060f811559bde09a79dae5', '00006000010000100001', 4, '多级菜单', 'ant-design:appstore-outlined', 1, 1, '/menu3', '/demo/level/LevelMenu.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-12-14 17:05:12', 'admin', '2023-04-20 18:11:25');
INSERT INTO `sso_menu` VALUES ('0aa9f017545ec947a075f76e34c075c0', '', '00007', 1, '系统监控', 'ion:fitness-outline', 7, 0, '/monitor', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-01-27 13:55:58', 'admin', '2023-09-01 15:14:23');
INSERT INTO `sso_menu` VALUES ('e159379c94b8fcc58ebc38cf8b322772', '0aa9f017545ec947a075f76e34c075c0', '0000700001', 2, '监控中心', 'ion:fitness-sharp', 1, 1, '/center', 'http://localhost:9223', NULL, 0, 1, NULL, 0, '', 'admin', '2023-01-27 13:56:32', 'admin', '2024-02-19 22:46:14');
INSERT INTO `sso_menu` VALUES ('bea31d33d125895a9eaa827863341a91', '', '00008', 1, 'ChatGpt', 'ion:chatbox-ellipses-outline', 9, 0, '/chat', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-02-08 16:45:13', 'admin', '2023-07-28 09:52:53');
INSERT INTO `sso_menu` VALUES ('08a567f09c8d90660c23f2b432e0e1d9', 'bea31d33d125895a9eaa827863341a91', '0000800001', 2, '聊天', 'ion:chatbubbles-outline', 1, 1, '/ai', '/chat/ai/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-02-08 16:46:12', 'admin', '2023-02-08 17:20:37');
INSERT INTO `sso_menu` VALUES ('ca0a3c9ae9cd551ee4e1b727861b7c78', '', '00009', 1, '任务调度', 'ion:calendar-outline', 6, 0, '/scheduler', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-02-14 17:13:20', 'admin', '2023-09-01 15:14:15');
INSERT INTO `sso_menu` VALUES ('9d5397b6ddb4d194a95b05f42b80445b', 'ca0a3c9ae9cd551ee4e1b727861b7c78', '0000900001', 2, '任务管理', 'ion:caret-forward-circle-outline', 1, 1, '/job', '/scheduler/job/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('238132a09b6f761374dfd205b6388245', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('6e02e0e621140968dc62a2ce3dfa198d', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:job:insert,sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('ad2ab62e13c7750dcb5b41b00cbdcf66', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:job:update,sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('4cabb05e97ea7a738a2f7ce3c9d224d8', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:job:delete,sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('c36420436629884000e73b158166f260', '9d5397b6ddb4d194a95b05f42b80445b', '000090000100005', 3, '执行', '#', 5, 2, '', NULL, 'sys:job:execute,sys:job:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:24', '', NULL);
INSERT INTO `sso_menu` VALUES ('a43c057f48b54c9038719179cf9e284d', 'ca0a3c9ae9cd551ee4e1b727861b7c78', '0000900002', 2, '任务日志', 'ion:ios-paper-outline', 2, 1, '/jobLog', '/scheduler/job-log/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-03-10 17:11:32', 'admin', '2023-04-11 22:24:06');
INSERT INTO `sso_menu` VALUES ('1e1b7d50ab93ffdeca33fe5b7006eb01', 'a43c057f48b54c9038719179cf9e284d', '000090000200001', 3, '查询', '#', 1, 2, '', NULL, 'sys:jobLog:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:32', '', NULL);
INSERT INTO `sso_menu` VALUES ('e1996e92ac6cf37c0c2e40825a7af472', 'a43c057f48b54c9038719179cf9e284d', '000090000200002', 3, '删除', '#', 2, 2, '', NULL, 'sys:jobLog:delete,sys:jobLog:query', 0, 1, NULL, NULL, '', 'admin', '2023-03-10 17:11:32', '', NULL);
INSERT INTO `sso_menu` VALUES ('4bfec85ae3174915cd2a3e8ddd822220', '', '00010', 1, '关于', 'simple-icons:aboutdotme', 21, 1, '/about', '/sys/about/index.vue', NULL, 0, 1, NULL, 0, '', 'admin', '2023-03-10 17:25:30', '', NULL);
INSERT INTO `sso_menu` VALUES ('70943d8248fd8f77ade038d9afa0bf33', '', '00011', 1, '低代码', 'ant-design:code-outlined', 2, 0, '/low-code', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-03-29 10:26:55', 'admin', '2023-09-01 15:13:44');
INSERT INTO `sso_menu` VALUES ('a606083b203d32915c4d0e649c7b7c6b', '70943d8248fd8f77ade038d9afa0bf33', '0001100001', 2, '代码生成', 'ion:code-slash-outline', 4, 1, '/code-build', '/sys/code-build/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-04-11 22:19:43', 'admin', '2024-01-09 11:47:36');
INSERT INTO `sso_menu` VALUES ('85cd52250e435c555622c268262f4c02', 'a606083b203d32915c4d0e649c7b7c6b', '000110000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:codeBuild:query', 0, 1, NULL, NULL, '', 'mfish', '2023-07-11 09:56:58', '', NULL);
INSERT INTO `sso_menu` VALUES ('8360ab9544a00dc7d9f15594dd69e2ff', 'a606083b203d32915c4d0e649c7b7c6b', '000110000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:codeBuild:query,sys:codeBuild:insert', 0, 1, NULL, NULL, '', 'mfish', '2023-07-11 09:57:12', '', NULL);
INSERT INTO `sso_menu` VALUES ('6cedf51376c8998eb49e280cdba6c533', 'a606083b203d32915c4d0e649c7b7c6b', '000110000100003', 3, '删除', '#', 3, 2, '', NULL, 'sys:codeBuild:query,sys:codeBuild:delete', 0, 1, NULL, NULL, '', 'mfish', '2023-07-11 09:57:45', '', NULL);
INSERT INTO `sso_menu` VALUES ('6aee07bfe60f4ee4021bfce397a8f4df', '70943d8248fd8f77ade038d9afa0bf33', '0001100003', 2, '数据源', 'ant-design:database-outlined', 3, 1, '/database', '/sys/database/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-07-25 17:49:02', 'admin', '2024-01-09 11:47:32');
INSERT INTO `sso_menu` VALUES ('81ecfe903cb3116f00c367678059c87c', '6aee07bfe60f4ee4021bfce397a8f4df', '000110000300001', 3, '查询', '#', 1, 2, '', NULL, 'sys:database:query', 0, 1, NULL, NULL, '', 'admin', '2023-07-25 17:49:02', '', NULL);
INSERT INTO `sso_menu` VALUES ('5ad16d38964bf541b6417b07ddf33d9b', '6aee07bfe60f4ee4021bfce397a8f4df', '000110000300002', 3, '新增', '#', 2, 2, '', NULL, 'sys:database:query,sys:database:insert', 0, 1, NULL, NULL, '', 'admin', '2023-07-25 17:49:02', '', NULL);
INSERT INTO `sso_menu` VALUES ('369de1bef8d1e964414f25ec6d3156bc', '6aee07bfe60f4ee4021bfce397a8f4df', '000110000300003', 3, '修改', '#', 3, 2, '', NULL, 'sys:database:query,sys:database:update', 0, 1, NULL, NULL, '', 'admin', '2023-07-25 17:49:02', '', NULL);
INSERT INTO `sso_menu` VALUES ('a2cdbac934bae3da9987df0655db2455', '6aee07bfe60f4ee4021bfce397a8f4df', '000110000300004', 3, '删除', '#', 4, 2, '', NULL, 'sys:database:query,sys:database:delete', 0, 1, NULL, NULL, '', 'admin', '2023-07-25 17:49:02', '', NULL);
INSERT INTO `sso_menu` VALUES ('292791abfbf63b35ad9ce510c0a5823e', '70943d8248fd8f77ade038d9afa0bf33', '0001100004', 2, '自助API', 'ant-design:api-outlined', 2, 1, '/mf-api', '/nocode/api-folder/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-08-02 10:39:11', 'admin', '2024-01-09 11:47:18');
INSERT INTO `sso_menu` VALUES ('eda0153e492e86ade2fe6702d267fef3', '292791abfbf63b35ad9ce510c0a5823e', '000110000400001', 3, '查询', '#', 1, 2, '', NULL, 'sys:mfApi:query', 0, 1, NULL, NULL, '', 'admin', '2023-08-02 10:39:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('afbf598d2290b24a5e87e7547c05515d', '292791abfbf63b35ad9ce510c0a5823e', '000110000400002', 3, '新增', '#', 2, 2, '', NULL, 'sys:mfApi:query,sys:mfApi:insert', 0, 1, NULL, NULL, '', 'admin', '2023-08-02 10:39:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('285bb37f32e878e24d5a22445c1bc5af', '292791abfbf63b35ad9ce510c0a5823e', '000110000400003', 3, '修改', '#', 3, 2, '', NULL, 'sys:mfApi:query,sys:mfApi:update', 0, 1, NULL, NULL, '', 'admin', '2023-08-02 10:39:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('01ee419d3dcc28acf1920428391720b7', '292791abfbf63b35ad9ce510c0a5823e', '000110000400004', 3, '删除', '#', 4, 2, '', NULL, 'sys:mfApi:query,sys:mfApi:delete', 0, 1, NULL, NULL, '', 'admin', '2023-08-02 10:39:11', '', NULL);
INSERT INTO `sso_menu` VALUES ('e909edc44910691dedc2c5338ec0e603', '70943d8248fd8f77ade038d9afa0bf33', '0001100005', 2, '自助大屏', 'ant-design:fund-projection-screen-outlined', 1, 1, '/mf-screen', '/nocode/screen-folder/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2024-01-09 11:47:10', 'admin', '2024-01-09 11:54:24');
INSERT INTO `sso_menu` VALUES ('40bf4846599bf8dbd307f77bf51a7dad', '', '00012', 1, '租户管理', 'ion:home-outline', 4, 0, '/tenant', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2023-05-31 22:41:48', 'admin', '2023-09-01 15:14:04');
INSERT INTO `sso_menu` VALUES ('74ce27a1e94091da2efa714e32680e7a', '40bf4846599bf8dbd307f77bf51a7dad', '0001200001', 2, '租户配置', 'ant-design:home-outlined', 1, 1, '/config', '/sys/sso-tenant/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-05-31 22:43:26', 'admin', '2023-06-13 15:51:35');
INSERT INTO `sso_menu` VALUES ('3a92fd411f0a70bf477e6dc354f4e29e', '74ce27a1e94091da2efa714e32680e7a', '000120000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:ssoTenant:query', 0, 1, NULL, NULL, '', 'mfish', '2023-06-28 13:02:17', '', NULL);
INSERT INTO `sso_menu` VALUES ('442f89857faa6bc929ef4f422b8c4b99', '74ce27a1e94091da2efa714e32680e7a', '000120000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:ssoTenant:query,sys:ssoTenant:insert', 0, 1, NULL, NULL, '', 'mfish', '2023-06-28 13:02:42', '', NULL);
INSERT INTO `sso_menu` VALUES ('520873645d565776988d81481d8b0d26', '74ce27a1e94091da2efa714e32680e7a', '000120000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:ssoTenant:query,sys:ssoTenant:update', 0, 1, NULL, NULL, '', 'mfish', '2023-06-28 13:02:59', '', NULL);
INSERT INTO `sso_menu` VALUES ('d9832cd3aefbb5f99267edb995ff8c75', '74ce27a1e94091da2efa714e32680e7a', '000120000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:ssoTenant:query,sys:ssoTenant:delete', 0, 1, NULL, NULL, '', 'mfish', '2023-06-28 13:03:15', '', NULL);
INSERT INTO `sso_menu` VALUES ('eb5f513d5430597d3ea312e1bf760b23', '40bf4846599bf8dbd307f77bf51a7dad', '0001200002', 2, '租户信息', 'ant-design:info-circle-outlined', 2, 1, '/info/3', '/sys/account/setting/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-06-25 20:21:51', 'admin', '2023-06-25 21:15:54');
INSERT INTO `sso_menu` VALUES ('7e690410346c4d3a1610d85e8c9f906b', '40bf4846599bf8dbd307f77bf51a7dad', '0001200003', 2, '个人信息', 'ion:person', 7, 1, '/info/1', '/sys/account/setting/index.vue', NULL, 0, 1, '0000200002', 1, '', 'admin', '2023-06-29 10:54:29', 'admin', '2023-06-29 10:59:16');
INSERT INTO `sso_menu` VALUES ('6d71e92a5a3712acbcfcc58f65b93f4f', '7e690410346c4d3a1610d85e8c9f906b', '000120000300001', 3, '上传图像', '#', 1, 2, '', NULL, 'sys:file:upload', 0, 1, NULL, NULL, '', 'admin', '2023-06-29 10:54:29', '', NULL);

-- ----------------------------
-- Table structure for sso_org
-- ----------------------------
DROP TABLE IF EXISTS `sso_org`;
CREATE TABLE `sso_org`  (
                            `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织ID',
                            `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '父组织ID',
                            `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
                            `org_fix_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织固定编码',
                            `org_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '组织编码(自动生成父子关系编码)',
                            `org_level` tinyint(4) NULL DEFAULT NULL COMMENT '组织级别',
                            `org_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '组织名称',
                            `org_sort` int(4) NULL DEFAULT 0 COMMENT '排序',
                            `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
                            `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
                            `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                            `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                            `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
                            `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
                            `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新用户',
                            `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `org_code_index`(`org_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组织结构表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_org
-- ----------------------------
INSERT INTO `sso_org` VALUES ('1', '', '1', 'admin', '00005', 1, '系统默认', 1, 'admin', '18900000001', 'mfish@qq.com', '租户组织', 0, 0, 'admin', '2023-06-20 23:30:00', 'admin', '2023-07-05 15:27:04');
INSERT INTO `sso_org` VALUES ('404542be62d21014451808aa67f1f5df', '', 'a480b6861ca4a44631af794a99e77265', 'mfish', '00006', 1, '南京XXX公司', 1, 'mfish', '18900000002', 'mfish2@qq.com', NULL, 0, 0, 'admin', '2023-06-28 11:17:08', 'admin', '2023-07-11 10:40:19');
INSERT INTO `sso_org` VALUES ('45ee8344ffb942d6f2ee1f868b584a99', '404542be62d21014451808aa67f1f5df', NULL, NULL, '0000600002', 2, 'XXX运维部', 2, 'aa', NULL, NULL, NULL, 0, 0, 'mfish', '2023-06-28 11:24:29', 'mfish', '2023-07-02 23:18:03');
INSERT INTO `sso_org` VALUES ('624edadc0562c7fe9b54f0e3ba5e658b', '8b7bbf1cac1af7faca6dd2686981fb22', NULL, '', '000050000100001', 3, '摸鱼开发部', 1, '', '', '', '', 0, 0, 'admin', '2023-06-28 11:47:54', 'mfish', '2023-07-03 15:48:22');
INSERT INTO `sso_org` VALUES ('76f3d1d15272858c2523f287aea955f9', '8b7bbf1cac1af7faca6dd2686981fb22', NULL, '', '000050000100002', 3, '摸鱼测试部', 2, '', '', '', '', 0, 0, 'admin', '2023-06-28 11:48:14', 'mfish', '2023-07-03 15:48:28');
INSERT INTO `sso_org` VALUES ('8b7bbf1cac1af7faca6dd2686981fb22', '1', NULL, 'mysyb', '0000500001', 2, '摸鱼事业部', 1, 'mfish', '18900000001', 'mfish@qq.com', '', 0, 0, 'admin', '2023-06-28 10:26:38', NULL, NULL);
INSERT INTO `sso_org` VALUES ('a2569f85d5cd316f5c7011e43dc86271', '404542be62d21014451808aa67f1f5df', NULL, NULL, '0000600001', 2, 'XXX开发部', 1, NULL, NULL, NULL, NULL, 0, 0, 'mfish', '2023-06-28 11:23:47', NULL, NULL);
INSERT INTO `sso_org` VALUES ('af30f104a622b5de76add210dd33b361', '8b7bbf1cac1af7faca6dd2686981fb22', NULL, '', '000050000100004', 3, '摸鱼运维部', 3, '', '', '', '', 0, 0, 'admin', '2023-06-28 11:48:25', 'mfish', '2023-07-03 15:48:34');

-- ----------------------------
-- Table structure for sso_org_role
-- ----------------------------
DROP TABLE IF EXISTS `sso_org_role`;
CREATE TABLE `sso_org_role`  (
                                 `role_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
                                 `org_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织ID',
                                 PRIMARY KEY (`role_id`, `org_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色组织关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_org_role
-- ----------------------------
INSERT INTO `sso_org_role` VALUES ('210297727b74ecb505c1b4d97f76daee', '76f3d1d15272858c2523f287aea955f9');
INSERT INTO `sso_org_role` VALUES ('4063404b06e967bcf619bf86e7fe6359', '45ee8344ffb942d6f2ee1f868b584a99');
INSERT INTO `sso_org_role` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '404542be62d21014451808aa67f1f5df');
INSERT INTO `sso_org_role` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'af30f104a622b5de76add210dd33b361');
INSERT INTO `sso_org_role` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'a2569f85d5cd316f5c7011e43dc86271');
INSERT INTO `sso_org_role` VALUES ('a787082d9b7c177439a114995e4caff1', '624edadc0562c7fe9b54f0e3ba5e658b');

-- ----------------------------
-- Table structure for sso_org_user
-- ----------------------------
DROP TABLE IF EXISTS `sso_org_user`;
CREATE TABLE `sso_org_user`  (
                                 `org_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织ID',
                                 `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
                                 PRIMARY KEY (`org_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色用户关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_org_user
-- ----------------------------
INSERT INTO `sso_org_user` VALUES ('1', '1');
INSERT INTO `sso_org_user` VALUES ('404542be62d21014451808aa67f1f5df', 'c51fde3955594074bb4db31e654a4483');
INSERT INTO `sso_org_user` VALUES ('624edadc0562c7fe9b54f0e3ba5e658b', 'c51fde3955594074bb4db31e654a4483');
INSERT INTO `sso_org_user` VALUES ('a2569f85d5cd316f5c7011e43dc86271', 'bd54e030ae204be8b77e36cf48583f35');

-- ----------------------------
-- Table structure for sso_role
-- ----------------------------
DROP TABLE IF EXISTS `sso_role`;
CREATE TABLE `sso_role`  (
                             `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
                             `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
                             `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
                             `role_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
                             `role_sort` int(4) NULL DEFAULT NULL COMMENT '显示顺序',
                             `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0正常 1停用',
                             `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
                             `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_role
-- ----------------------------
INSERT INTO `sso_role` VALUES ('1', '1', '超级管理员', 'superAdmin', 1, 0, '超级管理员', 0, 'admin', '2022-09-19 10:21:49', 'mfish', '2023-07-04 11:22:32');
INSERT INTO `sso_role` VALUES ('210297727b74ecb505c1b4d97f76daee', '1', '测试', 'test', 3, 0, '测试角色', 0, 'admin', '2022-11-29 18:37:32', 'mfish', '2023-07-05 11:13:10');
INSERT INTO `sso_role` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a480b6861ca4a44631af794a99e77265', 'XXX管理员', 'xxxgly', 1, 0, NULL, 0, 'mfish', '2023-06-28 11:22:07', '', NULL);
INSERT INTO `sso_role` VALUES ('4063404b06e967bcf619bf86e7fe6359', 'a480b6861ca4a44631af794a99e77265', 'XXX运维', 'xxxyw', 2, 0, NULL, 0, 'mfish', '2023-06-28 11:22:46', 'mfish', '2023-07-05 11:43:00');
INSERT INTO `sso_role` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1', '管理', 'manage', 2, 0, '管理', 0, 'admin', '2023-05-30 23:41:46', 'admin', '2023-09-01 16:12:23');
INSERT INTO `sso_role` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '1', '运维', 'operate', 4, 0, '运维角色', 0, 'admin', '2022-11-30 16:18:51', 'mfish', '2023-07-04 17:25:48');
INSERT INTO `sso_role` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'a480b6861ca4a44631af794a99e77265', 'XXX开发', 'xxxkf', 3, 0, NULL, 0, 'mfish', '2023-06-28 11:23:10', 'mfish', '2023-09-14 16:08:27');
INSERT INTO `sso_role` VALUES ('a787082d9b7c177439a114995e4caff1', '1', '开发', 'develop', 5, 0, '开发角色', 0, 'admin', '2023-06-28 11:54:49', 'mfish', '2023-07-31 15:16:31');
INSERT INTO `sso_role` VALUES ('e77a579aaf0844ba493cde1811b137a9', '1', '个人', 'person', NULL, 0, NULL, 0, 'admin', '2023-07-19 15:26:04', '', NULL);

-- ----------------------------
-- Table structure for sso_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sso_role_menu`;
CREATE TABLE `sso_role_menu`  (
                                  `role_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
                                  `menu_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID',
                                  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_role_menu
-- ----------------------------
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '76f149981f1c86fce81f2f4cdb9674b9');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', 'f4a0ed4ca7a609aa8268399bdffcecfb');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '0f5a85a6fd5bdc9df26b826eec3c17f1');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '101cb161536a5a80731a4d6db0b5eeac');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '1a73215261f568088e9adeef2dbd8e44');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '25a5783ea03e26d7844b9b7370576236');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '2628fb3c4166d6469f06fcea9b9c0c55');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '2a4e024fdc76063da32926c63ca9ead2');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '369de1bef8d1e964414f25ec6d3156bc');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '3a74d165bcd286f102e10a1be8c23eef');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '3c4b2d0f7558d7f45a29fd9c6a7edea7');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '3d1efa154266719e6322808064df4b13');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '43ca0b3ba70c7ba5a2f91882f618208b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '4ef7029abe93c11601678ba16dac406f');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '5271166ced06a95d787dc049d3f19bd2');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '58efbcc5f46b95aeab069076031959e7');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '5ad16d38964bf541b6417b07ddf33d9b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '60efb66a88ab33b339718eb0d052a033');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '67dfbce31013ada62800425f72997962');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '6ac6bc8054107436e24356e3466f00db');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '6aee07bfe60f4ee4021bfce397a8f4df');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '6fd5cdaf86772d4db0587f3b9281f99b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '731738ed9bbd2e36456b790dfadcb84e');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '76f149981f1c86fce81f2f4cdb9674b9');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '79cbc9e257ee8f44db6b133c584ff86a');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '80526081fb00ce5dbe629ef358231909');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '81ecfe903cb3116f00c367678059c87c');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '93190e5d426f69cd2712aac373542698');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '967795af502129d318899a60716da84f');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '9840528e4a1efd3a510d8a087baf4bdb');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '9a293c164762776e0a876323a3363dec');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '9b9139c09668bb22888201b7e8a812c4');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', '9f46c219e3fc35b1c2ef3a95438b16bf');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a27822a74728632e0e0ed10d8285bf54');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a2cdbac934bae3da9987df0655db2455');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a7145a05342033be0caa4a8f1e262f8a');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'a988f38821885f8f8aaffa49d681aaac');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'addeaf01bc278e216de75ad26a8f27b6');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'b40f985b74d1dd6e311e21369beea8ea');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'bf2e6661d9a4d115f018ffd4ff202d92');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'c46042d6e6d16ea95df6461648833675');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'c487023e85c9aaf5510a03e8017b768c');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'c52a49da263d57d2c89edcbc9ca70a0a');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'c9eb585420911ee18335d935d3872934');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'ce1a05cdedf2d0684574a30dd3ed14f9');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'ce71591e16d47d6b4ff1d52c2bb83ae7');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'cfbdf3ce5297cebf806ac116fc239558');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'cfc0ca0b3c9220fa3cae26420cf0c51f');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'e48e7b98d2c0331ff6241514e97dad8b');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'ee3ae3a2161e8d58e2c62f340c3d7b55');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'f4a0ed4ca7a609aa8268399bdffcecfb');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'f634ba1d6d840fb1f945b4f811dd928d');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'f87d8b297eb3650834048dba7c8d2d89');
INSERT INTO `sso_role_menu` VALUES ('351591b8df6f3eed5d1c613aac6c5bc8', 'fb5dac5b0b9b610ed1e996108d6445b0');
INSERT INTO `sso_role_menu` VALUES ('4063404b06e967bcf619bf86e7fe6359', '0aa9f017545ec947a075f76e34c075c0');
INSERT INTO `sso_role_menu` VALUES ('4063404b06e967bcf619bf86e7fe6359', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('4063404b06e967bcf619bf86e7fe6359', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('4063404b06e967bcf619bf86e7fe6359', 'e159379c94b8fcc58ebc38cf8b322772');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '01ee419d3dcc28acf1920428391720b7');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '0526179a70fca38a69dd709dec2f1a81');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '08a567f09c8d90660c23f2b432e0e1d9');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '0aa9f017545ec947a075f76e34c075c0');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '101cb161536a5a80731a4d6db0b5eeac');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1a0aee8380c525e7c4802b1c4d587fa8');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1ce2ac44228e37c063e9cd55ed8f0a49');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '1e1b7d50ab93ffdeca33fe5b7006eb01');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '234dc900ad6502579a51784f9ddb05d5');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '238132a09b6f761374dfd205b6388245');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '25a5783ea03e26d7844b9b7370576236');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '2628fb3c4166d6469f06fcea9b9c0c55');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '268d140daddc00dc77823c7d7c2025fb');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '285bb37f32e878e24d5a22445c1bc5af');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '292791abfbf63b35ad9ce510c0a5823e');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '369de1bef8d1e964414f25ec6d3156bc');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '3a74d165bcd286f102e10a1be8c23eef');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '3a92fd411f0a70bf477e6dc354f4e29e');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '3c4b2d0f7558d7f45a29fd9c6a7edea7');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '40bf4846599bf8dbd307f77bf51a7dad');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '442f89857faa6bc929ef4f422b8c4b99');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '4527c6c05549e3594f135ac056faaece');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '4bfec85ae3174915cd2a3e8ddd822220');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '4cabb05e97ea7a738a2f7ce3c9d224d8');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '503e3ac379a2e17e99105b77a727e6db');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '520873645d565776988d81481d8b0d26');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5271166ced06a95d787dc049d3f19bd2');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5ad16d38964bf541b6417b07ddf33d9b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5b543a83371c766788047a1a1907cffd');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '5c723efc132b50c0284d79eaafed5a0f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '60efb66a88ab33b339718eb0d052a033');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '67dfbce31013ada62800425f72997962');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6a38a3847b66cc690c3a2eacedb4e81f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6ac6bc8054107436e24356e3466f00db');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6cedf51376c8998eb49e280cdba6c533');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6e02e0e621140968dc62a2ce3dfa198d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '6e491486dc4cb475e4bd037d06ab2801');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '731738ed9bbd2e36456b790dfadcb84e');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '74ce27a1e94091da2efa714e32680e7a');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '76f149981f1c86fce81f2f4cdb9674b9');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '76f68d05f5054818762718ee85d6d0fe');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '79cbc9e257ee8f44db6b133c584ff86a');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '7e87849f80699ad24292fd9908f5aeb8');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '80526081fb00ce5dbe629ef358231909');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '81ecfe903cb3116f00c367678059c87c');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '8360ab9544a00dc7d9f15594dd69e2ff');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '85cd52250e435c555622c268262f4c02');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '8ad60664c7060f811559bde09a79dae5');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '93190e5d426f69cd2712aac373542698');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '967795af502129d318899a60716da84f');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '9a293c164762776e0a876323a3363dec');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '9c6f4eff70d7b2048f63adf229c5d30d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '9d5397b6ddb4d194a95b05f42b80445b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', '9f46c219e3fc35b1c2ef3a95438b16bf');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'a43c057f48b54c9038719179cf9e284d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'a606083b203d32915c4d0e649c7b7c6b');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'a7145a05342033be0caa4a8f1e262f8a');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ad2ab62e13c7750dcb5b41b00cbdcf66');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'afbf598d2290b24a5e87e7547c05515d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'bcd18784374699438a215a9ab1e9b351');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'bea31d33d125895a9eaa827863341a91');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'bf2e6661d9a4d115f018ffd4ff202d92');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'c36420436629884000e73b158166f260');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'c52a49da263d57d2c89edcbc9ca70a0a');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ca0a3c9ae9cd551ee4e1b727861b7c78');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ce1a05cdedf2d0684574a30dd3ed14f9');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'cfbdf3ce5297cebf806ac116fc239558');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'd9832cd3aefbb5f99267edb995ff8c75');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'e159379c94b8fcc58ebc38cf8b322772');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'e1996e92ac6cf37c0c2e40825a7af472');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'eb5f513d5430597d3ea312e1bf760b23');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'eda0153e492e86ade2fe6702d267fef3');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'ee34f5aec6a2220f57fa151a147ede3c');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'f4a0ed4ca7a609aa8268399bdffcecfb');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'f634ba1d6d840fb1f945b4f811dd928d');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'f87d8b297eb3650834048dba7c8d2d89');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'fa2211276b7b84a141667ec9ea8d33a4');
INSERT INTO `sso_role_menu` VALUES ('4b423f7b1ac0ed0b46a8e5ec3389ac14', 'fb5dac5b0b9b610ed1e996108d6445b0');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '0aa9f017545ec947a075f76e34c075c0');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'e159379c94b8fcc58ebc38cf8b322772');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '01ee419d3dcc28acf1920428391720b7');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '285bb37f32e878e24d5a22445c1bc5af');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '292791abfbf63b35ad9ce510c0a5823e');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '369de1bef8d1e964414f25ec6d3156bc');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '5ad16d38964bf541b6417b07ddf33d9b');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '6aee07bfe60f4ee4021bfce397a8f4df');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '6cedf51376c8998eb49e280cdba6c533');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '70943d8248fd8f77ade038d9afa0bf33');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '81ecfe903cb3116f00c367678059c87c');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '8360ab9544a00dc7d9f15594dd69e2ff');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', '85cd52250e435c555622c268262f4c02');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'a606083b203d32915c4d0e649c7b7c6b');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'afbf598d2290b24a5e87e7547c05515d');
INSERT INTO `sso_role_menu` VALUES ('86ec59a2a3261fd6ba4098da034965ad', 'eda0153e492e86ade2fe6702d267fef3');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '0526179a70fca38a69dd709dec2f1a81');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '1a0aee8380c525e7c4802b1c4d587fa8');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '1ce2ac44228e37c063e9cd55ed8f0a49');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '292791abfbf63b35ad9ce510c0a5823e');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '5c723efc132b50c0284d79eaafed5a0f');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '6cedf51376c8998eb49e280cdba6c533');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '6e491486dc4cb475e4bd037d06ab2801');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '70943d8248fd8f77ade038d9afa0bf33');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '8360ab9544a00dc7d9f15594dd69e2ff');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', '85cd52250e435c555622c268262f4c02');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', 'a606083b203d32915c4d0e649c7b7c6b');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', 'ee34f5aec6a2220f57fa151a147ede3c');
INSERT INTO `sso_role_menu` VALUES ('a787082d9b7c177439a114995e4caff1', 'fa2211276b7b84a141667ec9ea8d33a4');
INSERT INTO `sso_role_menu` VALUES ('e77a579aaf0844ba493cde1811b137a9', '6d71e92a5a3712acbcfcc58f65b93f4f');
INSERT INTO `sso_role_menu` VALUES ('e77a579aaf0844ba493cde1811b137a9', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('e77a579aaf0844ba493cde1811b137a9', 'eb5f513d5430597d3ea312e1bf760b23');

-- ----------------------------
-- Table structure for sso_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sso_tenant`;
CREATE TABLE `sso_tenant`  (
                               `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                               `tenant_type` tinyint(4) NULL DEFAULT NULL COMMENT '租户类型 0 个人 1 企业',
                               `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户名称',
                               `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市',
                               `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份',
                               `county` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区县',
                               `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
                               `corp_size` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司规模',
                               `corp_years` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '营业年限',
                               `trade` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属行业',
                               `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态 0正常 1注销',
                               `logo` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'logo',
                               `domain` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域名',
                               `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除状态(0-正常,1-已删除)',
                               `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID，关联用户为管理员',
                               `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新用户',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_tenant
-- ----------------------------
INSERT INTO `sso_tenant` VALUES ('1', 1, '系统默认', NULL, NULL, NULL, '南京市秦淮区', 'l', '10', '科技推广和应用服务业', 0, 'bcfe86a96a6543ccac0b1700ad3aa5ca.png', 'http://www.mfish.com.cn', 0, '1', 'admin', '2023-06-20 23:30:00', 'admin', '2023-07-05 15:27:04');
INSERT INTO `sso_tenant` VALUES ('a480b6861ca4a44631af794a99e77265', 1, '南京XXX公司', NULL, NULL, NULL, '江苏南京XXX区XX号', 'xs', '3', '软件和信息技术服务业', 0, '5131c2efe1c94c3489815ff7d1ad0bed.png', 'http://mfish.com.cn', 0, 'c51fde3955594074bb4db31e654a4483', 'admin', '2023-06-28 11:17:09', 'admin', '2023-07-05 16:12:20');

-- ----------------------------
-- Table structure for sso_user
-- ----------------------------
DROP TABLE IF EXISTS `sso_user`;
CREATE TABLE `sso_user`  (
                             `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                             `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
                             `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
                             `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                             `old_password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '旧密码',
                             `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                             `head_img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
                             `telephone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
                             `birthday` date NULL DEFAULT NULL COMMENT '生日',
                             `sex` tinyint(1) NULL DEFAULT 1 COMMENT '性别(1男0女)',
                             `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0正常 1停用',
                             `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
                             `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐',
                             `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信唯一id',
                             `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                             `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `account_index`(`account`) USING BTREE,
                             UNIQUE INDEX `openid_index`(`openid`) USING BTREE,
                             UNIQUE INDEX `phone_index`(`phone`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_user
-- ----------------------------
INSERT INTO `sso_user` VALUES ('1', 'admin', '18900000001', 'mfish@qq.com', '66d4ae108116cb9044c8b9ccae4a0b6c', '22d374999f108f1573aad145657ed698', '管理员', '77f39fdc429142bf9b2b1fff631098ef.png', '02512345678', '1984-08-08', 1, 0, 0, '452187570f682f2ddb35a216fd32460d', 'olbL54qA8qAccFNtModx6dM-Ha6w', '超级管理员', '', '2017-04-10 15:21:38', 'admin', '2023-06-28 13:05:17');
INSERT INTO `sso_user` VALUES ('bd54e030ae204be8b77e36cf48583f35', 'xzh', '18911111111', 'liudehua@qq.com', 'b16e23b22364b73715fe678b8493a250', NULL, '新租户', '08170556fd804908812a2768380896e3.png', NULL, NULL, 1, 0, 0, '57c61f5a32e75728843b7f6d0cf38cb1', NULL, NULL, 'mfish', '2023-07-04 10:06:06', 'admin', '2023-07-19 15:26:17');
INSERT INTO `sso_user` VALUES ('c51fde3955594074bb4db31e654a4483', 'mfish', '18900000002', 'mfish2@qq.com', '997ba196adf807d32b030d2ee8e242f9', NULL, '摸鱼', NULL, '', '2023-06-28', 1, 0, 0, '2833ba3ab03c7fb357cf5d71676146e8', NULL, '', 'admin', '2023-06-28 11:14:45', 'mfish', '2023-07-25 11:35:25');

-- ----------------------------
-- Table structure for sso_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sso_user_role`;
CREATE TABLE `sso_user_role`  (
                                  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
                                  `role_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
                                  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_user_role
-- ----------------------------
INSERT INTO `sso_user_role` VALUES ('1', '1');
INSERT INTO `sso_user_role` VALUES ('bd54e030ae204be8b77e36cf48583f35', 'e77a579aaf0844ba493cde1811b137a9');

SET FOREIGN_KEY_CHECKS = 1;
