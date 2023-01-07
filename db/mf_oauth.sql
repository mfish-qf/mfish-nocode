/*
 Navicat Premium Data Transfer

 Source Server         : home_mysql
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : app.mfish.com.cn:18119
 Source Schema         : mf_oauth

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 07/01/2023 16:35:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sso_client_details
-- ----------------------------
DROP TABLE IF EXISTS `sso_client_details`;
CREATE TABLE `sso_client_details`  (
  `client_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用于唯一标识每一个客户端(client)；注册时必须填写(也可以服务端自动生成)，这个字段是必须的，实际应用也有叫app_key',
  `resource_ids` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端能访问的资源id集合，注册客户端时，根据实际需要可选择资源id，也可以根据不同的额注册流程，赋予对应的额资源id',
  `client_secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册填写或者服务端自动生成，实际应用也有叫app_secret, 必须要有前缀代表加密方式',
  `scope` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指定client的权限范围，比如读写权限，比如移动端还是web端权限',
  `authorized_grant_types` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可选值 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token, 隐式模式: implicit: 客户端模式: client_credentials。支持多个用逗号分隔\n\n作者：谢海凡\n链接：https://www.jianshu.com/p/c1c6c966c3a7\n来源：简书\n著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。',
  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端重定向uri，authorization_code和implicit需要该值进行校验，注册时填写，',
  `authorities` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指定用户的权限范围，如果授权的过程需要用户登陆，该字段不生效，implicit和client_credentials需要',
  `access_token_validity` int(11) NULL DEFAULT NULL COMMENT '设置access_token的有效时间(秒),默认(606012,12小时)',
  `refresh_token_validity` int(11) NULL DEFAULT NULL COMMENT '设置refresh_token有效期(秒)，默认(606024*30, 30填)',
  `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '这是一个预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据',
  `autoapprove` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认false,适用于authorization_code模式,设置用户是否自动approval操作,设置true跳过用户确认授权操作页面，直接跳到redirect_uri',
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户端信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_client_details
-- ----------------------------
INSERT INTO `sso_client_details` VALUES ('system', NULL, 'system', 'all', 'authorization_code,password,refresh_token', 'http://baidu.com', NULL, 28800, NULL, NULL, 'true');

-- ----------------------------
-- Table structure for sso_client_user
-- ----------------------------
DROP TABLE IF EXISTS `sso_client_user`;
CREATE TABLE `sso_client_user`  (
  `client_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`client_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户端用户关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_client_user
-- ----------------------------
INSERT INTO `sso_client_user` VALUES ('system', '0b13f982db33481e8e1e5e84f6df7c3e');
INSERT INTO `sso_client_user` VALUES ('system', '1');
INSERT INTO `sso_client_user` VALUES ('system', '40062f1156ef42b9b3a341462c927fb6');
INSERT INTO `sso_client_user` VALUES ('system', '4ef9999a1cd0492db32c87d97659b963');
INSERT INTO `sso_client_user` VALUES ('system', 'f4056d9589a64146a7538f04c6bcc10f');

-- ----------------------------
-- Table structure for sso_menu
-- ----------------------------
DROP TABLE IF EXISTS `sso_menu`;
CREATE TABLE `sso_menu`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID',
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '父菜单ID',
  `client_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
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
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_menu_code`(`menu_code`) USING BTREE COMMENT '菜单编码索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_menu
-- ----------------------------
INSERT INTO `sso_menu` VALUES ('0526179a70fca38a69dd709dec2f1a81', '6e491486dc4cb475e4bd037d06ab2801', 'system', '0000300004', 2, 'Vben文档', 'ion:social-vimeo-outline', 4, 1, '/vben', 'https://doc.vvbin.cn/guide/introduction.html', NULL, 0, 1, NULL, 1, '', 'admin', '2022-12-15 09:14:09', '', NULL);
INSERT INTO `sso_menu` VALUES ('0f5a85a6fd5bdc9df26b826eec3c17f1', '2a4e024fdc76063da32926c63ca9ead2', 'system', '0000200004', 2, '角色管理', 'ion:ios-key', 3, 1, '/role', '/sys/role/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-11-30 17:32:14', 'admin', '2022-12-14 14:25:37');
INSERT INTO `sso_menu` VALUES ('1a0aee8380c525e7c4802b1c4d587fa8', '6e491486dc4cb475e4bd037d06ab2801', 'system', '0000300001', 2, '接口地址', 'ion:book-sharp', 1, 1, '/swagger', 'http://localhost:8888/swagger-ui/index.html', NULL, 0, 1, NULL, 0, '', 'admin', '2022-12-14 10:25:31', 'admin', '2022-12-14 22:52:39');
INSERT INTO `sso_menu` VALUES ('1a73215261f568088e9adeef2dbd8e44', 'a988f38821885f8f8aaffa49d681aaac', 'system', '000020000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:menu:delete,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:05:36', 'admin', '2022-12-20 11:34:00');
INSERT INTO `sso_menu` VALUES ('1ce2ac44228e37c063e9cd55ed8f0a49', '6e491486dc4cb475e4bd037d06ab2801', 'system', '0000300002', 2, 'Git地址', 'ion:logo-github', 2, 1, '/git', 'https://github.com/mfish-qf/mfish-nocode', NULL, 1, 1, NULL, 1, '', 'admin', '2022-12-14 15:27:03', 'admin', '2022-12-14 22:52:51');
INSERT INTO `sso_menu` VALUES ('234dc900ad6502579a51784f9ddb05d5', '76f68d05f5054818762718ee85d6d0fe', 'system', '000010000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:workbench:insert', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:57:09', 'admin', '2022-11-30 16:59:05');
INSERT INTO `sso_menu` VALUES ('268d140daddc00dc77823c7d7c2025fb', '76f68d05f5054818762718ee85d6d0fe', 'system', '000010000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:workbench:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:56:30', 'admin', '2022-11-30 16:58:57');
INSERT INTO `sso_menu` VALUES ('2a4e024fdc76063da32926c63ca9ead2', '', 'system', '00002', 1, '系统管理', 'ant-design:setting-outlined', 2, 0, '/system', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:59:57', '', NULL);
INSERT INTO `sso_menu` VALUES ('4527c6c05549e3594f135ac056faaece', '', 'system', '00004', 1, '引导页', 'whh:paintroll', 5, 1, '/setup', '/demo/setup/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-11-08 17:11:09', 'admin', '2022-12-15 21:04:42');
INSERT INTO `sso_menu` VALUES ('4bfec85ae3174915cd2a3e8ddd822220', '', 'system', '00005', 1, '关于', 'simple-icons:about-dot-me', 6, 1, '/about', '/sys/about/index.vue', '', 0, 1, NULL, 0, '', 'admin', '2022-11-08 17:13:12', 'admin', '2022-12-21 21:55:08');
INSERT INTO `sso_menu` VALUES ('4ef7029abe93c11601678ba16dac406f', '2a4e024fdc76063da32926c63ca9ead2', 'system', '0000200002', 2, '帐号管理', 'ion:people-sharp', 4, 1, '/account', '/sys/account/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-30 16:40:28', 'admin', '2023-01-04 21:03:33');
INSERT INTO `sso_menu` VALUES ('503e3ac379a2e17e99105b77a727e6db', '', 'system', '00001', 1, '驾驶舱', 'ant-design:appstore-outlined', 1, 0, '/dashboard', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:53:57', 'admin', '2022-12-19 21:54:18');
INSERT INTO `sso_menu` VALUES ('58efbcc5f46b95aeab069076031959e7', 'addeaf01bc278e216de75ad26a8f27b6', 'system', '000020000300003', 3, '修改', '#', 3, 2, '', NULL, 'sys:org:update,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:28:45', 'admin', '2022-12-20 11:45:25');
INSERT INTO `sso_menu` VALUES ('5b543a83371c766788047a1a1907cffd', '9c6f4eff70d7b2048f63adf229c5d30d', 'system', '0000600001', 2, '目录1', 'ion:folder-open-outline', 1, 0, '/menu1', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-12-14 17:03:39', 'admin', '2022-12-15 20:35:07');
INSERT INTO `sso_menu` VALUES ('5c723efc132b50c0284d79eaafed5a0f', '6e491486dc4cb475e4bd037d06ab2801', 'system', '0000300003', 2, 'AntDesign文档', 'ion:social-angular-outline', 3, 1, '/ant', 'https://2x.antdv.com/docs/vue/introduce-cn/', NULL, 0, 1, NULL, 1, '', 'admin', '2022-12-14 15:38:22', 'admin', '2022-12-15 14:40:11');
INSERT INTO `sso_menu` VALUES ('67dfbce31013ada62800425f72997962', '2a4e024fdc76063da32926c63ca9ead2', 'system', '0000200006', 2, '字典项', 'ion:ios-menu', 6, 1, '/dict/:dictCode', '/sys/dictItem/index.vue', NULL, 0, 0, '0000200005', 0, '', 'admin', '2023-01-03 17:07:39', 'admin', '2023-01-04 14:51:30');
INSERT INTO `sso_menu` VALUES ('6a38a3847b66cc690c3a2eacedb4e81f', '76f68d05f5054818762718ee85d6d0fe', 'system', '000010000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:workbench:update', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:57:42', 'admin', '2022-11-30 16:59:13');
INSERT INTO `sso_menu` VALUES ('6ac6bc8054107436e24356e3466f00db', '4ef7029abe93c11601678ba16dac406f', 'system', '000020000200001', 3, '查询', '#', 1, 2, '', NULL, 'sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 16:54:15', 'admin', '2022-12-20 11:46:51');
INSERT INTO `sso_menu` VALUES ('6e491486dc4cb475e4bd037d06ab2801', '', 'system', '00003', 1, '项目文档', 'ion:book-outline', 3, 0, '/doc', NULL, NULL, 1, 1, NULL, NULL, '', 'admin', '2022-11-08 17:08:24', 'admin', '2022-12-13 18:32:31');
INSERT INTO `sso_menu` VALUES ('6fd5cdaf86772d4db0587f3b9281f99b', 'a988f38821885f8f8aaffa49d681aaac', 'system', '000020000100003', 3, '修改', '#', 3, 2, '', NULL, 'sys:menu:update,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:05:12', 'admin', '2022-12-20 11:33:37');
INSERT INTO `sso_menu` VALUES ('75882dc140444e061741fbd9f026dd2b', 'a988f38821885f8f8aaffa49d681aaac', 'system', '000020000100001', 3, '查询', '#', 1, 2, '', NULL, 'sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:04:16', 'admin', '2022-11-30 16:59:33');
INSERT INTO `sso_menu` VALUES ('76f149981f1c86fce81f2f4cdb9674b9', 'addeaf01bc278e216de75ad26a8f27b6', 'system', '000020000300001', 3, '查询', '#', 1, 2, '', NULL, 'sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:26:51', '', NULL);
INSERT INTO `sso_menu` VALUES ('76f68d05f5054818762718ee85d6d0fe', '503e3ac379a2e17e99105b77a727e6db', 'system', '0000100001', 2, '工作台', 'ant-design:calendar-outlined', 1, 1, '/workbench', '/dashboard/workbench/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-11-08 16:55:25', 'admin', '2022-12-14 21:58:47');
INSERT INTO `sso_menu` VALUES ('7e690410346c4d3a1610d85e8c9f906b', '2a4e024fdc76063da32926c63ca9ead2', 'system', '0000200007', 2, '个人设置', 'ion:person', 7, 1, '/account/setting', '/sys/account/setting/index.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2023-01-04 18:14:47', 'admin', '2023-01-04 21:03:23');
INSERT INTO `sso_menu` VALUES ('7e87849f80699ad24292fd9908f5aeb8', '76f68d05f5054818762718ee85d6d0fe', 'system', '000010000100004', 3, '删除', '#', 4, 2, '', NULL, 'sys:workbench:delete', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 16:58:31', 'admin', '2022-11-30 16:59:24');
INSERT INTO `sso_menu` VALUES ('8ad60664c7060f811559bde09a79dae5', '5b543a83371c766788047a1a1907cffd', 'system', '000060000100001', 3, '目录2', 'ion:folder-open-outline', 1, 0, '/menu2', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-12-14 17:04:20', 'admin', '2023-01-03 20:27:11');
INSERT INTO `sso_menu` VALUES ('967795af502129d318899a60716da84f', 'a988f38821885f8f8aaffa49d681aaac', 'system', '000020000100002', 3, '新增', '#', 2, 2, '', NULL, 'sys:menu:insert,sys:menu:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-08 17:04:45', 'admin', '2022-12-20 11:30:37');
INSERT INTO `sso_menu` VALUES ('9b9139c09668bb22888201b7e8a812c4', '0f5a85a6fd5bdc9df26b826eec3c17f1', 'system', '000020000400003', 3, '修改', '#', 3, 2, '', NULL, 'sys:role:update,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:58:08', 'admin', '2022-12-20 11:46:23');
INSERT INTO `sso_menu` VALUES ('9c6f4eff70d7b2048f63adf229c5d30d', '', 'system', '00006', 1, '多级目录', 'ion:folder-open-outline', 4, 0, '/level', NULL, NULL, 0, 1, NULL, NULL, '', 'admin', '2022-12-14 17:03:01', 'admin', '2022-12-15 21:04:32');
INSERT INTO `sso_menu` VALUES ('9f46c219e3fc35b1c2ef3a95438b16bf', '4ef7029abe93c11601678ba16dac406f', 'system', '000020000200002', 3, '新增', '#', 2, 2, '', NULL, 'sys:account:insert,sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:02', 'admin', '2022-12-20 11:47:06');
INSERT INTO `sso_menu` VALUES ('a27822a74728632e0e0ed10d8285bf54', '4ef7029abe93c11601678ba16dac406f', 'system', '000020000200004', 3, '删除', '#', 4, 2, '', NULL, 'sys:account:delete,  sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:48', 'admin', '2022-12-20 11:47:15');
INSERT INTO `sso_menu` VALUES ('a988f38821885f8f8aaffa49d681aaac', '2a4e024fdc76063da32926c63ca9ead2', 'system', '0000200001', 2, '菜单管理', 'ion:ios-menu', 1, 1, '/menu', '/sys/menu/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-08 17:02:02', 'admin', '2022-12-14 14:25:17');
INSERT INTO `sso_menu` VALUES ('addeaf01bc278e216de75ad26a8f27b6', '2a4e024fdc76063da32926c63ca9ead2', 'system', '0000200003', 2, '组织管理', 'ion:ios-people', 2, 1, '/org', '/sys/org/index.vue', '', 0, 1, NULL, 1, '', 'admin', '2022-11-30 17:22:12', 'admin', '2023-01-03 20:27:42');
INSERT INTO `sso_menu` VALUES ('bcd18784374699438a215a9ab1e9b351', '8ad60664c7060f811559bde09a79dae5', 'system', '00006000010000100001', 4, '多级菜单', 'ant-design:appstore-outlined', 1, 1, '/menu3', '/demo/level/LevelMenu.vue', NULL, 0, 1, NULL, 1, '', 'admin', '2022-12-14 17:05:12', 'admin', '2022-12-14 17:22:27');
INSERT INTO `sso_menu` VALUES ('c46042d6e6d16ea95df6461648833675', '4ef7029abe93c11601678ba16dac406f', 'system', '000020000200003', 3, '修改', '#', 3, 2, '', NULL, 'sys:account:update,sys:account:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:03:23', 'admin', '2022-12-17 22:26:35');
INSERT INTO `sso_menu` VALUES ('c487023e85c9aaf5510a03e8017b768c', '0f5a85a6fd5bdc9df26b826eec3c17f1', 'system', '000020000400004', 3, '删除', '#', 4, 2, '', NULL, 'sys:role:delete,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 18:02:10', 'admin', '2022-12-20 11:46:13');
INSERT INTO `sso_menu` VALUES ('c9eb585420911ee18335d935d3872934', '2a4e024fdc76063da32926c63ca9ead2', 'system', '0000200005', 2, '字典管理', 'ion:ios-list', 5, 1, '/dict', '/sys/dict/index.vue', NULL, 0, 1, NULL, NULL, '', 'admin', '2022-11-30 18:08:11', 'admin', '2022-12-14 14:26:05');
INSERT INTO `sso_menu` VALUES ('ee3ae3a2161e8d58e2c62f340c3d7b55', 'addeaf01bc278e216de75ad26a8f27b6', 'system', '000020000300004', 3, '删除', '#', 4, 2, '', NULL, 'sys:org:delete,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:29:49', 'admin', '2022-12-20 11:45:40');
INSERT INTO `sso_menu` VALUES ('f4a0ed4ca7a609aa8268399bdffcecfb', '0f5a85a6fd5bdc9df26b826eec3c17f1', 'system', '000020000400001', 3, '查询', '#', 1, 2, '', NULL, 'sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:53:00', '', NULL);
INSERT INTO `sso_menu` VALUES ('f87d8b297eb3650834048dba7c8d2d89', 'addeaf01bc278e216de75ad26a8f27b6', 'system', '000020000300002', 3, '新增', '#', 2, 2, '', NULL, 'sys:org:insert,sys:org:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:27:41', 'admin', '2022-12-20 11:34:47');
INSERT INTO `sso_menu` VALUES ('fb5dac5b0b9b610ed1e996108d6445b0', '0f5a85a6fd5bdc9df26b826eec3c17f1', 'system', '000020000400002', 3, '新增', '#', 2, 2, '', NULL, 'sys:role:insert,sys:role:query', 0, 1, NULL, NULL, '', 'admin', '2022-11-30 17:54:39', 'admin', '2022-12-20 11:46:05');

-- ----------------------------
-- Table structure for sso_org
-- ----------------------------
DROP TABLE IF EXISTS `sso_org`;
CREATE TABLE `sso_org`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织ID',
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '父组织ID',
  `client_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
  `org_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '组织编码',
  `org_level` tinyint(4) NULL DEFAULT NULL COMMENT '组织级别',
  `org_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '组织名称',
  `org_sort` int(4) NULL DEFAULT 0 COMMENT '排序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `org_code_index`(`org_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组织结构表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_org
-- ----------------------------
INSERT INTO `sso_org` VALUES ('3c8c6bc136bec3ea74f1e48237e83702', '950736539e99c0521531cc127d5b8712', 'system', '0000100002', 2, '摸鱼二部', 2, 'mfish2', '18922222222', '22@qq.com', '摸鱼二部', 0, 0, 'admin', '2022-11-12 11:09:47', 'admin', '2022-11-30 16:30:44');
INSERT INTO `sso_org` VALUES ('49b4cfe17e0cf4b5472aadea0f63bc57', '950736539e99c0521531cc127d5b8712', 'system', '0000100003', 2, '摸鱼测试部', 3, 'test', '18933333333', NULL, '摸鱼测试部', 0, 0, 'admin', '2022-12-02 22:20:58', 'admin', '2022-12-02 22:21:06');
INSERT INTO `sso_org` VALUES ('950736539e99c0521531cc127d5b8712', '', 'system', '00001', 1, '摸鱼事业部', 1, 'mfish', '18911111111', '11@qq.com', '摸鱼事业部', 0, 0, 'admin', '2022-11-12 10:57:36', 'admin', '2023-01-04 17:53:35');
INSERT INTO `sso_org` VALUES ('bb94731770f981fae7eec5cbb1b32bb3', '950736539e99c0521531cc127d5b8712', 'system', '0000100001', 2, '摸鱼一部', 1, 'mfish1', '18922222222', '22@qq.com', '摸鱼一部', 0, 0, 'admin', '2022-11-12 11:07:12', 'admin', '2023-01-04 17:53:30');

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
INSERT INTO `sso_org_user` VALUES ('3c8c6bc136bec3ea74f1e48237e83702', '0b13f982db33481e8e1e5e84f6df7c3e');
INSERT INTO `sso_org_user` VALUES ('3c8c6bc136bec3ea74f1e48237e83702', '40062f1156ef42b9b3a341462c927fb6');
INSERT INTO `sso_org_user` VALUES ('3c8c6bc136bec3ea74f1e48237e83702', '4ef9999a1cd0492db32c87d97659b963');
INSERT INTO `sso_org_user` VALUES ('950736539e99c0521531cc127d5b8712', '1');
INSERT INTO `sso_org_user` VALUES ('bb94731770f981fae7eec5cbb1b32bb3', 'f4056d9589a64146a7538f04c6bcc10f');

-- ----------------------------
-- Table structure for sso_role
-- ----------------------------
DROP TABLE IF EXISTS `sso_role`;
CREATE TABLE `sso_role`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `client_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `role_sort` int(4) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0正常 1停用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_role
-- ----------------------------
INSERT INTO `sso_role` VALUES ('1', 'system', '超级管理员', 'superAdmin', 1, 0, '超级管理员', 0, 'admin', '2022-09-19 10:21:49', 'admin', '2023-01-04 17:52:45');
INSERT INTO `sso_role` VALUES ('210297727b74ecb505c1b4d97f76daee', 'system', '测试', 'test', 2, 0, '测试角色', 0, 'admin', '2022-11-29 18:37:32', 'admin', '2023-01-04 21:11:29');
INSERT INTO `sso_role` VALUES ('4a470c08a4014485d190b5f4141ae571', 'system', 'ss', 'sss', 2, 0, 'ss', 1, 'admin', '2022-12-21 22:08:02', 'admin', '2022-12-21 22:08:25');
INSERT INTO `sso_role` VALUES ('57ad11f7d8d94e2664f4d772a6dd9d7d', 'system', '测试1', 'test1', 3, 0, '测试角色1', 0, 'admin', '2022-11-30 12:09:59', 'admin', '2022-12-19 21:25:12');
INSERT INTO `sso_role` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'system', '运维', 'operate', 4, 0, '运维角色', 0, 'admin', '2022-11-30 16:18:51', 'admin', '2022-12-02 21:34:04');

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
INSERT INTO `sso_role_menu` VALUES ('1', '0526179a70fca38a69dd709dec2f1a81');
INSERT INTO `sso_role_menu` VALUES ('1', '0f5a85a6fd5bdc9df26b826eec3c17f1');
INSERT INTO `sso_role_menu` VALUES ('1', '1a0aee8380c525e7c4802b1c4d587fa8');
INSERT INTO `sso_role_menu` VALUES ('1', '1a73215261f568088e9adeef2dbd8e44');
INSERT INTO `sso_role_menu` VALUES ('1', '1ce2ac44228e37c063e9cd55ed8f0a49');
INSERT INTO `sso_role_menu` VALUES ('1', '234dc900ad6502579a51784f9ddb05d5');
INSERT INTO `sso_role_menu` VALUES ('1', '268d140daddc00dc77823c7d7c2025fb');
INSERT INTO `sso_role_menu` VALUES ('1', '2a4e024fdc76063da32926c63ca9ead2');
INSERT INTO `sso_role_menu` VALUES ('1', '4527c6c05549e3594f135ac056faaece');
INSERT INTO `sso_role_menu` VALUES ('1', '4bfec85ae3174915cd2a3e8ddd822220');
INSERT INTO `sso_role_menu` VALUES ('1', '4ef7029abe93c11601678ba16dac406f');
INSERT INTO `sso_role_menu` VALUES ('1', '503e3ac379a2e17e99105b77a727e6db');
INSERT INTO `sso_role_menu` VALUES ('1', '58efbcc5f46b95aeab069076031959e7');
INSERT INTO `sso_role_menu` VALUES ('1', '5b543a83371c766788047a1a1907cffd');
INSERT INTO `sso_role_menu` VALUES ('1', '5c723efc132b50c0284d79eaafed5a0f');
INSERT INTO `sso_role_menu` VALUES ('1', '6a38a3847b66cc690c3a2eacedb4e81f');
INSERT INTO `sso_role_menu` VALUES ('1', '6ac6bc8054107436e24356e3466f00db');
INSERT INTO `sso_role_menu` VALUES ('1', '6e491486dc4cb475e4bd037d06ab2801');
INSERT INTO `sso_role_menu` VALUES ('1', '6fd5cdaf86772d4db0587f3b9281f99b');
INSERT INTO `sso_role_menu` VALUES ('1', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('1', '76f149981f1c86fce81f2f4cdb9674b9');
INSERT INTO `sso_role_menu` VALUES ('1', '76f68d05f5054818762718ee85d6d0fe');
INSERT INTO `sso_role_menu` VALUES ('1', '7e87849f80699ad24292fd9908f5aeb8');
INSERT INTO `sso_role_menu` VALUES ('1', '8ad60664c7060f811559bde09a79dae5');
INSERT INTO `sso_role_menu` VALUES ('1', '967795af502129d318899a60716da84f');
INSERT INTO `sso_role_menu` VALUES ('1', '9b9139c09668bb22888201b7e8a812c4');
INSERT INTO `sso_role_menu` VALUES ('1', '9c6f4eff70d7b2048f63adf229c5d30d');
INSERT INTO `sso_role_menu` VALUES ('1', '9f46c219e3fc35b1c2ef3a95438b16bf');
INSERT INTO `sso_role_menu` VALUES ('1', 'a27822a74728632e0e0ed10d8285bf54');
INSERT INTO `sso_role_menu` VALUES ('1', 'a988f38821885f8f8aaffa49d681aaac');
INSERT INTO `sso_role_menu` VALUES ('1', 'addeaf01bc278e216de75ad26a8f27b6');
INSERT INTO `sso_role_menu` VALUES ('1', 'b');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '7e690410346c4d3a1610d85e8c9f906b');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', '9b9139c09668bb22888201b7e8a812c4');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', 'a27822a74728632e0e0ed10d8285bf54');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', 'c46042d6e6d16ea95df6461648833675');
INSERT INTO `sso_role_menu` VALUES ('210297727b74ecb505c1b4d97f76daee', 'f87d8b297eb3650834048dba7c8d2d89');
INSERT INTO `sso_role_menu` VALUES ('4a470c08a4014485d190b5f4141ae571', '268d140daddc00dc77823c7d7c2025fb');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '0f5a85a6fd5bdc9df26b826eec3c17f1');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '1a73215261f568088e9adeef2dbd8e44');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '234dc900ad6502579a51784f9ddb05d5');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '268d140daddc00dc77823c7d7c2025fb');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '4ef7029abe93c11601678ba16dac406f');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '503e3ac379a2e17e99105b77a727e6db');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '58efbcc5f46b95aeab069076031959e7');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '6a38a3847b66cc690c3a2eacedb4e81f');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '6ac6bc8054107436e24356e3466f00db');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '6fd5cdaf86772d4db0587f3b9281f99b');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '75882dc140444e061741fbd9f026dd2b');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '76f149981f1c86fce81f2f4cdb9674b9');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '76f68d05f5054818762718ee85d6d0fe');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '7e87849f80699ad24292fd9908f5aeb8');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '967795af502129d318899a60716da84f');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '9b9139c09668bb22888201b7e8a812c4');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', '9f46c219e3fc35b1c2ef3a95438b16bf');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'a27822a74728632e0e0ed10d8285bf54');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'a988f38821885f8f8aaffa49d681aaac');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'addeaf01bc278e216de75ad26a8f27b6');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'c46042d6e6d16ea95df6461648833675');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'c487023e85c9aaf5510a03e8017b768c');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'ee3ae3a2161e8d58e2c62f340c3d7b55');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'f4a0ed4ca7a609aa8268399bdffcecfb');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'f87d8b297eb3650834048dba7c8d2d89');
INSERT INTO `sso_role_menu` VALUES ('67e95f5e81b8da9a8f70db7540b7409d', 'fb5dac5b0b9b610ed1e996108d6445b0');

-- ----------------------------
-- Table structure for sso_user
-- ----------------------------
DROP TABLE IF EXISTS `sso_user`;
CREATE TABLE `sso_user`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `old_password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '旧密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `head_img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `telephone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `sex` tinyint(1) NULL DEFAULT NULL COMMENT '性别(1男0女)',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态 0正常 1停用',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐',
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信唯一id',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_index`(`account`) USING BTREE,
  UNIQUE INDEX `openid_index`(`openid`) USING BTREE,
  UNIQUE INDEX `phone_index`(`phone`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_user
-- ----------------------------
INSERT INTO `sso_user` VALUES ('0b13f982db33481e8e1e5e84f6df7c3e', 'test2', '18922222222', 'test2@qq.com', '68513a46ed069540e2665eaf3cad4475', NULL, '测试2', NULL, '02522212341', '2021-12-08', 1, 0, 1, '9706bf5fef1652afa3c89520f0f74aed', NULL, NULL, 'admin', '2022-12-01 17:34:02', 'admin', '2022-12-05 23:32:15');
INSERT INTO `sso_user` VALUES ('1', 'admin', '18911111111', 'mfish@qq.com', '22d374999f108f1573aad145657ed698', '', '管理员', 'aebae0b309f648a086f1d2528e5e554e.png', '02512345678', '1998-06-14', 1, 0, 0, '452187570f682f2ddb35a216fd32460d', '', '管理员', '', '2017-04-10 15:21:38', 'admin', '2023-01-07 14:53:06');
INSERT INTO `sso_user` VALUES ('40062f1156ef42b9b3a341462c927fb6', 'test', '18922222220', 'test@qq.com', 'e7bf117daf1732f94ba3590f7df80ba2', '', '测试', NULL, '02522222222', '2022-11-24', 1, 0, 0, '272cb4e5912be9cf6f371c13a28ea030', NULL, NULL, 'admin', '2022-11-24 22:51:54', 'admin', '2023-01-04 17:56:14');
INSERT INTO `sso_user` VALUES ('4ef9999a1cd0492db32c87d97659b963', 'test1', '18922222221', 'test1@qq.com', 'ee508c5ee37a4b27e41ab9cc80af453b', NULL, '测试1', NULL, '02587654321', '2022-11-20', 1, 0, 0, '3952171ab8cb094c4abe55cc831d1c76', NULL, NULL, 'admin', '2022-11-26 22:06:09', 'admin', '2022-12-20 12:09:30');
INSERT INTO `sso_user` VALUES ('f4056d9589a64146a7538f04c6bcc10f', 'operate', '18933333333', 'operate@qq.com', 'b48905c015cfa5ec95dd2e7c8f9e810f', NULL, '运营', NULL, '0251233111', '2022-11-23', 1, 0, 0, '0662ab48ae6102c7caaa37700200ed7f', NULL, NULL, 'admin', '2022-11-24 23:33:10', 'admin', '2022-12-19 21:24:39');

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
INSERT INTO `sso_user_role` VALUES ('0b13f982db33481e8e1e5e84f6df7c3e', '57ad11f7d8d94e2664f4d772a6dd9d7d');
INSERT INTO `sso_user_role` VALUES ('1', '1');
INSERT INTO `sso_user_role` VALUES ('40062f1156ef42b9b3a341462c927fb6', '210297727b74ecb505c1b4d97f76daee');
INSERT INTO `sso_user_role` VALUES ('4ef9999a1cd0492db32c87d97659b963', '57ad11f7d8d94e2664f4d772a6dd9d7d');
INSERT INTO `sso_user_role` VALUES ('f4056d9589a64146a7538f04c6bcc10f', '67e95f5e81b8da9a8f70db7540b7409d');

SET FOREIGN_KEY_CHECKS = 1;
