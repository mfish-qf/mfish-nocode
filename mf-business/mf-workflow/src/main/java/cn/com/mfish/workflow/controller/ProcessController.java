package cn.com.mfish.workflow.controller;

import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.*;
import cn.com.mfish.common.workflow.api.req.ReqAllTask;
import cn.com.mfish.common.workflow.api.req.ReqProcess;
import cn.com.mfish.common.workflow.api.req.ReqTask;
import cn.com.mfish.common.workflow.enums.AuditOperator;
import cn.com.mfish.common.workflow.service.FlowableService;
import cn.com.mfish.workflow.common.BpmnConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @description: 工作流
 * @author: mfish
 * @date: 2025/9/10
 */
@RestController
@RequestMapping("/process")
@Tag(name = "工作流接口")
@Slf4j
public class ProcessController {

    private final FlowableService flowableService;

    public ProcessController(FlowableService flowableService) {
        this.flowableService = flowableService;
    }

    @Operation(summary = "手动部署流程")
    @GetMapping("/deploy/{id}")
    public Result<Integer> deployProcess(@Parameter(name = "id", description = "流程定义key") @PathVariable String id) {
        return Result.ok(flowableService.deployProcess(id), "部署成功");
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
    public Result<String> getImage(@Parameter(name = "processInstanceId", description = "流程实例id") @PathVariable("processInstanceId") String processInstanceId) {
        return Result.ok(flowableService.getImage(processInstanceId), "查询流程图片成功");
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

    @Operation(summary = "根据业务key获取最新流程实例任务列表")
    @GetMapping("/tasks/businessKey/{businessKey}")
    public Result<List<MfTask>> getProcessTasksByBusinessKey(@Parameter(name = "businessKey", description = "业务id") @PathVariable String businessKey) {
        return Result.ok(flowableService.getProcessTasksByBusinessKey(businessKey), "查询任务列表成功");
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

    @Operation(summary = "查询我发起任务列表")
    @GetMapping("/tasks/apply")
    public Result<PageResult<MfTask>> getApplyTasks(ReqAllTask reqAllTask, ReqPage reqPage) {
        return Result.ok(flowableService.getApplyTasks(reqAllTask, reqPage), "查询我发起任务列表成功");
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

    @Operation(summary = "构建流程xml")
    @PostMapping("/buildXml")
    public void buildXml(@RequestBody FlowManage flowManage) {
        BpmnModel bpmnModel = BpmnConverter.convertToBpmn(flowManage.getFlowKey(), flowManage.getName(), flowManage.getRemark(), flowManage.getFlowConfig());
        byte[] xml = new BpmnXMLConverter().convertToXML(bpmnModel);
        if (xml == null) {
            return;
        }
        String fileName = (flowManage.getFlowKey() != null ? flowManage.getFlowKey() : "process") + ".bpmn20.xml";
        HttpServletResponse response = ServletUtils.getResponse();
        if (null == response) {
            return;
        }
        response.setContentType("application/xml;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + fileName);
        try (ServletOutputStream servletOutputStream = response.getOutputStream();
             BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(xml))) {
            byte[] buffer = new byte[8096];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                servletOutputStream.write(buffer, 0, count);
            }
            servletOutputStream.flush();
        } catch (IOException e) {
            log.error("下载流程定义xml失败", e);
        }
    }

    @Operation(summary = "查询流程管理信息")
    @GetMapping("/flowManage/{processInstanceId}")
    public Result<FlowManage> queryFlowManage(@Parameter(name = "processInstanceId", description = "流程实例id") @PathVariable String processInstanceId) {
        return Result.ok(flowableService.queryFlowManage(processInstanceId), "查询流程管理信息成功");
    }

    @Operation(summary = "查询当前流程激活的流程定义key")
    @GetMapping("/activeDefinitionKeys/{processInstanceId}")
    public Result<List<String>> getActiveDefinitionKeys(@Parameter(name = "processInstanceId", description = "流程实例id") @PathVariable String processInstanceId) {
        return Result.ok(flowableService.getActiveDefinitionKeys(processInstanceId), "查询流程定义key成功");
    }
}