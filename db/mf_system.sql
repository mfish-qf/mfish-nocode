/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : localhost:3306
 Source Schema         : mf_system

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 15/12/2022 22:01:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  `req_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `req_source` int(1) NULL DEFAULT 0 COMMENT '请求来源（0其它 1后台用户 2手机端用户）',
  `oper_type` int(2) NULL DEFAULT 0 COMMENT '操作类型（0其它 1查询 2新增 3修改 4删除 5授权 6导入 7导出...）',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作IP',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `oper_status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `oper_time_index`(`oper_time`) USING BTREE COMMENT '操作时间'
) ENGINE = InnoDB AUTO_INCREMENT = 1188 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统日志' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
