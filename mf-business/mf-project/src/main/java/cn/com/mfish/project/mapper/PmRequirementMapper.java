package cn.com.mfish.project.mapper;

import cn.com.mfish.project.entity.PmRequirement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @description: 核心需求
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmRequirementMapper extends BaseMapper<PmRequirement> {

    /**
     * 研发起止时间列：实时关联排期明细，按同一 req_id 聚合最早开始 / 最晚结束。
     * 列表查询与回收站查询共用，保证前端看到的口径完全一致；无明细时为 NULL。
     */
    String SCHEDULE_RANGE_COLUMNS =
            "(SELECT MIN(ds.dev_start_date) FROM pm_dev_schedule ds WHERE ds.req_id = pm_requirement.id) AS dev_start_date, "
                    + "(SELECT MAX(ds.dev_end_date) FROM pm_dev_schedule ds WHERE ds.req_id = pm_requirement.id) AS dev_end_date, ";

    /**
     * 网格列表查询：返回需求全部基础字段(刻意排除 prd_doc_content 大文本)，
     * 并通过 CASE 表达式派生 has_prd_doc 标记(是否已撰写详细文档)，用于"待撰PRD"等指示器。
     * 配合 PageHelper 分页，WHERE/ORDER BY 由 MP 条件构造器 ${ew.customSqlSegment} 注入。
     *
     * <p>【pm_requirement 已无 dev_start_date / dev_end_date 列】这两个别名由实时关联
     * pm_dev_schedule 按同一 req_id 取 MIN(dev_start_date) / MAX(dev_end_date) 得出：
     * 一个需求可拆成多个人员×岗位任务，需求整体区间即最早开始 ~ 最晚结束。
     * 无排期明细时返回 NULL(前端显示 '-')。PmRequirement 实体对应字段标注
     * {@code @TableField(exist = false)}，只承接查询结果、不参与任何 INSERT/UPDATE。</p>
     *
     * <p>用相关标量子查询而非 LEFT JOIN：避免 id/del_flag/tenant_id 等裸列名歧义（与 month 列同一手法）。
     * RequirementController 上声明了 @DataScope(table="pm_dev_schedule")，SelectInterceptor 会把此处表名
     * 改写为 (select * from pm_dev_schedule where tenant_id=...) 并保留别名 ds，自动完成租户隔离；
     * 子查询里的 pm_requirement.id 因表名后紧跟 '.' 会被 END_KEYWORD 守卫跳过，仍正确引用外层表。
     * pm_dev_schedule 已有 idx_dev_schedule_req(req_id) 索引，列表最多 2000 行也只是索引查找。</p>
     */
    @Select("SELECT id, req_no, module, level1_feature, level2_feature, level3_feature, feature_description, "
            + "prd_section, review_date, review_result, review_notes, vendors, priority, requester, request_date, "
            + "high_fidelity_url, change_date, change_content, pre_meeting_questions, release_time, frontend_dev, "
            // month(版本号)为派生列: 由 iteration_id 关联 pm_iteration.code 取得, 库表不再存该字段。
            // 用相关标量子查询而非 LEFT JOIN —— 后者会让 id/del_flag/status/create_time 等裸列名在两表中均存在,
            // 触发 MySQL "Column 'xxx' is ambiguous"(含 MP 注入的 del_flag 条件与 @DataScope 注入的 tenant_id)。
            + "backend_dev, qa_tester, remarks, owner, iteration_id, "
            + "(SELECT pi.code FROM pm_iteration pi WHERE pi.id = pm_requirement.iteration_id AND pi.del_flag = 0) AS month, "
            + "workload, frontend_workload, backend_workload, "
            + SCHEDULE_RANGE_COLUMNS
            + "dev_progress, test_phase, test_node_id, test_node_name, test_node_date, status, doc_last_modified, "
            + "doc_author, del_flag, "
            + "CASE WHEN prd_doc_content IS NOT NULL AND CHAR_LENGTH(prd_doc_content) > 0 THEN 1 ELSE 0 END AS has_prd_doc "
            + "FROM pm_requirement "
            + "${ew.customSqlSegment}")
    List<PmRequirement> selectGridList(@Param(Constants.WRAPPER) com.baomidou.mybatisplus.core.conditions.Wrapper<PmRequirement> wrapper);

    /**
     * 回收站查询: 列出全部已逻辑删除(del_flag=1)的需求(排除 prd_doc_content 大文本, 派生 has_prd_doc)
     * 自定义 @Select 不走 @TableLogic 自动过滤, 显式限定 del_flag=1 按删除时间倒序(新删除的在前)
     */
    @Select("SELECT id, req_no, module, level1_feature, level2_feature, level3_feature, feature_description, "
            + "prd_section, review_date, review_result, review_notes, vendors, priority, requester, request_date, "
            + "high_fidelity_url, change_date, change_content, pre_meeting_questions, release_time, frontend_dev, "
            + "backend_dev, qa_tester, remarks, owner, iteration_id, "
            + "(SELECT pi.code FROM pm_iteration pi WHERE pi.id = pm_requirement.iteration_id AND pi.del_flag = 0) AS month, "
            + "workload, frontend_workload, backend_workload, "
            + SCHEDULE_RANGE_COLUMNS
            + "dev_progress, test_phase, test_node_id, test_node_name, test_node_date, status, doc_last_modified, "
            + "doc_author, create_by, create_time, "
            // 历史数据删除时未写 update_by/update_time(逻辑删除只置 del_flag)，用最新一条 DELETE 审计日志兜底，
            // 避免回收站「删除信息」显示「未知操作人 / 时间未知」。新数据以主表实写值为准。
            + "COALESCE(pm_requirement.update_by, (SELECT h.create_by FROM pm_requirement_history h "
            + "  WHERE h.req_id = pm_requirement.id AND h.action_type = 'DELETE' "
            + "  ORDER BY h.create_time DESC, h.id DESC LIMIT 1)) AS update_by, "
            + "COALESCE(pm_requirement.update_time, (SELECT h.create_time FROM pm_requirement_history h "
            + "  WHERE h.req_id = pm_requirement.id AND h.action_type = 'DELETE' "
            + "  ORDER BY h.create_time DESC, h.id DESC LIMIT 1)) AS update_time, "
            + "del_flag, "
            + "CASE WHEN prd_doc_content IS NOT NULL AND CHAR_LENGTH(prd_doc_content) > 0 THEN 1 ELSE 0 END AS has_prd_doc "
            + "FROM pm_requirement "
            + "WHERE del_flag = 1 "
            + "ORDER BY COALESCE(update_time, create_time) DESC, req_no DESC")
    List<PmRequirement> selectDeletedList();

    /**
     * 按主键查询(含已逻辑删除 del_flag=1 的行): 用于回收站恢复/彻底删除前的归属与存在性校验。
     * 自定义 @Select 不走 @TableLogic 自动过滤(del_flag=0)，否则回收站内的需求永远查不到。
     */
    @Select("SELECT id, req_no, tenant_id, del_flag FROM pm_requirement WHERE id = #{id}")
    PmRequirement selectByIdIncludeDeleted(@Param("id") String id);

    /**
     * 按主键查询 PRD 详细文档(含已逻辑删除 del_flag=1 的行): 供回收站查看已删除需求的详细文档。
     * 自定义 @Select 不走 @TableLogic 自动过滤(del_flag=0)，否则回收站内的需求永远查不到；
     * 显式带 tenant_id 防止跨租户读取；仅取文档相关字段，避免拉取大宽表。
     */
    @Select("SELECT id, req_no, module, level1_feature, level2_feature, prd_doc_content, "
            + "doc_last_modified, doc_author FROM pm_requirement "
            + "WHERE id = #{id} AND tenant_id = #{tenantId}")
    PmRequirement selectDocByIdIncludeDeleted(@Param("id") String id, @Param("tenantId") String tenantId);

    /**
     * 从回收站恢复需求: 将 del_flag 置回 0(原生 UPDATE, 否则 @TableLogic 会自动追加 del_flag=0 条件导致 0 行受影响)
     * 带 del_flag=1 条件防并发重复恢复; 记录恢复操作人与时间
     */
    @Update("UPDATE pm_requirement SET del_flag = 0, update_by = #{updateBy}, update_time = NOW() "
            + "WHERE id = #{id} AND del_flag = 1")
    int restoreDeleted(@Param("id") String id, @Param("updateBy") String updateBy);

    /**
     * 彻底删除需求: 物理 DELETE(区别于 MP 逻辑删除, 用于回收站永久清除, 数据不可恢复)
     */
    @Delete("DELETE FROM pm_requirement WHERE id = #{id}")
    int physicalDeleteById(@Param("id") String id);
}
