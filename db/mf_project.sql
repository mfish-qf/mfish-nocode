-- ----------------------------------------
--  项目管理系统（mf-project）业务表
--  单实例启动直接在 mfish_nocode 库中创建；微服务模式可自行调整 USE 目标库
--  超级管理员拥有 *:*:* 全量权限可直接访问 project:* 接口；
--  普通用户需在系统菜单管理中配置 project:xxx 权限后访问
-- ----------------------------------------
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mfish_nocode`;

-- ----------------------------
-- Table structure for pm_requirement 核心需求表
-- ----------------------------
DROP TABLE IF EXISTS `pm_requirement`;
CREATE TABLE `pm_requirement`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `req_no` int NULL DEFAULT NULL COMMENT '序号(自动递增)',
  `module` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块',
  `level1_feature` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '一级功能点',
  `level2_feature` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '二级功能点',
  `level3_feature` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '三级功能点',
  `feature_description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '功能点描述',
  `prd_section` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求设计说明书对应章节',
  `review_date` date NULL DEFAULT NULL COMMENT '需求评审日期',
  `review_result` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评审结果',
  `review_notes` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求评审记录',
  `vendors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '涉及厂商',
  `priority` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '优先级',
  `requester` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求提出人',
  `request_date` date NULL DEFAULT NULL COMMENT '需求提出日期',
  `high_fidelity_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '高保真图(URL或图示说明)',
  `change_date` date NULL DEFAULT NULL COMMENT '需求变更日期',
  `change_content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求变更内容',
  `pre_meeting_questions` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会前提出疑问/意见',
  `release_time` date NULL DEFAULT NULL COMMENT '研发确认发版时间',
  `frontend_dev` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端人员',
  `frontend_dev_ids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端人员用户ID列表(JSON数组, 与frontend_dev按索引对齐, null表示该位未匹配到账号)',
  `backend_dev` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后端人员',
  `backend_dev_ids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后端人员用户ID列表(JSON数组, 与backend_dev按索引对齐, null表示该位未匹配到账号)',
  `qa_tester` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试人员',
  `qa_tester_ids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试人员用户ID列表(JSON数组, 与qa_tester按索引对齐, null表示该位未匹配到账号)',
  `remarks` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注\\疑问',
  `owner` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联负责人',
  `owner_ids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联负责人用户ID列表(JSON数组, 与owner按索引对齐, null表示该位未匹配到账号)',
  `iteration_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属迭代ID(版本号由该ID关联 pm_iteration.code 派生, 不冗余存储)',
  `workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '研发预估总工时(单位:人/天,=前端+后端)',
  `frontend_workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '前端研发工时(单位:人/天)',
  `backend_workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '后端研发工时(单位:人/天)',
  `dev_start_date` date NULL DEFAULT NULL COMMENT '开发开始日期',
  `dev_end_date` date NULL DEFAULT NULL COMMENT '开发完成日期(根据工时与日历规则自动推导)',
  `dev_progress` int NULL DEFAULT NULL COMMENT '开发进度百分比(0-100)',
  `test_phase` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '转测阶段/批次标签',
  `test_node_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定的版本转测节点ID',
  `test_node_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定的版本转测节点名称',
  `test_node_date` date NULL DEFAULT NULL COMMENT '绑定的版本转测时间',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '研发状态',
  `prd_doc_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详细需求PRD文档内容(类Word富文本规范与正文)',
  `doc_last_modified` datetime NULL DEFAULT NULL COMMENT '需求详细文档最后修改时间',
  `doc_author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求详细文档编写人',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户ID(多租户隔离)',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_req_tenant`(`tenant_id`) USING BTREE,
  INDEX `idx_pm_req_no`(`req_no`) USING BTREE,
  INDEX `idx_pm_req_iteration`(`iteration_id`) USING BTREE,
  INDEX `idx_pm_req_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心需求表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_dev_schedule 研发排期明细表(需求×人员×岗位)
-- 由自动排期引擎生成, 支持一键重算; 单行可钉住(is_pinned), 重算时保持不动
-- ----------------------------
DROP TABLE IF EXISTS `pm_dev_schedule`;
CREATE TABLE `pm_dev_schedule`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `req_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '需求ID',
  `req_no` int NULL DEFAULT NULL COMMENT '需求序号(冗余, 便于排序展示)',
  `iteration_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属迭代ID',
  `person_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发人员姓名',
  `person_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开发人员用户ID(排期身份主键, 为空表示未匹配到账号则按姓名兜底)',
  `dev_role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位: frontend前端/backend后端/fullstack全栈(未拆分工时)',
  `workload` decimal(5, 1) NOT NULL DEFAULT 1.0 COMMENT '该人员在本需求上的工时(人/天)',
  `seq` int NOT NULL DEFAULT 0 COMMENT '同一人员任务顺序(升序即执行顺序, 重算后按开始日重排)',
  `dev_start_date` date NOT NULL COMMENT '任务开始日(有效工作日)',
  `dev_end_date` date NOT NULL COMMENT '任务完成日(按工时与日历跳过规则推导)',
  `is_pinned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否钉住: 1=一键重算时保持不动',
  `pin_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '钉住原因/备注(拖拽调期时自动记录)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户ID(多租户隔离)',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dev_schedule_tenant`(`tenant_id`) USING BTREE,
  INDEX `idx_dev_schedule_req`(`req_id`) USING BTREE,
  INDEX `idx_dev_schedule_person`(`person_name`, `dev_role`) USING BTREE,
  INDEX `idx_dev_schedule_person_id`(`person_id`) USING BTREE,
  INDEX `idx_dev_schedule_iteration`(`iteration_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '研发排期明细表(需求×人员×岗位, 支持钉住与拖拽调期)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_iteration 迭代版本表
