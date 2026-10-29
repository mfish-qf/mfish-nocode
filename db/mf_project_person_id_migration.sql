-- =============================================================================
-- PM 排期/需求人员「账号化」迁移脚本
-- 目标: 需求表承载原子用户ID(JSON数组, 与姓名按索引对齐), 排期明细增加 person_id
-- 设计: ID 优先 + 姓名兜底
--   - 排期身份 key = person_id 非空 ? person_id : person_name
--   - 能唯一匹配到账号的人 -> 用 userId 精确区分(解决同租户同名)
--   - 匹配不到 / 租户内多人同名 -> person_id 为 NULL, 自动降级为按姓名处理(与改造前行为一致)
--
-- 适用库: 已存在 PM 表的数据库。全新安装直接用 db/mf_project.sql(已含本脚本的所有列)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Part A. 结构变更(需求表字段可通过字段名判空重复执行)
-- -----------------------------------------------------------------------------
ALTER TABLE `pm_requirement`
    ADD COLUMN `frontend_dev_ids` varchar(1000) NULL DEFAULT NULL COMMENT '前端人员用户ID列表(JSON数组, 与frontend_dev按索引对齐, null表示该位未匹配到账号)' AFTER `frontend_dev`;
ALTER TABLE `pm_requirement`
    ADD COLUMN `backend_dev_ids` varchar(1000) NULL DEFAULT NULL COMMENT '后端人员用户ID列表(JSON数组, 与backend_dev按索引对齐, null表示该位未匹配到账号)' AFTER `backend_dev`;
ALTER TABLE `pm_requirement`
    ADD COLUMN `qa_tester_ids` varchar(1000) NULL DEFAULT NULL COMMENT '测试人员用户ID列表(JSON数组, 与qa_tester按索引对齐, null表示该位未匹配到账号)' AFTER `qa_tester`;
ALTER TABLE `pm_requirement`
    ADD COLUMN `owner_ids` varchar(1000) NULL DEFAULT NULL COMMENT '关联负责人用户ID列表(JSON数组, 与owner按索引对齐, null表示该位未匹配到账号)' AFTER `owner`;

ALTER TABLE `pm_dev_schedule`
    ADD COLUMN `person_id` varchar(32) NULL DEFAULT NULL COMMENT '开发人员用户ID(排期身份主键, 为空则按姓名兜底)' AFTER `person_name`;
-- 存在即视为已迁移; MySQL 不支持 CREATE INDEX IF NOT EXISTS, 重复执行报错可忽略
ALTER TABLE `pm_dev_schedule` ADD INDEX `idx_dev_schedule_person_id` (`person_id`);

-- -----------------------------------------------------------------------------
-- Part B. 数据回填(必须走应用层, 不能在 SQL 里直连 sso 表)
--
-- 原因: 「姓名 -> 用户ID」必须遵守「租户内唯一命中才采用」的规则,
--       而租户归属需要 user -> sso_org_user -> sso_org 的 org_code 前缀判定,
--       且歧义(租户内多人同名)必须在应用层被识别并跳过。用纯 SQL 回填容易把
--       排期静默挂到另一个同名同事名下。
--
-- 推荐顺序:
--   1) 回填需求表的 *_ids: 对每个需求按角色解析姓名 -> id 数组
--      (走内部接口 POST /user/tenantUserIds, tenantId 取该需求的 tenant_id)
--      规则: RESOLVED 落 id; UNRESOLVED / AMBIGUOUS 落 NULL
--   2) 回填排期明细 person_id: 通过 req_id 关联到需求, 按 dev_role 找到对应角色的
--      姓名数组, 用 person_name 在其中的下标取同位置的 *_ids 值
--      (这一步也可不手工执行 —— 下一次重算/自愈会按 Phase 3 的自愈逻辑自动补齐)
--   3) 校验缺失情况: 见 Part C
-- -----------------------------------------------------------------------------

-- -----------------------------------------------------------------------------
-- Part C. 校验查询
-- -----------------------------------------------------------------------------
-- C1. 仍未回填 person_id 的排期行数量(预期随自愈逐步下降, 不为 0 属正常 —— 未匹配到账号的人)
SELECT r.dev_role, COUNT(*) AS rows_missing_person_id
FROM pm_dev_schedule ds
         JOIN pm_requirement r ON r.id = ds.req_id
WHERE ds.person_id IS NULL
GROUP BY r.dev_role;

-- C2. 同租户同名且已解析出不同用户ID的人员(应预期为 0 表示账号化生效后同名人被正确区分)
--     若本查询有结果, 说明这些姓名在租户内其实已能区分, 检查是否回填遗漏
SELECT ds.tenant_id, ds.person_name, COUNT(DISTINCT ds.person_id) AS distinct_user_ids
FROM pm_dev_schedule ds
WHERE ds.person_id IS NOT NULL
GROUP BY ds.tenant_id, ds.person_name
HAVING COUNT(DISTINCT ds.person_id) > 1;
