-- =====================================================================
-- 删除 pm_requirement.month 冗余列
-- 背景: month 仅存储迭代版本代号(= pm_iteration.code), 与 iteration_id 完全冗余。
--       版本号统一改为由 iteration_id -> pm_iteration.code 派生返回(实体 month 字段标记
--       @TableField(exist=false), 由 selectGridList / selectDeletedList 用相关标量子查询填充、
--       queryById 查表填充), 不再落库。
--       注意: 派生不可用 LEFT JOIN —— pm_iteration 同样含 id/del_flag/status/create_time 等列,
--       裸列名会触发 MySQL ERROR 1052 "Column 'xxx' is ambiguous"(含 MP 注入的 del_flag 条件)。
-- 步骤:
--   1) 回填: 仅存 month(版本代号) 但无 iteration_id 的历史需求, 按 code 关联回填 iteration_id
--      (当前开发库实测 old_month_only=0, 此步为其它环境兜底; 幂等可重复执行)
--   2) DROP COLUMN month(幂等, 列已不存在则跳过)
--      注: month 列上的单列索引 idx_pm_req_month 由 MySQL 随 DROP COLUMN 自动移除, 无需单独处理
-- 执行库: mfish_nocode
-- =====================================================================

UPDATE pm_requirement r
SET r.iteration_id = (
    SELECT i.id FROM pm_iteration i
    WHERE i.code = r.month AND i.del_flag = 0
    LIMIT 1
)
WHERE r.del_flag = 0
  AND (r.iteration_id IS NULL OR r.iteration_id = '')
  AND r.month IS NOT NULL AND r.month <> ''
  AND r.month <> 'UNASSIGNED' AND r.month <> '未纳入版本';

SET @db  = 'mfish_nocode';
SET @tbl = 'pm_requirement';
SET @col = 'month';

SET @exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @db
      AND table_name   = @tbl
      AND column_name  = @col
);

SET @sql = IF(
    @exists > 0,
    CONCAT('ALTER TABLE `', @tbl, '` DROP COLUMN `', @col, '`'),
    'SELECT 1 AS skipped'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