-- ----------------------------
DROP TABLE IF EXISTS `pm_iteration`;
CREATE TABLE `pm_iteration`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号/代号(如 v1.1、2026-09B)',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '迭代名称',
  `start_date` date NULL DEFAULT NULL COMMENT '迭代开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '迭代结束日期',
  `release_date` date NULL DEFAULT NULL COMMENT '研发确认发版日期',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '迭代状态(规划中/进行中/测试中/已发布)',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '迭代目标与说明',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户ID(多租户隔离)',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '版本相关附件 JSON 字符串 ([{fileKey,name,size,type}])，可上传多文件(图片/压缩包/文档等)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_iter_tenant`(`tenant_id`) USING BTREE,
  INDEX `idx_pm_iter_code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '迭代版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_iteration_test_node 迭代批次转测节点表
-- ----------------------------
DROP TABLE IF EXISTS `pm_iteration_test_node`;
CREATE TABLE `pm_iteration_test_node`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `iteration_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属迭代ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '节点名称(如:第1批转测、冒烟提测、二批集成)',
  `node_date` date NULL DEFAULT NULL COMMENT '转测时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提测说明',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_tn_iteration`(`iteration_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '迭代批次转测节点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 说明: 原 pm_personnel(团队人员与岗位档案表)已废弃
-- 系统中所有人员统一通过后端 /oauth2/ssoTenant/user 接口获取(按组织与角色名称
-- 派生 前端/后端、主管/经理/普通研发 岗位属性)，前端全局缓存于 useProjectUsers。
-- 存量环境数据迁移(手工执行一次): DROP TABLE IF EXISTS `pm_personnel`;
-- ----------------------------

-- ----------------------------
-- 说明: 原 pm_dict_category(树形业务字典与功能层级分类表)已废弃
-- 分类字典统一迁移至框架 sys_dict_category(分类编码 project_management),
-- 由 mf-sys 的 /sys/dictCategory/tree/project_management?direction=down 提供查询,
-- 种子数据见 db/mf_system.sql 的 sys_dict_category 记录区。
--
-- 存量环境数据迁移(手工执行一次, 需在 pm_dict_category 仍存在时执行):
-- 结构: 一级根节点(携带分类编码) -> 二级模块 -> 三级一级功能
-- 1. 插入一级根节点(分类编码仅挂根节点, 与框架规范一致)
--   INSERT INTO `sys_dict_category`
--     (`id`, `parent_id`, `category_code`, `category_name`, `tree_code`, `tree_level`, `icon`, `sort`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
--   VALUES ('dc-000', '', 'project_management', '项目管理', '00001', 1, 'project', 0, 'PM 模块与一级功能分类根节点', 'admin', NOW(), 'admin', NOW());
-- 2. 原一级(模块)迁为二级, 挂到根节点下, category_code 置空
--   INSERT INTO `sys_dict_category`
--     (`id`, `parent_id`, `category_code`, `category_name`, `tree_code`, `tree_level`, `icon`, `sort`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
--   SELECT `id`, 'dc-000', '', `category_name`,
--     CONCAT('00001', LPAD(SUBSTR(`tree_code`,1,3),5,'0')),
--     2, `icon`, IFNULL(`sort`,0), `remark`, 'admin', NOW(), 'admin', NOW()
--   FROM `pm_dict_category` WHERE `del_flag` = 0 AND `tree_level` = 1;
-- 3. 原二级(一级功能)迁为三级, 挂到对应模块下, category_code 置空
--   INSERT INTO `sys_dict_category`
--     (`id`, `parent_id`, `category_code`, `category_name`, `tree_code`, `tree_level`, `icon`, `sort`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`)
--   SELECT c.`id`, p.`id`, '', c.`category_name`,
--     CONCAT('00001', LPAD(SUBSTR(p.`tree_code`,1,3),5,'0'), LPAD(SUBSTR(c.`tree_code`,4,3),5,'0')),
--     3, c.`icon`, IFNULL(c.`sort`,0), c.`remark`, 'admin', NOW(), 'admin', NOW()
--   FROM `pm_dict_category` c JOIN `pm_dict_category` p ON c.`parent_id` = p.`id`
--   WHERE c.`del_flag` = 0 AND c.`tree_level` = 2;
--   DROP TABLE IF EXISTS `pm_dict_category`;
-- (tree_code 由 3 位分段转换为框架 5 位分段, 如 001->00001, 001002->0000100002)

