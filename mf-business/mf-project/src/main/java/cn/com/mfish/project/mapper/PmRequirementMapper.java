package cn.com.mfish.project.mapper;

import cn.com.mfish.project.entity.PmRequirement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
