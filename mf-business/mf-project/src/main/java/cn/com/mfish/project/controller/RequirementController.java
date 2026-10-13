package cn.com.mfish.project.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.req.ReqBalancingPlan;
import cn.com.mfish.project.req.ReqBatchAssign;
import cn.com.mfish.project.req.ReqBatchIds;
import cn.com.mfish.project.req.ReqBatchIteration;
import cn.com.mfish.project.req.ReqBatchReview;
import cn.com.mfish.project.req.ReqBatchStatus;
import cn.com.mfish.project.req.ReqBatchTestNode;
import cn.com.mfish.project.req.ReqPmRequirement;
import cn.com.mfish.project.req.ReqReassign;
import cn.com.mfish.project.req.ReqWorkload;
import cn.com.mfish.project.service.PmRequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @description: 核心需求管理
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Tag(name = "需求管理")
@RestController
@RequestMapping("/project/requirement")
public class RequirementController {

    @Resource
    private PmRequirementService requirementService;

    /**
     * 分页多维过滤查询需求列表
     * 支持按 month、iterationId、status、priority、人员、关键词模糊匹配(模块/功能名)分页检索
     */
    @Operation(summary = "需求管理-分页列表查询", description = "支持按月份、迭代、状态、优先级、人员、关键词多维过滤")
    @GetMapping("/list")
    @RequiresPermissions("project:requirement:query")
    public Result<PageResult<PmRequirement>> queryPageList(ReqPmRequirement reqPmRequirement,
                                                           @RequestParam(required = false) Integer pageNum,
                                                           @RequestParam(required = false) Integer pageSize) {
        return requirementService.queryPageList(reqPmRequirement, pageNum, pageSize);
    }

    /**
     * 全量不分页查询需求列表(甘特图看板实时联动)
     */
    @Operation(summary = "需求管理-全量列表查询", description = "不分页返回全部需求(甘特图看板联动)")
    @GetMapping("/all")
    @RequiresPermissions("project:requirement:query")
    public Result<List<PmRequirement>> queryAll(ReqPmRequirement reqPmRequirement) {
        Result<PageResult<PmRequirement>> pageResult = requirementService.queryPageList(reqPmRequirement, 1, 100000);
        if (pageResult.getData() == null) {
            return Result.fail("错误:需求列表-查询失败!");
        }
        return Result.ok(pageResult.getData().getList(), "需求列表-查询成功!");
    }