-- ----------------------------
-- Table structure for pm_schedule_config 排期日历全局规则配置表(单行配置)
-- ----------------------------
DROP TABLE IF EXISTS `pm_schedule_config`;
CREATE TABLE `pm_schedule_config`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID(固定为1,单行配置)',
  `skip_weekends` tinyint(1) NULL DEFAULT 1 COMMENT '是否默认跳过周六与周日',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排期日历全局规则配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_schedule_skipped_date 排期日历跳过日期表(节假日、封版冻结日)
-- ----------------------------
DROP TABLE IF EXISTS `pm_schedule_skipped_date`;
CREATE TABLE `pm_schedule_skipped_date`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `config_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属配置ID',
  `date` date NULL DEFAULT NULL COMMENT '跳过日期',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '假期/跳过原因(如 国庆节黄金周、中秋节、研发封版冻结)',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型(holiday法定假日/custom_off自定义休息/freeze封版冻结)',
  `is_recurring_weekend` tinyint(1) NULL DEFAULT 0 COMMENT '是否周末',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_skip_config`(`config_id`) USING BTREE,
  INDEX `idx_pm_skip_date`(`date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排期日历跳过日期表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_schedule_makeup_date 排期日历调休补班工作日表
-- ----------------------------
DROP TABLE IF EXISTS `pm_schedule_makeup_date`;
CREATE TABLE `pm_schedule_makeup_date`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `config_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属配置ID',
  `date` date NULL DEFAULT NULL COMMENT '调休补班工作日(在周末但仍需计入工作)',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_makeup_config`(`config_id`) USING BTREE,
  INDEX `idx_pm_makeup_date`(`date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排期日历调休补班工作日表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_requirement_history 需求调配与状态流转审计日志表
-- ----------------------------
DROP TABLE IF EXISTS `pm_requirement_history`;
CREATE TABLE `pm_requirement_history`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `req_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求ID',
  `req_no` int NULL DEFAULT NULL COMMENT '需求序号',
  `action_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作类型(REASSIGN人员调配/STATUS状态流转/BALANCING负载均衡/WORKLOAD工时调整/BATCH批量操作)',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位角色(frontend/backend/qa/owner)',
  `from_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原人员',
  `to_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标人员',
  `old_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变更前内容',
  `new_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变更后内容',
  `detail` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作详细说明',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_hist_req`(`req_id`) USING BTREE,
  INDEX `idx_pm_hist_type`(`action_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '需求调配与状态流转审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_user_column_config 用户表格列配置表(按登录人员保存)
-- ----------------------------
DROP TABLE IF EXISTS `pm_user_column_config`;
CREATE TABLE `pm_user_column_config`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID(雪花)',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属用户ID(当前登录人员)',
  `config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '列配置JSON(含key/title/category/width/visible)',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_pm_ucc_user`(`user_id`, `del_flag`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表格列配置表(按登录人员保存)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pm_schedule_config 默认排期配置(单行)
-- ----------------------------
INSERT INTO `pm_schedule_config` VALUES ('1', 1, 0, '系统默认排期配置(跳过周六日)', '', NOW(), '', NOW());

-- ----------------------------
-- Records of pm_schedule_skipped_date 默认节假日(2026中秋+国庆黄金周)
-- 注意: 2026年中秋节为9月25日(周五), 9/26-27为周末自动跳过
-- ----------------------------
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-mid-autumn-1', '1', '2026-09-25', '中秋节假期', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-1', '1', '2026-10-01', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-2', '1', '2026-10-02', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-3', '1', '2026-10-03', '国庆节黄金周', 'holiday', 1, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-4', '1', '2026-10-04', '国庆节黄金周', 'holiday', 1, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-5', '1', '2026-10-05', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-6', '1', '2026-10-06', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-7', '1', '2026-10-07', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());

-- ----------------------------
-- Records of pm_iteration 初始迭代版本(4个)
-- ----------------------------
INSERT INTO `pm_iteration` VALUES ('iter-v1.1', 'v1.1.0', 'v1.1 基础引擎与表单控件版本', '2026-09-01', '2026-09-22', '2026-09-24', '已发布', '9月上中旬周期(共22天)，聚焦低代码基础引擎与表单核心控件库。', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration` VALUES ('iter-v1.2', 'v1.2.0', 'v1.2 协同迭代版本 (09.12 ~ 10.20)', '2026-09-12', '2026-10-20', '2026-10-22', '进行中', '涵盖9月中旬至10月下旬(共39天)，涵盖权限矩阵、动态脚本与多次分批转测。', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration` VALUES ('iter-v2.0', 'v2.0.0', 'v2.0 架构升级大版本 (09.25 ~ 11.20)', '2026-09-25', '2026-11-20', '2026-11-25', '规划中', '涵盖分布式引擎重构与全景测试。', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration` VALUES ('iter-v2.1', 'v2.1.0', 'v2.1 双周轻量冲刺版 (10.08 ~ 10.26)', '2026-10-08', '2026-10-26', '2026-10-28', '规划中', '10月中下旬敏捷冲刺，聚焦移动端适配与UI微调。', 0, NULL, '', NOW(), '', NOW());

-- ----------------------------
-- Records of pm_iteration_test_node 初始转测节点(7个)
-- ----------------------------
INSERT INTO `pm_iteration_test_node` VALUES ('tn-v11-1', 'iter-v1.1', '第1批转测 (基础接口与模型)', '2026-09-08', '基础模型与元数据接口', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration_test_node` VALUES ('tn-v11-2', 'iter-v1.1', '第2批转测 (表单控件与校验)', '2026-09-18', '低代码表单核心控件', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration_test_node` VALUES ('tn-v12-1', 'iter-v1.2', '第1批转测 (渲染器与审批流)', '2026-09-22', '动态页面渲染与审批引擎', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration_test_node` VALUES ('tn-v12-2', 'iter-v1.2', '第2批转测 (支付与全量验收)', '2026-10-15', '外部支付SDK及全量回归', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration_test_node` VALUES ('tn-v20-1', 'iter-v2.0', '首批转测 (分布式引擎重构)', '2026-10-20', '后端协议与引擎高可用压测', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration_test_node` VALUES ('tn-v20-2', 'iter-v2.0', '全量提测 (全景功能验收)', '2026-11-10', '全量集成测试与准出评估', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_iteration_test_node` VALUES ('tn-v21-1', 'iter-v2.1', '冲刺唯一转测', '2026-10-22', 'UI细节与移动端适配冒烟准入', 0, NULL, '', NOW(), '', NOW());

-- ----------------------------
-- 存量库增量迁移: 研发工时拆分(前端/后端)
-- ----------------------------
ALTER TABLE `pm_requirement`
  ADD COLUMN `frontend_workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '前端研发工时(单位:人/天)' AFTER `workload`,
  ADD COLUMN `backend_workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '后端研发工时(单位:人/天)' AFTER `frontend_workload`;

-- ----------------------------
-- 存量库增量迁移: 回退前后端起始两列(改为独立排期表 pm_dev_schedule)
-- 注意: 仅当此前执行过"前后端分离排期(各自独立起算日)"两列迁移时需要;
--       全新库建表脚本已不含该两列, 无需执行(取消下面注释即可)
-- ----------------------------
-- ALTER TABLE `pm_requirement`
--   DROP COLUMN `frontend_dev_start_date`,
--   DROP COLUMN `backend_dev_start_date`;

-- ----------------------------
-- 存量库增量迁移: 研发排期明细表(需求×人员×岗位)
-- 注意: 本表重算/删除均走物理删除, 无 del_flag 列
-- ----------------------------
CREATE TABLE IF NOT EXISTS `pm_dev_schedule`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `req_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '需求ID',
  `req_no` int NULL DEFAULT NULL COMMENT '需求序号(冗余, 便于排序展示)',
  `iteration_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属迭代ID',
  `person_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发人员姓名',
  `person_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开发人员用户ID(排期身份主键, 为空表示未匹配到账号则按姓名兜底)',
  `dev_role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位: frontend前端/backend后端/fullstack全栈(未拆分工时)',
  `workload` decimal(5, 1) NOT NULL DEFAULT 1.0 COMMENT '该人员在本需求上的工时(人/天)',
  `seq` int NOT NULL DEFAULT 0 COMMENT '同一人员任务顺序(升序即执行顺序, 重算后按开始日重排)',
  `dev_start_date` date NOT NULL COMMENT '任务开始日(有效工作日)',
  `dev_end_date` date NOT NULL COMMENT '任务完成日(按工时与日历跳过规则推导)',
  `is_pinned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否钉住: 1=一键重算时保持不动',
  `pin_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '钉住原因/备注(拖拽调期时自动记录)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户ID(多租户隔离)',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dev_schedule_tenant`(`tenant_id`) USING BTREE,
  INDEX `idx_dev_schedule_req`(`req_id`) USING BTREE,
  INDEX `idx_dev_schedule_person`(`person_name`, `dev_role`) USING BTREE,
  INDEX `idx_dev_schedule_person_id`(`person_id`) USING BTREE,
  INDEX `idx_dev_schedule_iteration`(`iteration_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '研发排期明细表(需求×人员×岗位, 支持钉住与拖拽调期)' ROW_FORMAT = Dynamic;

-- 若此前执行过含 del_flag 列的旧版迁移, 取消下面注释执行(先清残留再删列):
-- DELETE FROM `pm_dev_schedule` WHERE `del_flag` = 1;
-- ALTER TABLE `pm_dev_schedule` DROP COLUMN `del_flag`;


SET FOREIGN_KEY_CHECKS = 1;
