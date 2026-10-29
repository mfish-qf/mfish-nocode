-- =====================================================================
-- 删除 pm_requirement 冗余字段 remark（与业务字段 remarks 重名易混，且 PM 业务从未读写）
-- 说明：
--   1. remarks（复数）才是需求表真实在用的「备注/疑问」业务字段（前端 RequirementModal 绑定、授权可编辑）。
--   2. remark（单数）是早期框架遗留的空列，实体 PmRequirement 已无该字段映射，无任何 Java/前端代码引用。
--   3. 其它表（pm_iteration / pm_dev_schedule 等）的 remark 列不在本次清理范围，保持不变。
-- 幂等：列已不存在时自动跳过，可重复执行。
-- 执行库：mfish_nocode
-- =====================================================================

SET @db   = 'mfish_nocode';
SET @tbl  = 'pm_requirement';
SET @col  = 'remark';

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
