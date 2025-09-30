package cn.com.mfish.common.api;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.*;
import cn.com.mfish.common.workflow.api.remote.RemoteWorkflowService;
import cn.com.mfish.common.workflow.api.req.ReqProcess;
import cn.com.mfish.common.workflow.api.req.ReqTask;
import cn.com.mfish.common.workflow.enums.AuditOperator;
import cn.com.mfish.common.workflow.service.FlowableService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 工作流服务接口单实例实现
 * @author: mfish
 * @date: 2025/9/28
 */
@Service("remoteWorkflowService")
public class BootWorkflowService implements RemoteWorkflowService {
    @Resource
    FlowableService flowableService;

    @Override
    public Result<String> deployProcess(String origin, String name) {
        flowableService.deployProcess(name);
        return Result.ok("部署成功");
    }

    @Override
    public Result<PageResult<MfProcess>> getProcessList(String origin, ReqProcess reqProcess, ReqPage reqPage) {
        return Result.ok(flowableService.getProcessList(reqProcess, reqPage), "查询流程实例列表成功");
    }

    @Override
    public Result<PageResult<MfProcess>> getHistoryProcessList(String origin, ReqProcess reqProcess, ReqPage reqPage) {
        return Result.ok(flowableService.getHistoryProcessList(reqProcess, reqPage), "查询历史流程实例列表成功");
    }

    @Override
    public Result<String> startProcess(String origin, FlowableParam<?> flowableParam) {
        return Result.ok(flowableService.startProcess(flowableParam), "启动流程实例成功");
    }

    @Override
    public Result<String> delProcess(String origin, String processInstanceId, String reason) {
        return flowableService.delProcess(processInstanceId, null, reason);
    }

    @Override
    public Result<String> delProcessByBusinessKey(String origin, String businessKey, String reason) {
        return flowableService.delProcess(null, businessKey, reason);
    }

    @Override
    public Result<String> queryImage(String origin, String processInstanceId) {
        return Result.ok(flowableService.queryImage(processInstanceId), "查询流程实例图片成功");
    }

    @Override
    public Result<List<MfTask>> getProcessTasks(String origin, String processInstanceId) {
        return Result.ok(flowableService.getProcessTasks(processInstanceId), "查询流程实例任务列表成功");
    }

    @Override
    public Result<List<AuditComment>> getAuditComments(String origin, String processInstanceId) {
        return Result.ok(flowableService.getAuditComments(processInstanceId), "查询流程实例审核评论列表成功");
    }

    @Override
    public Result<PageResult<MfTask>> getPendingTasks(String origin, ReqTask reqTask, ReqPage reqPage) {
        return Result.ok(flowableService.getPendingTasks(reqTask, reqPage), "查询待办任务列表成功");
    }

    @Override
    public Result<List<MfTask>> getHistoryTasks(String origin, String processInstanceId) {
        return Result.ok(flowableService.getProcessTasks(processInstanceId, true), "查询历史任务列表成功");
    }

    @Override
    public Result<String> approvedTask(String origin, ApproveInfo approveInfo) {
        flowableService.completeTask(AuditOperator.审核通过, approveInfo);
        return Result.ok("审核通过");
    }

    @Override
    public Result<String> rejectedTask(String origin, ApproveInfo approveInfo) {
        flowableService.completeTask(AuditOperator.审核拒绝, approveInfo);
        return Result.ok("审核不通过");
    }
}
