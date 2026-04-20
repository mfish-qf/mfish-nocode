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

INSERT INTO `flw_mf_manage` VALUES ('b686750cc7edd180d4f5c4ac8d5299fa', 'demo_leave_apply_release', '工作流样例审批', '工作流试用样例', 1, 1, '{\"nodes\":[{\"id\":\"node_start_1\",\"type\":\"custom\",\"draggable\":true,\"initialized\":false,\"position\":{\"x\":250,\"y\":50},\"data\":{\"type\":\"start\",\"label\":\"开始\",\"icon\":\"Play\"},\"label\":\"开始\"},{\"id\":\"node_approval_1776677052658\",\"type\":\"custom\",\"draggable\":true,\"initialized\":false,\"position\":{\"x\":550,\"y\":50},\"data\":{\"type\":\"approval\",\"label\":\"审批\",\"approvalType\":\"OR\",\"userIds\":[\"c51fde3955594074bb4db31e654a4483\"],\"userNames\":[\"mfish\"]},\"label\":\"审批\"},{\"id\":\"node_approval_1776677060910\",\"type\":\"custom\",\"draggable\":true,\"initialized\":false,\"position\":{\"x\":981.25,\"y\":47.5},\"data\":{\"type\":\"approval\",\"label\":\"审批\",\"approvalType\":\"OR\",\"roleIds\":[\"4b423f7b1ac0ed0b46a8e5ec3389ac14\"],\"roleNames\":[\"管理\"]},\"label\":\"审批\"},{\"id\":\"node_end_1776677072235\",\"type\":\"custom\",\"draggable\":true,\"initialized\":false,\"position\":{\"x\":1360,\"y\":221.24999999999997},\"data\":{\"type\":\"end\",\"label\":\"结束\",\"executionListeners\":[{\"event\":\"start\",\"type\":\"class\",\"value\":\"cn.com.mfish.workflow.handler.CompleteCallbackHandler\"}]},\"label\":\"结束\"}],\"edges\":[{\"id\":\"e-node_start_1-right-node_approval_1776677052658-left\",\"type\":\"custom\",\"source\":\"node_start_1\",\"target\":\"node_approval_1776677052658\",\"sourceHandle\":\"right\",\"targetHandle\":\"left\",\"data\":{\"showArrow\":true,\"pathType\":\"default\",\"condition\":null},\"label\":\"\",\"animated\":true,\"style\":{\"stroke\":\"#EE4F12\",\"strokeWidth\":2},\"markerEnd\":{\"type\":\"arrowclosed\",\"color\":\"#EE4F12\"},\"sourceX\":492.5,\"sourceY\":80.66667175292969,\"targetX\":547.5,\"targetY\":110.66665649414062},{\"id\":\"e-node_approval_1776677052658-right-node_approval_1776677060910-left\",\"type\":\"custom\",\"source\":\"node_approval_1776677052658\",\"target\":\"node_approval_1776677060910\",\"sourceHandle\":\"right\",\"targetHandle\":\"left\",\"data\":{\"showArrow\":true,\"pathType\":\"default\",\"condition\":\"approved\"},\"label\":\"\",\"animated\":true,\"style\":{\"stroke\":\"#EE4F12\",\"strokeWidth\":2},\"markerEnd\":{\"type\":\"arrowclosed\",\"color\":\"#EE4F12\"},\"sourceX\":792.5,\"sourceY\":110.66665649414062,\"targetX\":978.75,\"targetY\":108.16665649414062},{\"id\":\"e-node_approval_1776677060910-top-source-node_approval_1776677052658-top-source\",\"type\":\"custom\",\"source\":\"node_approval_1776677060910\",\"target\":\"node_approval_1776677052658\",\"sourceHandle\":\"top-source\",\"targetHandle\":\"top-source\",\"data\":{\"showArrow\":true,\"pathType\":\"default\",\"condition\":\"rejected\"},\"label\":\"\",\"animated\":true,\"style\":{\"stroke\":\"#EE4F12\",\"strokeWidth\":2},\"markerEnd\":{\"type\":\"arrowclosed\",\"color\":\"#EE4F12\"},\"sourceX\":1101.25,\"sourceY\":45,\"targetX\":670,\"targetY\":47.5},{\"id\":\"e-node_approval_1776677060910-right-node_end_1776677072235-left\",\"type\":\"custom\",\"source\":\"node_approval_1776677060910\",\"target\":\"node_end_1776677072235\",\"sourceHandle\":\"right\",\"targetHandle\":\"left\",\"data\":{\"showArrow\":true,\"pathType\":\"default\",\"condition\":\"approved\"},\"label\":\"\",\"animated\":true,\"style\":{\"stroke\":\"#EE4F12\",\"strokeWidth\":2},\"markerEnd\":{\"type\":\"arrowclosed\",\"color\":\"#EE4F12\"},\"sourceX\":1223.75,\"sourceY\":108.16665649414062,\"targetX\":1357.5,\"targetY\":251.9166564941406},{\"id\":\"e-node_approval_1776677052658-bottom-node_end_1776677072235-left\",\"type\":\"custom\",\"source\":\"node_approval_1776677052658\",\"target\":\"node_end_1776677072235\",\"sourceHandle\":\"bottom\",\"targetHandle\":\"left\",\"data\":{\"showArrow\":true,\"pathType\":\"smoothstep\",\"condition\":\"rejected\"},\"label\":\"\",\"animated\":true,\"style\":{\"stroke\":\"#EE4F12\",\"strokeWidth\":2},\"markerEnd\":{\"type\":\"arrowclosed\",\"color\":\"#EE4F12\"},\"sourceX\":670,\"sourceY\":173.83331298828125,\"targetX\":1357.5,\"targetY\":251.9166564941406}],\"position\":[-27.5,349.1],\"zoom\":0.8,\"viewport\":{\"x\":-27.5,\"y\":349.1,\"zoom\":0.8}}', 0, 'ca50d1785a07f71abbec0da4af6b0632d8d51f54c84ca5050debf1c46f529291', 'admin', '2026-04-20 17:25:00', 'admin', '2026-04-20 17:25:05');


SET FOREIGN_KEY_CHECKS = 1;
