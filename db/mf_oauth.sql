/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : localhost:3306
 Source Schema         : mf_oauth

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 16/08/2022 16:24:36
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_client_details
-- ----------------------------
INSERT INTO `sso_client_details` VALUES ('system', NULL, 'system', 'all', 'authorization_code,password,refresh_token', 'http://baidu.com', NULL, 28800, NULL, NULL, 'true');

-- ----------------------------
-- Table structure for sso_logs
-- ----------------------------
DROP TABLE IF EXISTS `sso_logs`;
CREATE TABLE `sso_logs`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端id',
  `interface_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调用接口名称',
  `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ip地址',
  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求sessionId',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `state` tinyint(4) NULL DEFAULT NULL COMMENT '接口调用状态 0成功 1失败',
  `remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_logs
-- ----------------------------

-- ----------------------------
-- Table structure for sso_user
-- ----------------------------
DROP TABLE IF EXISTS `sso_user`;
CREATE TABLE `sso_user`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `old_password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '旧密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `headImgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `telephone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `sex` tinyint(1) NULL DEFAULT NULL COMMENT '性别',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐',
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信唯一id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_index`(`account`) USING BTREE,
  UNIQUE INDEX `openid_index`(`openid`) USING BTREE,
  UNIQUE INDEX `phone_index`(`phone`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sso_user
-- ----------------------------
INSERT INTO `sso_user` VALUES ('6870e0fe-fa79-468c-86d9-e963e9b5c43f', 'admin', '18911111111', 'qiufeng9862@qq.com', '643ce6d324f7762cada2df58023cd9c6', '$2a$10$WskRinqDYZxZ2KO2O5/Yf.mslpeM7dwhEYz7dUSiwfrmhVNY1mKzG,b23e5b12659873270b76432639538ff0', '管理员', NULL, '02511111111', '1998-06-28', 0, 1, '452187570f682f2ddb35a216fd32460d', 'olbL54qA8qAccFNtModx6dM-Ha6w', '2017-04-10 15:21:38', '2021-12-14 10:26:40');

SET FOREIGN_KEY_CHECKS = 1;