    /**
     * 通过id查询需求详情
     */
    @Operation(summary = "需求管理-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("project:requirement:query")
    public Result<PmRequirement> queryById(@Parameter(name = "id", description = "需求ID") @PathVariable String id) {
        return requirementService.queryById(id);
    }

    /**
     * 创建需求(若未传序号自动生成递增req_no，根据工时与日历规则自动推导dev_end_date)
     */
    @Log(title = "需求管理-添加", operateType = OperateType.INSERT)
    @Operation(summary = "需求管理-添加")
    @PostMapping
    @RequiresPermissions("project:requirement:insert")
    public Result<PmRequirement> add(@RequestBody PmRequirement requirement) {
        return requirementService.add(requirement);
    }

    /**
     * 更新需求(修改工时或开始时间时联动重算截止时间)
     */
    @Log(title = "需求管理-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-编辑")
    @PutMapping
    @RequiresPermissions("project:requirement:update")
    public Result<PmRequirement> edit(@RequestBody PmRequirement requirement) {
        return requirementService.edit(requirement);
    }

    /**
     * 通过id逻辑删除需求
     */
    @Log(title = "需求管理-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "需求管理-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("project:requirement:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "需求ID") @PathVariable String id) {
        return requirementService.delete(id);
    }

    /**
     * 获取需求PRD富文本正文内容
     */
    @Operation(summary = "需求管理-获取PRD文档")
    @GetMapping("/{id}/doc")
    @RequiresPermissions("project:requirement:query")
    public Result<PmRequirement> getDoc(@Parameter(name = "id", description = "需求ID") @PathVariable String id) {
        return requirementService.getDoc(id);
    }

    /**
     * 保存需求PRD富文本正文内容
     */
    @Log(title = "需求管理-保存PRD文档", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-保存PRD文档")
    @PutMapping("/{id}/doc")
    @RequiresPermissions("project:requirement:update")
    public Result<PmRequirement> saveDoc(@Parameter(name = "id", description = "需求ID") @PathVariable String id,
                                         @RequestBody Map<String, String> body) {
        return requirementService.saveDoc(id, body.get("prdDocContent"), body.get("docAuthor"));
    }

    /**
     * Excel/JSON 批量导入创建需求
     */
    @Log(title = "需求管理-批量导入", operateType = OperateType.INSERT)
    @Operation(summary = "需求管理-批量导入创建")
    @PostMapping("/batch")
    @RequiresPermissions("project:requirement:insert")
    public Result<List<PmRequirement>> batchCreate(@RequestBody List<PmRequirement> requirements) {
        return requirementService.batchCreate(requirements);
    }

    /**
     * 批量更新状态(如批量流转至"开发中"、"测试中")
     */
    @Log(title = "需求管理-批量更新状态", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-批量更新状态")
    @PutMapping("/batch/status")
    @RequiresPermissions("project:requirement:update")
    public Result<Integer> batchUpdateStatus(@Valid @RequestBody ReqBatchStatus req) {
        return requirementService.batchUpdateStatus(req.getIds(), req.getStatus());
    }

    /**
     * 批量分配或移除所属迭代
     */
    @Log(title = "需求管理-批量分配迭代", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-批量分配/移除所属迭代")
    @PutMapping("/batch/iteration")
    @RequiresPermissions("project:requirement:update")
    public Result<Integer> batchUpdateIteration(@Valid @RequestBody ReqBatchIteration req) {
        return requirementService.batchUpdateIteration(req.getIds(), req.getIterationId());
    }

    /**
     * 批量更新评审结果与评审意见
     */
    @Log(title = "需求管理-批量更新评审", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-批量更新评审结果")
    @PutMapping("/batch/review")
    @RequiresPermissions("project:requirement:update")
    public Result<Integer> batchUpdateReview(@Valid @RequestBody ReqBatchReview req) {
        return requirementService.batchUpdateReview(req.getIds(), req.getReviewResult(), req.getReviewDate(), req.getReviewNotes());
    }

    /**
     * 批量分配前端/后端/测试人员
     */
    @Log(title = "需求管理-批量指派人员", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-批量分配人员")
    @PutMapping("/batch/assign")
    @RequiresPermissions("project:requirement:update")
    public Result<Integer> batchAssign(@Valid @RequestBody ReqBatchAssign req) {
        return requirementService.batchAssign(req.getIds(), req.getPersonnelType(), req.getNames());
    }

    /**
     * 批量绑定转测节点
     */
    @Log(title = "需求管理-批量绑定转测节点", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-批量绑定转测节点")
    @PutMapping("/batch/test-node")
    @RequiresPermissions("project:requirement:update")
    public Result<Integer> batchBindTestNode(@Valid @RequestBody ReqBatchTestNode req) {
        return requirementService.batchBindTestNode(req.getIds(), req.getNodeId());
    }

    /**
     * 批量逻辑删除需求
     */
    @Log(title = "需求管理-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "需求管理-批量逻辑删除")
    @DeleteMapping("/batch")
    @RequiresPermissions("project:requirement:delete")
    public Result<Boolean> deleteBatch(@Valid @RequestBody ReqBatchIds req) {
        return requirementService.deleteBatch(req.getIds());
    }

    /**
     * 快捷调配与工时微调(支持甘特图看板实时联动)
     * transferAll=true 时将整项需求移交目标开发人员独占；单角色替换仅替换对应岗位成员，并记录审计日志
     */
    @Log(title = "需求管理-人员调配", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-人员调配与任务转派")
    @PostMapping("/reassign")
    @RequiresPermissions("project:requirement:update")
    public Result<PmRequirement> reassign(@Valid @RequestBody ReqReassign req) {
        return requirementService.reassign(req);
    }

    /**
     * 修改需求工时，并根据日历跳过规则自动重新计算更新 dev_end_date
     */
    @Log(title = "需求管理-工时微调", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-调整预估工时")
    @PostMapping("/workload")
    @RequiresPermissions("project:requirement:update")
    public Result<PmRequirement> updateWorkload(@Valid @RequestBody ReqWorkload req) {
        return requirementService.updateWorkload(req);
    }

    /**
     * 一键应用智能负载均衡方案：在单事务中批量执行调配并记录审计日志
     */
    @Log(title = "需求管理-批量负载均衡", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-批量应用智能负载均衡调配方案")
    @PostMapping("/batch-balancing")
    @RequiresPermissions("project:requirement:update")
    public Result<Integer> batchBalancing(@Valid @RequestBody ReqBalancingPlan req) {
        return requirementService.batchBalancing(req.getPlan());
    }

    /**
     * 智能自动日历排期编排：按人员分组合并任务，遵循工作日及节假日跳过规则，
     * 实现任务紧凑首尾串行排期
     */
    @Log(title = "需求管理-智能自动排期", operateType = OperateType.UPDATE)
    @Operation(summary = "需求管理-智能自动日历排期编排")
    @PostMapping("/auto-schedule")
    @RequiresPermissions("project:requirement:update")
    public Result<Map<String, Object>> autoSchedule(@RequestBody(required = false) ReqBatchIds req) {
        return requirementService.autoSchedule(req == null ? null : req.getIds());
    }
}
