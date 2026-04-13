/**
  工作流数据库（采用微服务启动时，需要创建工作流数据库，数据表引擎会自动创建）
 */
DROP DATABASE IF EXISTS `mf_workflow`;
CREATE DATABASE  `mf_workflow` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_workflow`;

-- ----------------------------
-- Table structure for flw_mf_manage
-- ----------------------------
DROP TABLE IF EXISTS `flw_mf_manage`;
CREATE TABLE `flw_mf_manage`  (
                                  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                                  `flow_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程key',
                                  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程名称',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程描述',
                                  `version` int DEFAULT NULL COMMENT '版本号',
                                  `released` tinyint(1) NULL DEFAULT 0 COMMENT '是否发布（0未发布 1已发布）',
                                  `flow_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '流程配置',
                                  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标记(0未删除1删除)',
                                  `hex` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置信息hex',
                                  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建用户',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新用户',
                                  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程管理' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
