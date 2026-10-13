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
  `backend_dev` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后端人员',
  `qa_tester` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试人员',
  `remarks` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注\\疑问',
  `owner` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联负责人',
  `month` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属迭代/月份代码(如 2026-09 或 2026-Q4)',
  `iteration_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属迭代ID',
  `workload` decimal(5, 1) NULL DEFAULT NULL COMMENT '研发预估工时(单位:人/天)',
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
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_req_no`(`req_no`) USING BTREE,
  INDEX `idx_pm_req_month`(`month`) USING BTREE,
  INDEX `idx_pm_req_iteration`(`iteration_id`) USING BTREE,
  INDEX `idx_pm_req_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心需求表' ROW_FORMAT = Dynamic;

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
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
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
-- Table structure for pm_personnel 团队人员与岗位档案表
-- ----------------------------
DROP TABLE IF EXISTS `pm_personnel`;
CREATE TABLE `pm_personnel`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `display_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示全称(如 产品部-林晓敏 或 李博文(PMO))',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位角色编码(frontend/backend/qa/product/owner)',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '中文岗位名',
  `department` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职称头衔',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '在职状态(在职/忙碌/请假)',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_psn_role`(`role`) USING BTREE,
  INDEX `idx_pm_psn_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团队人员与岗位档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pm_dict_category 树形业务字典与功能层级分类表
-- ----------------------------
DROP TABLE IF EXISTS `pm_dict_category`;
CREATE TABLE `pm_dict_category`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一ID',
  `parent_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父分类id',
  `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类编码',
  `category_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `tree_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类树编码(系统自动编码,如 001、001001)',
  `tree_level` int NULL DEFAULT NULL COMMENT '分类树层级(自动生成,1为一级分类,2为二级分类)',
  `icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志(0正常 1已删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pm_dc_parent`(`parent_id`) USING BTREE,
  INDEX `idx_pm_dc_tree_code`(`tree_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '树形业务字典与功能层级分类表' ROW_FORMAT = Dynamic;

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
-- Records of pm_schedule_config 默认排期配置(单行)
-- ----------------------------
INSERT INTO `pm_schedule_config` VALUES ('1', 1, 0, '系统默认排期配置(跳过周六日)', '', NOW(), '', NOW());

-- ----------------------------
-- Records of pm_schedule_skipped_date 默认节假日(2026中秋+国庆黄金周)
-- ----------------------------
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-mid-autumn-1', '1', '2026-09-15', '中秋节假期', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-mid-autumn-2', '1', '2026-09-16', '中秋节假期', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-mid-autumn-3', '1', '2026-09-17', '中秋节假期', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-1', '1', '2026-10-01', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-2', '1', '2026-10-02', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-3', '1', '2026-10-03', '国庆节黄金周', 'holiday', 1, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-4', '1', '2026-10-04', '国庆节黄金周', 'holiday', 1, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-5', '1', '2026-10-05', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-6', '1', '2026-10-06', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_schedule_skipped_date` VALUES ('skip-national-7', '1', '2026-10-07', '国庆节黄金周', 'holiday', 0, 0, NULL, '', NOW(), '', NOW());

-- ----------------------------
-- Records of pm_personnel 初始团队人员库(25人)
-- ----------------------------
INSERT INTO `pm_personnel` VALUES ('user-fe-01', '周伟', '周伟', 'frontend', '前端开发专家', '摸鱼大前端平台组', '高级前端专家', '👨‍💻', 'zhouwei@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-fe-02', '王敏', '王敏', 'frontend', '前端架构师', '低代码引擎核心组', '大前端资深架构师', '👩‍💻', 'wangmin@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-fe-03', '李强', '李强', 'frontend', '全栈与前端开发', '组件树与画布引擎组', '前端全栈技术骨干', '👨‍💻', 'liqiang@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-fe-04', '张晓', '张晓', 'frontend', '前端组件工程师', '交互设计与移动端组', '高级前端工程师', '👩‍💻', 'zhangxiao@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-fe-05', '赵宇', '赵宇', 'frontend', '前端可视化开发', '大屏与BI报表组', 'WebGL/ECharts专家', '👨‍💻', 'zhaoyu@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-be-01', '陈明', '陈明', 'backend', '后端架构专家', '云原生中间件与数据中心', '后端核心架构专家', '👨‍💻', 'chenming@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-be-02', '赵刚', '赵刚', 'backend', '后端中间件研发', '分布式事务与支付结算组', '资深后端开发专家', '👨‍💻', 'zhaogang@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-be-03', '孙立', '孙立', 'backend', '服务端开发工程师', '用户中心与OAuth鉴权组', '高级后端工程师', '👨‍💻', 'sunli@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-be-04', '黄浩', '黄浩', 'backend', '微服务网关研发', '微服务与API网关架构组', '首席分布式架构师', '👨‍💻', 'huanghao@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-be-05', '徐磊', '徐磊', 'backend', '分布式数据湖研发', '多租户与数据安全组', '存储与中间件高级工程师', '👨‍💻', 'xulei@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-qa-01', '张静怡', '张静怡', 'qa', 'QA测试经理', '质量保障与效能研发部', 'QA测试总监 / 测试经理', '👩‍🔬', 'zhangjingyi@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-qa-02', '王凯', '王凯', 'qa', '自动化与性能测试', '全链路集成压测组', '资深性能测试专家', '👨‍🔬', 'wangkai@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-qa-03', '刘洋', '刘洋', 'qa', '业务与多端测试', '敏捷转测与冒烟接入组', '高级测试工程师', '👨‍🔬', 'liuyang@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-qa-04', '郑洁', '郑洁', 'qa', '安全与合规测试', '安全测试与准入审计组', '安全测试分析师', '👩‍🔬', 'zhengjie@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-pm-01', '林晓敏', '产品部-林晓敏', 'product', '产品经理 (PM)', '低代码产品部', '高级产品经理', '👩‍💼', 'linxiaomin@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-pm-02', '赵一鸣', '信息安全部-赵一鸣', 'product', '合规与安全产品', '信息安全部', '安全合规专家', '👨‍💼', 'zhaoyiming@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-pm-03', '刘芳', '业务运营-刘芳', 'product', '业务运营负责人', '商业化运营部', '资深运营专家', '👩‍💼', 'liufang@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-pm-04', '钱伟', '财务中心-钱伟', 'product', '财务结算产品', '财务结算中心', '财务风控业务专家', '👨‍💼', 'qianwei@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-pm-05', '孙涛', '大数据部-孙涛', 'product', '数据产品总监', '大数据部', '数据资产负责人', '👨‍💼', 'suntao@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-pm-06', '周长青', '技术总监-周长青', 'product', '技术总监', '研发中心', '技术管理总监', '👨‍💼', 'zhouchangqing@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-pm-07', '李博文', '产品管理部-李博文', 'product', '项目总监', '产品管理部 (PMO)', 'PMO高级总监', '👨‍💼', 'libowen@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-owner-01', '李博文(PMO)', '李博文(PMO)', 'owner', 'PMO技术交付总监', 'PMO项目管理办公室', '首席项目管理交付专家', '👔', 'pmo.lead@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-owner-02', '张瀚文(资深PM)', '张瀚文(资深PM)', 'owner', '业务线资深PM负责人', '数字业务交付中心', '资深业务交付负责人', '👔', 'zhanghanwen@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-owner-03', '黄浩(首席架构师)', '黄浩(首席架构师)', 'owner', '平台首席架构师', '集团技术委员会', '平台首席架构师', '🏛️', 'huanghao.arch@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-owner-04', '周长青(技术总监)', '周长青(技术总监)', 'owner', '研发中心技术总监', '摸鱼研发管理中心', '研发中心技术总监', '💼', 'zhouchangqing@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-owner-05', '陈明(架构专家)', '陈明(架构专家)', 'owner', '后端架构组长', '后端技术攻坚组', '分布式后端架构专家', '💻', 'chenming.arch@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-owner-06', '孙涛(数据总监)', '孙涛(数据总监)', 'owner', '大数据平台总监', '数据架构治理组', '数据平台架构总监', '📊', 'suntao.data@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-owner-07', '王敏(前端负责人)', '王敏(前端负责人)', 'owner', '大前端技术主管', '大前端工程化中心', '前端技术带头人', '💻', 'wangmin.fe@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());
INSERT INTO `pm_personnel` VALUES ('user-owner-08', '赵刚(后端主管)', '赵刚(后端主管)', 'owner', '后端服务技术主管', '服务端研发工程部', '服务端研发主管', '⚙️', 'zhaogang.be@mfish.io', '在职', 0, NULL, '', NOW(), '', NOW());

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
-- Records of pm_dict_category 初始树形字典(6大一级模块 + 27项二级功能)
-- ----------------------------
INSERT INTO `pm_dict_category` VALUES ('dc-001', NULL, 'FORM_ENGINE', '低代码表单引擎', '001', 1, 'form', 1, 0, '动态表单渲染核心引擎与校验体系', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-001-01', 'dc-001', 'FORM_WIDGETS', '表单核心控件库', '001001', 2, 'appstore', 1, 0, '包含输入、下拉、日期、上传等40+基础控件', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-001-02', 'dc-001', 'FORM_VALIDATOR', '动态校验规则器', '001002', 2, 'check-circle', 2, 0, '正则表达式、远程异步校验、函数表达式联动校验', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-001-03', 'dc-001', 'FORM_LINKAGE', '字段生命周期与联动显隐', '001003', 2, 'fork', 3, 0, '值监听变动、级联显示/隐藏、只读/可编辑状态联动', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-001-04', 'dc-001', 'FORM_CONTAINER', '分步向导与多Tab容器', '001004', 2, 'layout', 4, 0, '复杂长表单分页、折叠面板与弹窗嵌入容器', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-001-05', 'dc-001', 'FORM_GRID_LAYOUT', '栅格流式排版设计', '001005', 2, 'table', 5, 0, '24栅格自适应拖拽与多端响应式断点', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-002', NULL, 'WORKFLOW_CENTER', '工作流与审批中心', '002', 1, 'branches', 2, 0, 'BPMN2.0 流程编排与中国式复杂审批流', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-002-01', 'dc-002', 'WF_DESIGNER', 'BPMN可视化流程设计器', '002001', 2, 'node-index', 1, 0, '拖拽式流程节点定义、分支网关与连线', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-002-02', 'dc-002', 'WF_APPROVAL_POLICY', '多级审批/会签/或签/加签', '002002', 2, 'usergroup-add', 2, 0, '支持按角色、主管领导、动态脚本指定审批人', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-002-03', 'dc-002', 'WF_GATEWAY_RULE', '流程分支网关与条件表达式', '002003', 2, 'apartment', 3, 0, '根据表单数值、部门、申请额度进行流转分支分流', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-002-04', 'dc-002', 'WF_REMINDER', '待办催办与超时自动升级', '002004', 2, 'bell', 4, 0, '邮件、企微、钉钉消息推送与超时自动同意/转交', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-002-05', 'dc-002', 'WF_TIMELINE_LOG', '审批历史时间轴与轨迹图', '002005', 2, 'history', 5, 0, '审批人签字意见、用印附件与历史节点回退记录', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-003', NULL, 'AUTH_TENANT', '多租户与组织权限', '003', 1, 'safety', 3, 0, 'SaaS多租户隔离、RBAC与数据行级安全', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-003-01', 'dc-003', 'TENANT_ISOLATION', '租户空间与资源物理隔离', '003001', 2, 'database', 1, 0, '多租户独立Schema/分库分表与空间配额管控', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-003-02', 'dc-003', 'RBAC_SECURITY', 'RBAC角色与按钮级权限', '003002', 2, 'key', 2, 0, '功能菜单鉴权、操作按钮显隐、字段级读写遮罩', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-003-03', 'dc-003', 'ORG_STRUCTURE', '部门层级与人员兼职矩阵', '003003', 2, 'cluster', 3, 0, '支持集团/分子公司/部门多级树与虚拟项目组', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-003-04', 'dc-003', 'SSO_INTEGRATION', '单点登录SSO与OAuth2', '003004', 2, 'login', 4, 0, '飞书、企业微信、钉钉及SAML2.0统一认证对接', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-003-05', 'dc-003', 'AUDIT_LOGGING', '操作审计与登录安全日志', '003005', 2, 'audit', 5, 0, 'IP溯源、数据前后快照比对、导出下载痕迹审计', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-004', NULL, 'DATA_OPENAPI', '数据模型与OpenAPI', '004', 1, 'api', 4, 0, '可视化建模、无代码CRUD与第三方API编排', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-004-01', 'dc-004', 'DATA_MODEL_ER', '可视化实体数据建模', '004001', 2, 'table', 1, 0, '在线创建字段、主外键关联、索引与逆向工程', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-004-02', 'dc-004', 'CRUD_GENERATOR', '标准RESTful接口自动化', '004002', 2, 'code', 2, 0, '增删改查分页排序自动生成与Swagger在线文档', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-004-03', 'dc-004', 'API_ORCHESTRATION', '第三方API连接器与数据转换', '004003', 2, 'cloud-sync', 3, 0, '入参出参映射、Token鉴权拦截、失败重试机制', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-004-04', 'dc-004', 'DATA_SOURCE_POOL', '异构数据源连接池管理', '004004', 2, 'link', 4, 0, 'MySQL, PostgreSQL, Oracle, MongoDB 连接保活', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-005', NULL, 'BI_DASHBOARD', '可视化大屏与BI报表', '005', 1, 'bar-chart', 5, 0, '全场景多维数据统计、大屏拖拽与报表导出打印', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-005-01', 'dc-005', 'ECHARTS_WIDGETS', 'ECharts多维图表控件库', '005001', 2, 'pie-chart', 1, 0, '柱折饼、散点雷达、中国地图与热力图组件', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-005-02', 'dc-005', 'DASHBOARD_CANVAS', '自由拖拽大屏画布引擎', '005002', 2, 'desktop', 2, 0, '标尺网格吸附、成组锁定、大屏全屏自适应缩放', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-005-03', 'dc-005', 'CROSS_TAB_REPORT', '复杂交叉报表与精准套打', '005003', 2, 'printer', 3, 0, '多表头合并、小计合计计算公式、PDF/Excel矢量打印', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-005-04', 'dc-005', 'DRILL_DOWN_CHART', '图表数据下钻与实时过滤联动', '005004', 2, 'fund', 4, 0, '点击饼图扇区实时过滤关联表格，支持层级穿透', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-006', NULL, 'OPEN_MARKET', '开放平台与扩展市场', '006', 1, 'shop', 6, 0, '插件生态沙箱、行业应用模版与Webhook订阅', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-006-01', 'dc-006', 'SANDBOX_PLUGIN', '自定义JS/CSS插件沙箱', '006001', 2, 'tool', 1, 0, '用户编写前端增强脚本，安全执行防XSS注入', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-006-02', 'dc-006', 'TEMPLATE_STORE', '应用模版一键克隆与市场', '006002', 2, 'cloud-download', 2, 0, '项目管理、CRM、考勤打卡、工单套件开箱即用', '', NOW(), '', NOW());
INSERT INTO `pm_dict_category` VALUES ('dc-006-03', 'dc-006', 'WEBHOOK_EVENT', 'Webhook事件订阅与消息广播', '006003', 2, 'message', 3, 0, '需求变更、转测准入事件实时向外部系统Post通知', '', NOW(), '', NOW());

SET FOREIGN_KEY_CHECKS = 1;
