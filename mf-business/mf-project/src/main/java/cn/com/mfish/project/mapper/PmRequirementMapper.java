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
     * 网格列表查询：返回需求全部基础字段(刻意排除 prd_doc_content 大文本)，
     * 并通过 CASE 表达式派生 has_prd_doc 标记(是否已撰写详细文档)，用于"待撰PRD"等指示器。
     * 配合 PageHelper 分页，WHERE/ORDER BY 由 MP 条件构造器 ${ew.customSqlSegment} 注入。
     */
    @Select("SELECT id, req_no, module, level1_feature, level2_feature, level3_feature, feature_description, "
            + "prd_section, review_date, review_result, review_notes, vendors, priority, requester, request_date, "
            + "high_fidelity_url, change_date, change_content, pre_meeting_questions, release_time, frontend_dev, "
            + "backend_dev, qa_tester, remarks, owner, month, iteration_id, workload, frontend_workload, backend_workload, dev_start_date, dev_end_date, "
            + "dev_progress, test_phase, test_node_id, test_node_name, test_node_date, status, doc_last_modified, "
            + "doc_author, del_flag, remark, "
            + "CASE WHEN prd_doc_content IS NOT NULL AND CHAR_LENGTH(prd_doc_content) > 0 THEN 1 ELSE 0 END AS has_prd_doc "
            + "FROM pm_requirement ${ew.customSqlSegment}")
    List<PmRequirement> selectGridList(@Param(Constants.WRAPPER) com.baomidou.mybatisplus.core.conditions.Wrapper<PmRequirement> wrapper);

    /**
     * 回收站查询: 列出全部已逻辑删除(del_flag=1)的需求(排除 prd_doc_content 大文本, 派生 has_prd_doc)
     * 自定义 @Select 不走 @TableLogic 自动过滤, 显式限定 del_flag=1 按删除时间倒序(新删除的在前)
     */
    @Select("SELECT id, req_no, module, level1_feature, level2_feature, level3_feature, feature_description, "
            + "prd_section, review_date, review_result, review_notes, vendors, priority, requester, request_date, "
            + "high_fidelity_url, change_date, change_content, pre_meeting_questions, release_time, frontend_dev, "
            + "backend_dev, qa_tester, remarks, owner, month, iteration_id, workload, frontend_workload, backend_workload, dev_start_date, dev_end_date, "
            + "dev_progress, test_phase, test_node_id, test_node_name, test_node_date, status, doc_last_modified, "
            + "doc_author, create_by, create_time, update_by, update_time, del_flag, remark, "
            + "CASE WHEN prd_doc_content IS NOT NULL AND CHAR_LENGTH(prd_doc_content) > 0 THEN 1 ELSE 0 END AS has_prd_doc "
            + "FROM pm_requirement WHERE del_flag = 1 "
            + "ORDER BY COALESCE(update_time, create_time) DESC, req_no DESC")
    List<PmRequirement> selectDeletedList();

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
