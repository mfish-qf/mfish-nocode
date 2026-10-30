-- =============================================================================
-- PM 人员「姓名 → 用户ID」数据回填（简化版）
-- -----------------------------------------------------------------------------
-- 目标: 把 pm_requirement 的人名串按姓名解析为 sso_user.id, 写回
--       frontend_dev_ids / backend_dev_ids / qa_tester_ids / owner_ids;
--       并把 pm_dev_schedule.person_name 解析为 person_id。
-- 约定(按需求, 一切从简):
--   - 人名视为【全局唯一】, 直接按 sso_user.nickname(退化 account) 匹配,
--     不做租户 / 角色 / 歧义判定
--   - 多个人名按 、 , ， 空格 / 分隔, 解析后按原顺序拼成与姓名串对齐的 JSON 数组
--   - 兼容历史遗留 ["a","b"] 形式
-- 运行前提: 已先执行 mf_project_person_id_migration.sql(Part A) 把列加上。
-- 幂等: 每次都从姓名串重算, 可安全重跑。
-- =============================================================================

SET NAMES utf8mb4;
SET SESSION group_concat_max_len = 1000000;

-- -----------------------------------------------------------------------------
-- 1) 需求表四个 *_ids 列
-- -----------------------------------------------------------------------------
WITH RECURSIVE _seq(n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM _seq WHERE n < 100
),
-- 四个姓名串合并为 (需求id, 列标记, 原始串)
src AS (
    SELECT id, 'fe' AS col, frontend_dev AS raw FROM pm_requirement WHERE del_flag = 0 AND frontend_dev IS NOT NULL AND frontend_dev <> ''
    UNION ALL
    SELECT id, 'be', backend_dev FROM pm_requirement WHERE del_flag = 0 AND backend_dev IS NOT NULL AND backend_dev <> ''
    UNION ALL
    SELECT id, 'qa', qa_tester FROM pm_requirement WHERE del_flag = 0 AND qa_tester IS NOT NULL AND qa_tester <> ''
    UNION ALL
    SELECT id, 'ow', owner FROM pm_requirement WHERE del_flag = 0 AND owner IS NOT NULL AND owner <> ''
),
-- 按分隔符拆词(保留位置 pos), 并剥掉 JSON 数组外壳与引号
tok AS (
    SELECT s.id, s.col, n.n AS pos,
           REGEXP_SUBSTR(REGEXP_REPLACE(s.raw, '^\\[|\\]$', ''), '[^、,，/[:space:]"'']+', 1, n.n) AS name
    FROM src s
    JOIN _seq n
      ON REGEXP_SUBSTR(REGEXP_REPLACE(s.raw, '^\\[|\\]$', ''), '[^、,，/[:space:]"'']+', 1, n.n) IS NOT NULL
),
-- 按姓名查 sso_user.id(nickname 优先, account 兜底; 人名唯一, LIMIT 1)
rid AS (
    SELECT t.id, t.col, t.pos, t.name,
           (SELECT u.id FROM sso_user u WHERE u.nickname = t.name OR u.account = t.name LIMIT 1) AS uid
    FROM tok t
    WHERE t.name IS NOT NULL AND t.name <> ''
),
-- 按 pos 顺序拼成 JSON 数组(查不到的位写 null, 保持索引对齐)
arr AS (
    SELECT id, col,
           CONCAT('[',
                  GROUP_CONCAT(CASE WHEN uid IS NULL THEN 'null' ELSE JSON_QUOTE(uid) END
                                ORDER BY pos SEPARATOR ','),
                  ']') AS json
    FROM rid
    GROUP BY id, col
)
UPDATE pm_requirement r
JOIN (
    SELECT id,
           MAX(CASE col WHEN 'fe' THEN json END) AS frontend_dev_ids,
           MAX(CASE col WHEN 'be' THEN json END) AS backend_dev_ids,
           MAX(CASE col WHEN 'qa' THEN json END) AS qa_tester_ids,
           MAX(CASE col WHEN 'ow' THEN json END) AS owner_ids
    FROM arr
    GROUP BY id
) a ON a.id = r.id
SET r.frontend_dev_ids = a.frontend_dev_ids,
    r.backend_dev_ids  = a.backend_dev_ids,
    r.qa_tester_ids   = a.qa_tester_ids,
    r.owner_ids       = a.owner_ids;

-- -----------------------------------------------------------------------------
-- 2) 排期明细 person_id(单行单名, 直接解析)
-- -----------------------------------------------------------------------------
UPDATE pm_dev_schedule ds
SET ds.person_id = (
    SELECT u.id FROM sso_user u
    WHERE u.nickname = ds.person_name OR u.account = ds.person_name
    LIMIT 1
)
WHERE ds.person_name IS NOT NULL AND ds.person_name <> '';
