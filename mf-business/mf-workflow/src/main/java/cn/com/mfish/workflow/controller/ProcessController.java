package cn.com.mfish.workflow.controller;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.*;
import cn.com.mfish.common.workflow.api.req.ReqAllTask;
import cn.com.mfish.common.workflow.api.req.ReqProcess;
import cn.com.mfish.common.workflow.api.req.ReqTask;
import cn.com.mfish.common.workflow.enums.AuditOperator;
import cn.com.mfish.common.workflow.service.FlowableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 工作流
 * @author: mfish
 * @date: 2025/9/10
 */
@RestController
@RequestMapping("/process")
@Tag(name = "工作流接口")
public class ProcessController {

    private final FlowableService flowableService;

    public ProcessController(FlowableService flowableService) {
        this.flowableService = flowableService;
    }

    @Operation(summary = "手动部署流程")
    @GetMapping("/deploy/{name}")
    public Result<String> deployProcess(@Parameter(name = "name", description = "流程定义文件名称 例如：test.bpmn20.xml文件名称传test") @PathVariable String name) {
        flowableService.deployProcess(name);
        return Result.ok("部署成功");
    }

    @Operation(summary = "分页查询流程列表")
    @GetMapping
    public Result<PageResult<MfProcess>> getProcessList(ReqProcess reqProcess, ReqPage reqPage) {
        return Result.ok(flowableService.getProcessList(reqProcess, reqPage), "查询流程列表成功");
    }

    @Operation(summary = "分页查询历史流程列表")
    @GetMapping("/history")
    public Result<PageResult<MfProcess>> getHistoryProcessList(ReqProcess reqProcess, ReqPage reqPage) {
        return Result.ok(flowableService.getHistoryProcessList(reqProcess, reqPage), "查询历史流程列表成功");
    }

    @Operation(summary = "启动流程")
    @PostMapping("/start")
    public Result<String> startProcess(@RequestBody FlowableParam<?> flowableParam) {
        return Result.ok(flowableService.startProcess(flowableParam), "启动流程实例成功");
    }

    @Operation(summary = "通过业务key删除流程实例")
    @DeleteMapping("/{processInstanceId}")
    public Result<String> delProcess(@Parameter(name = "processInstanceId", description = "流程实例id") @PathVariable String processInstanceId, @RequestParam String reason) {
        return flowableService.delProcess(processInstanceId, null, reason);
    }

    @Operation(summary = "通过业务key删除流程实例")
    @DeleteMapping("/businessKey/{businessKey}")
    public Result<String> delProcessByBusinessKey(@Parameter(name = "businessKey", description = "业务id") @PathVariable String businessKey, @RequestParam String reason) {
        return flowableService.delProcess(null, businessKey, reason);
    }

    @Operation(summary = "查询流程图片")
    @GetMapping("/image/{processInstanceId}")
    public Result<String> queryImage(@Parameter(name = "processInstanceId", description = "流程实例id") @PathVariable("processInstanceId") String processInstanceId) {
        return Result.ok(flowableService.queryImage(processInstanceId), "查询流程图片成功");
    }

    @Operation(summary = "查询实例的审批意见")
    @GetMapping("/comments/{processInstanceId}")
    public Result<List<AuditComment>> getAuditComments(@Parameter(name = "processInstanceId", description = "流程实例id") @PathVariable String processInstanceId) {
        return Result.ok(flowableService.getAuditComments(processInstanceId), "查询审批意见列表成功");
    }

    @Operation(summary = "查询实例的任务进度")
    @GetMapping("/tasks/{processInstanceId}")
    public Result<List<MfTask>> getProcessTasks(@Parameter(name = "processInstanceId", description = "流程实例id") @PathVariable String processInstanceId) {
        return Result.ok(flowableService.getProcessTasks(processInstanceId), "查询任务列表成功");
    }

    @Operation(summary = "查询待处理任务列表")
    @GetMapping("/tasks/pending")
    public Result<PageResult<MfTask>> getPendingTasks(ReqTask reqTask, ReqPage reqPage) {
        return Result.ok(flowableService.getPendingTasks(reqTask, reqPage), "查询待处理任务列表成功");
    }

    @Operation(summary = "查询所有任务列表")
    @GetMapping("/tasks/all")
    public Result<PageResult<MfTask>> getAllTasks(ReqAllTask reqAllTask, ReqPage reqPage) {
        return Result.ok(flowableService.getAllTasks(reqAllTask, reqPage), "查询所有任务列表成功");
    }

    @Operation(summary = "查询历史任务-包括当前正在处理的任务")
    @GetMapping("/tasks/history/{processInstanceId}")
    public Result<List<MfTask>> getHistoryTasks(@Parameter(name = "processInstanceId", description = "流程实例id") @PathVariable String processInstanceId) {
        return Result.ok(flowableService.getProcessTasks(processInstanceId, true), "查询历史任务列表成功");
    }

    @Operation(summary = "查询当前用户任务统计")
    @GetMapping("/tasks/total")
    public Result<TaskTotal> getTaskTotal(ReqTask reqTask) {
        return Result.ok(flowableService.getTaskTotal(reqTask), "查询当前用户任务统计成功");
    }

    @Operation(summary = "审核通过")
    @PostMapping("/tasks/approved")
    public Result<String> approvedTask(@RequestBody ApproveInfo approveInfo) {
        flowableService.completeTask(AuditOperator.审核通过, approveInfo);
        return Result.ok("审核通过");
    }

    @Operation(summary = "审核不通过")
    @PostMapping("/tasks/rejected")
    public Result<String> rejectedTask(@RequestBody ApproveInfo approveInfo) {
        flowableService.completeTask(AuditOperator.审核拒绝, approveInfo);
        return Result.ok("审核不通过");
    }
}