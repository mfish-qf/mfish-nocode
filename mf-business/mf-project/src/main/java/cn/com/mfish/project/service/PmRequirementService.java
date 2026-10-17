package cn.com.mfish.project.service;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.req.BalancingPlanItem;
import cn.com.mfish.project.req.ReqPmRequirement;
import cn.com.mfish.project.req.ReqReassign;
import cn.com.mfish.project.req.ReqWorkload;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @description: 核心需求
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmRequirementService extends IService<PmRequirement> {

    /**
     * 分页多维过滤查询需求列表
     * 支持按 month、iterationId、status、priority、人员(frontendDev/backendDev/qaTester)、
     * 关键词模糊匹配(模块/功能名)分页检索
     *
     * @param reqPmRequirement 查询参数
     * @param pageNum           页码
     * @param pageSize          每页条数
     * @return 分页结果
     */
    Result<PageResult<PmRequirement>> queryPageList(ReqPmRequirement reqPmRequirement, Integer pageNum, Integer pageSize);

    /**
     * 通过id查询需求详情
     *
     * @param id 需求ID
     * @return 需求详情
     */
    Result<PmRequirement> queryById(String id);

    /**
     * 创建需求(若未传序号自动生成递增req_no，根据工时与日历规则自动推导dev_end_date)
     *
     * @param requirement 需求对象
     * @return 添加结果
     */
    Result<PmRequirement> add(PmRequirement requirement);

    /**
     * 更新需求(修改工时或开始时间时联动重算截止时间)
     *
     * @param requirement 需求对象
     * @return 更新结果
     */
    Result<PmRequirement> edit(PmRequirement requirement);

    /**
     * 通过id逻辑删除需求
     *
     * @param id 需求ID
     * @return 删除结果
     */
    Result<Boolean> delete(String id);

    /**
     * 批量逻辑删除需求
     *
     * @param ids 需求ID列表
     * @return 删除结果
     */
    Result<Boolean> deleteBatch(List<String> ids);

    /**
     * 获取需求PRD富文本正文内容
     *
     * @param id 需求ID
     * @return 文档内容
     */
    Result<PmRequirement> getDoc(String id);

    /**
     * 分页批量获取需求PRD富文本正文内容(仅返回必要字段 + prd_doc_content)
     * 用于版本 Word 导出等需要聚合大量需求文档的场景，按给定 ID 列表分页拉取，避免一次性传输超大文本。
     *
     * @param ids      需求ID列表
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果(仅含 id/reqNo/module/level1Feature/prdDocContent/docLastModified/docAuthor)
     */
    Result<PageResult<PmRequirement>> queryDocBatch(List<String> ids, Integer pageNum, Integer pageSize);

    /**
     * 保存需求PRD富文本正文内容
     *
     * @param id            需求ID
     * @param prdDocContent PRD文档内容
     * @param docAuthor     文档编写人
     * @return 保存结果
     */
    Result<PmRequirement> saveDoc(String id, String prdDocContent, String docAuthor);

    /**
     * Excel/JSON 批量导入创建需求
     *
     * @param requirements 需求列表
     * @return 导入结果
     */
    Result<List<PmRequirement>> batchCreate(List<PmRequirement> requirements);

    /**
     * 批量更新状态(如批量流转至"开发中"、"测试中")
     *
     * @param ids    需求ID列表
     * @param status 目标状态
     * @return 更新结果
     */
    Result<Integer> batchUpdateStatus(List<String> ids, String status);

    /**
     * 批量分配或移除所属迭代
     *
     * @param ids         需求ID列表
     * @param iterationId 迭代ID(为空表示移除)
     * @return 更新结果
     */
    Result<Integer> batchUpdateIteration(List<String> ids, String iterationId);

    /**
     * 批量更新评审结果与评审意见
     *
     * @param ids          需求ID列表
     * @param reviewResult 评审结果
     * @param reviewDate   评审日期
     * @param reviewNotes  评审意见
     * @return 更新结果
     */
    Result<Integer> batchUpdateReview(List<String> ids, String reviewResult, java.util.Date reviewDate, String reviewNotes);

    /**
     * 批量分配前端/后端/测试人员
     *
     * @param ids            需求ID列表
     * @param personnelType 人员类型(frontendDev/backendDev/qaTester/owner)
     * @param names         人员姓名串
     * @return 更新结果
     */
    Result<Integer> batchAssign(List<String> ids, String personnelType, String names);

    /**
     * 批量绑定转测节点
     *
     * @param ids    需求ID列表
     * @param nodeId 转测节点ID
     * @return 更新结果
     */
    Result<Integer> batchBindTestNode(List<String> ids, String nodeId);

    /**
     * 快捷人员调配与任务转派
     * 若 transferAll=true，将整项需求移交目标开发人员独占；若为单角色替换，
     * 仅替换对应岗位的成员，并记录 PmRequirementHistory 审计日志
     *
     * @param reqReassign 调配参数
     * @return 调配结果(含更新后的需求)
     */
    Result<PmRequirement> reassign(ReqReassign reqReassign);

    /**
     * 修改需求工时，并根据日历跳过规则自动重新计算更新 dev_end_date
     *
     * @param reqWorkload 工时调整参数
     * @return 更新结果
     */
    Result<PmRequirement> updateWorkload(ReqWorkload reqWorkload);

    /**
     * 一键应用智能负载均衡方案：接收调配计划列表，在单事务中批量执行调配并记录审计日志
     *
     * @param plan 调配计划列表
     * @return 执行结果(含影响需求数)
     */
    Result<Integer> batchBalancing(List<BalancingPlanItem> plan);

    /**
     * 智能自动日历排期编排：按人员分组合并任务，遵循工作日及节假日跳过规则，
     * 实现任务紧凑首尾串行排期
     *
     * @param ids 需求ID列表(为空时对全部需求排期)
     * @return 排期结果(含排期数量与更新后的列表)
     */
    Result<Map<String, Object>> autoSchedule(List<String> ids);
}
