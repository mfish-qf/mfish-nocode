DROP DATABASE IF EXISTS `mf_project`;
CREATE DATABASE  `mf_project` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE `mf_project`;

-- ----------------------------
-- Table structure for pm_dev_schedule
-- ----------------------------
DROP TABLE IF EXISTS `pm_dev_schedule`;
CREATE TABLE `pm_dev_schedule`  (
                                    `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
                                    `req_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '需求ID',
                                    `req_no` int NULL DEFAULT NULL COMMENT '需求序号(冗余, 便于排序展示)',
                                    `iteration_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属迭代ID',
                                    `person_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发人员姓名',
                                    `person_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开发人员用户ID(排期身份主键, 为空则按姓名兜底)',
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
                                    INDEX `idx_dev_schedule_req`(`req_id`) USING BTREE,
                                    INDEX `idx_dev_schedule_person`(`person_name`, `dev_role`) USING BTREE,
                                    INDEX `idx_dev_schedule_iteration`(`iteration_id`) USING BTREE,
                                    INDEX `idx_dev_schedule_tenant`(`tenant_id`) USING BTREE,
                                    INDEX `idx_dev_schedule_person_id`(`person_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '研发排期明细表(需求×人员×岗位, 支持钉住与拖拽调期)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_iteration
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
                                 `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户ID(多租户隔离)',
                                 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                 `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '版本相关附件 JSON 字符串 ([{fileKey,name,size,type}])，可上传多文件(图片/压缩包/文档等)',
                                 `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
                                 `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_pm_iter_code`(`code`) USING BTREE,
                                 INDEX `idx_pm_iter_tenant`(`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '迭代版本表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for pm_iteration_test_node
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
-- Table structure for pm_requirement
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
                                   `review_notes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求评审记录',
                                   `vendors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '涉及厂商',
                                   `priority` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '优先级',
                                   `requester` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求提出人',
                                   `request_date` date NULL DEFAULT NULL COMMENT '需求提出日期',
                                   `high_fidelity_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '高保真图(URL或图示说明)',
                                   `change_date` date NULL DEFAULT NULL COMMENT '需求变更日期',
                                   `change_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需求变更内容',
                                   `pre_meeting_questions` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会前提出疑问/意见',
                                   `release_time` date NULL DEFAULT NULL COMMENT '研发确认发版时间',
                                   `frontend_dev` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端人员',
                                   `frontend_dev_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端人员用户ID列表(JSON数组, 与frontend_dev按索引对齐, null表示该位未匹配到账号)',
                                   `backend_dev` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后端人员',
                                   `backend_dev_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后端人员用户ID列表(JSON数组, 与backend_dev按索引对齐, null表示该位未匹配到账号)',
                                   `qa_tester` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试人员',
                                   `qa_tester_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试人员用户ID列表(JSON数组, 与qa_tester按索引对齐, null表示该位未匹配到账号)',
                                   `remarks` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注\\疑问',
                                   `owner` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联负责人',
                                   `owner_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联负责人用户ID列表(JSON数组, 与owner按索引对齐, null表示该位未匹配到账号)',
                                   `iteration_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属迭代ID',
                                   `workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '研发预估总工时(单位:人/天,=前端+后端)',
                                   `frontend_workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '前端研发工时(单位:人/天)',
                                   `backend_workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '后端研发工时(单位:人/天)',
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
                                   INDEX `idx_pm_req_no`(`req_no`) USING BTREE,
                                   INDEX `idx_pm_req_iteration`(`iteration_id`) USING BTREE,
                                   INDEX `idx_pm_req_status`(`status`) USING BTREE,
                                   INDEX `idx_pm_req_tenant`(`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心需求表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_requirement_history
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
-- Table structure for pm_schedule_config
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
-- Records of pm_schedule_config
-- ----------------------------
INSERT INTO `pm_schedule_config` VALUES ('1', 1, 0, '系统默认排期配置(跳过周六日)', '', '2026-10-12 14:19:03', 'admin', '2026-10-15 11:09:56');

-- ----------------------------
-- Table structure for pm_schedule_makeup_date
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
-- Records of pm_schedule_makeup_date
-- ----------------------------
INSERT INTO `pm_schedule_makeup_date` VALUES ('2110568828122697737', '1', '2026-09-20', 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_makeup_date` VALUES ('2110568828122697738', '1', '2026-10-10', 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);

-- ----------------------------
-- Table structure for pm_schedule_skipped_date
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
-- Records of pm_schedule_skipped_date
-- ----------------------------
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828059783170', '1', '2026-05-01', '五一劳动节', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828059783171', '1', '2026-05-02', '五一劳动节', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828059783172', '1', '2026-05-03', '五一劳动节', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828059783173', '1', '2026-05-04', '五一劳动节', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828059783174', '1', '2026-05-05', '五一劳动节', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828059783175', '1', '2026-09-25', '中秋', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828059783176', '1', '2026-09-26', '中秋', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828122697729', '1', '2026-09-27', '中秋', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828122697730', '1', '2026-10-01', '国庆节黄金周', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828122697731', '1', '2026-10-02', '国庆节黄金周', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828122697732', '1', '2026-10-03', '国庆节黄金周', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828122697733', '1', '2026-10-04', '国庆节黄金周', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828122697734', '1', '2026-10-05', '国庆节黄金周', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828122697735', '1', '2026-10-06', '国庆节黄金周', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);
INSERT INTO `pm_schedule_skipped_date` VALUES ('2110568828122697736', '1', '2026-10-07', '国庆节黄金周', 'holiday', 0, 0, NULL, 'admin', '2026-10-15 11:09:56', '', NULL);

-- ----------------------------
-- Table structure for pm_user_column_config
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


SET FOREIGN_KEY_CHECKS = 1;
