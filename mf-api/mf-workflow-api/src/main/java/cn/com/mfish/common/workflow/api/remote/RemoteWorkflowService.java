package cn.com.mfish.common.workflow.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.*;
import cn.com.mfish.common.workflow.api.fallback.RemoteWorkflowFallBack;
import cn.com.mfish.common.workflow.api.req.ReqAllTask;
import cn.com.mfish.common.workflow.api.req.ReqProcess;
import cn.com.mfish.common.workflow.api.req.ReqTask;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 工作流服务接口
 * @author: mfish
 * @date: 2025/09/26
 */
@FeignClient(contextId = "remoteWorkflowService", value = ServiceConstants.WORKFLOW_SERVICE, fallbackFactory = RemoteWorkflowFallBack.class)
public interface RemoteWorkflowService {
    @Operation(summary = "手动部署流程")
    @GetMapping("/process/deploy/{name}")
    Result<String> deployProcess(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("name") String name);

    @Operation(summary = "分页查询流程列表")
    @GetMapping("/process")
    Result<PageResult<MfProcess>> getProcessList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqProcess reqProcess, @SpringQueryMap ReqPage reqPage);

    @Operation(summary = "分页查询历史流程列表")
    @GetMapping("/process/history")
    Result<PageResult<MfProcess>> getHistoryProcessList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqProcess reqProcess, @SpringQueryMap ReqPage reqPage);

    @Operation(summary = "启动流程")
    @PostMapping("/process/start")
    Result<String> startProcess(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody FlowableParam<?> flowableParam);

    @Operation(summary = "删除流程实例")
    @DeleteMapping("/process/{processInstanceId}")
    Result<String> delProcess(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("processInstanceId") String processInstanceId, @RequestParam("reason") String reason);

    @Operation(summary = "通过业务key删除流程实例")
    @DeleteMapping("/process/businessKey/{businessKey}")
    Result<String> delProcessByBusinessKey(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("businessKey") String businessKey, @RequestParam("reason") String reason);

    @Operation(summary = "查询流程图片")
    @GetMapping("/process/image/{processInstanceId}")
    Result<String> queryImage(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("processInstanceId") String processInstanceId);

    @Operation(summary = "查询实例的审批意见")
    @GetMapping("/process/comments/{processInstanceId}")
    Result<List<AuditComment>> getAuditComments(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("processInstanceId") String processInstanceId);

    @Operation(summary = "查询实例的任务进度")
    @GetMapping("/process/tasks/{processInstanceId}")
    Result<List<MfTask>> getProcessTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("processInstanceId") String processInstanceId);

    @Operation(summary = "查询待处理任务列表")
    @GetMapping("/process/tasks/pending")
    Result<PageResult<MfTask>> getPendingTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqTask reqTask, @SpringQueryMap ReqPage reqPage);

    @Operation(summary = "查询所有任务列表-包括待处理任务和历史任务")
    @GetMapping("/process/tasks/all")
    Result<PageResult<MfTask>> getAllTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqAllTask reqAllTask, @SpringQueryMap ReqPage reqPage);

    @Operation(summary = "查询我发起任务列表")
    @GetMapping("/process/tasks/apply")
    Result<PageResult<MfTask>> getApplyTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqAllTask reqAllTask, @SpringQueryMap ReqPage reqPage);

    @Operation(summary = "查询历史任务-包括当前正在处理的任务")
    @GetMapping("/process/tasks/history/{processInstanceId}")
    Result<List<MfTask>> getHistoryTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("processInstanceId") String processInstanceId);

    @Operation(summary = "查询当前用户任务统计")
    @GetMapping("/process/tasks/total")
    Result<TaskTotal> getTaskTotal(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqTask reqTask);

    @Operation(summary = "审核通过")
    @PostMapping("/process/tasks/approved")
    Result<String> approvedTask(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody ApproveInfo approveInfo);

    @Operation(summary = "审核不通过")
    @PostMapping("/process/tasks/rejected")
    Result<String> rejectedTask(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody ApproveInfo approveInfo);
}
