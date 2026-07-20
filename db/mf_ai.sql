-- ----------------------------
-- AI中心数据库 (摸鱼小助手 AI相关配置库)
-- ----------------------------
DROP DATABASE IF EXISTS `mf_ai`;
CREATE DATABASE  `mf_ai` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_ai`;


-- ----------------------------
-- Table structure for ai_model_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_model_config`;
CREATE TABLE `ai_model_config`  (
                                    `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
                                    `tenant_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
                                    `provider` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提供者: openai/ollama/deepseek/zhipuai/anthropic等',
                                    `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模型名称: gpt-4o, qwen3:8b, deepseek-v3 等',
                                    `protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接入协议:openai/ollama/deepseek/anthropic',
                                    `api_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'API密钥(加密存储)',
                                    `base_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'API基础地址',
                                    `max_tokens` int NULL DEFAULT 4096 COMMENT '最大token数',
                                    `temperature` double NULL DEFAULT 0.7 COMMENT '温度参数',
                                    `top_p` double NULL DEFAULT NULL COMMENT 'top_p参数',
                                    `completions_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '补全项路径',
                                    `enabled` tinyint NULL DEFAULT 1 COMMENT '是否启用 1启用 0禁用',
                                    `sort_order` int NULL DEFAULT 0 COMMENT '排序(决定fallback优先级)',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                    `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
                                    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI模型配置信息' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
