-- ----------------------------------------
--  pm 模块多租户迁移脚本（存量库执行一次）
--  对应 db/mf_project.sql 中 3 张表新增 tenant_id 列：
--    pm_requirement / pm_dev_schedule / pm_iteration
--  （pm_iteration_test_node 与 pm_requirement_history 是对应主表的子表，
--    数据经主表 id 关联、随主表 @DataScope 一并隔离，无需单独持有 tenant_id，
--    故本脚本不包含这两张表）
--  全新库直接执行 mf_project.sql 即可，无需本文件。
--
--  重要：本脚本会把历史数据的 tenant_id 统一回填为 <TENANT_ID> 占位符。
--  请在“第 3 步回填”中把 <TENANT_ID> 替换为当前实际租户 ID
--  （登录后从网关注入的 req-tenant-id 取值；单租户环境即系统默认租户）。
--  若不回填，@DataScope 租户过滤条件 tenant_id='当前租户' 会使所有历史数据不可见。
-- ----------------------------------------
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) 新增 tenant_id 列（置于 remark 之后）
ALTER TABLE `pm_requirement`
  ADD COLUMN `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户ID(多租户隔离)' AFTER `remark`;
ALTER TABLE `pm_dev_schedule`
  ADD COLUMN `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户ID(多租户隔离)' AFTER `remark`;
ALTER TABLE `pm_iteration`
  ADD COLUMN `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户ID(多租户隔离)' AFTER `remark`;

-- 2) 新增租户索引（与 mf_project.sql 命名保持一致）
ALTER TABLE `pm_requirement`          ADD INDEX `idx_pm_req_tenant`(`tenant_id`);
ALTER TABLE `pm_dev_schedule`         ADD INDEX `idx_dev_schedule_tenant`(`tenant_id`);
ALTER TABLE `pm_iteration`            ADD INDEX `idx_pm_iter_tenant`(`tenant_id`);

-- 3) 回填历史数据（务必替换 <TENANT_ID> 为实际租户 ID）
--    多租户环境若为不同租户的历史数据，请按各自归属分别 UPDATE。
UPDATE `pm_requirement`          SET `tenant_id` = 'a90dc8fdfb63b73102cfdb6a5d52dc5e' WHERE `tenant_id` IS NULL OR `tenant_id` = '';
UPDATE `pm_dev_schedule`         SET `tenant_id` = 'a90dc8fdfb63b73102cfdb6a5d52dc5e' WHERE `tenant_id` IS NULL OR `tenant_id` = '';
UPDATE `pm_iteration`            SET `tenant_id` = 'a90dc8fdfb63b73102cfdb6a5d52dc5e' WHERE `tenant_id` IS NULL OR `tenant_id` = '';

SET FOREIGN_KEY_CHECKS = 1;
