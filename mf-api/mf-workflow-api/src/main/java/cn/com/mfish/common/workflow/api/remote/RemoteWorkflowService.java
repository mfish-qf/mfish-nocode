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

    /**
     * 手动部署流程
     *
     * @param origin 来源
     * @param id     流程ID
     * @return 部署版本号
     */
    @GetMapping("/process/deploy/{id}")
    Result<Integer> deployProcess(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String id);

    /**
     * 分页查询流程列表
     *
     * @param origin     来源
     * @param reqProcess 查询条件
     * @param reqPage    分页参数
     * @return 流程分页列表
     */
    @GetMapping("/process")
    Result<PageResult<MfProcess>> getProcessList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqProcess reqProcess, @SpringQueryMap ReqPage reqPage);

    /**
     * 分页查询历史流程列表
     *
     * @param origin     来源
     * @param reqProcess 查询条件
     * @param reqPage    分页参数
     * @return 历史流程分页列表
     */
    @GetMapping("/process/history")
    Result<PageResult<MfProcess>> getHistoryProcessList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqProcess reqProcess, @SpringQueryMap ReqPage reqPage);

    /**
     * 启动流程
     *
     * @param origin        来源
     * @param flowableParam 流程参数
     * @return 流程实例ID
     */
    @PostMapping("/process/start")
    Result<String> startProcess(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody FlowableParam<?> flowableParam);

    /**
     * 删除流程实例
     *
     * @param origin            来源
     * @param processInstanceId 流程实例ID
     * @param reason            删除原因
     * @return 删除结果
     */
    @DeleteMapping("/process/{processInstanceId}")
    Result<String> delProcess(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String processInstanceId, @RequestParam("reason") String reason);

    /**
     * 通过业务key删除流程实例
     *
     * @param origin      来源
     * @param businessKey 业务key
     * @param reason      删除原因
     * @return 删除结果
     */
    @DeleteMapping("/process/businessKey/{businessKey}")
    Result<String> delProcessByBusinessKey(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String businessKey, @RequestParam("reason") String reason);

    /**
     * 查询流程图片
     *
     * @param origin            来源
     * @param processInstanceId 流程实例ID
     * @return 流程图片Base64
     */
    @GetMapping("/process/image/{processInstanceId}")
    Result<String> getImage(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String processInstanceId);

    /**
     * 查询实例的审批意见
     *
     * @param origin            来源
     * @param processInstanceId 流程实例ID
     * @return 审批意见列表
     */
    @GetMapping("/process/comments/{processInstanceId}")
    Result<List<AuditComment>> getAuditComments(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String processInstanceId);

    /**
     * 查询实例的任务进度
     *
     * @param origin            来源
     * @param processInstanceId 流程实例ID
     * @return 任务列表
     */
    @GetMapping("/process/tasks/{processInstanceId}")
    Result<List<MfTask>> getProcessTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String processInstanceId);

    /**
     * 查询待处理任务列表
     *
     * @param origin  来源
     * @param reqTask 查询条件
     * @param reqPage 分页参数
     * @return 待处理任务分页列表
     */
    @GetMapping("/process/tasks/pending")
    Result<PageResult<MfTask>> getPendingTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqTask reqTask, @SpringQueryMap ReqPage reqPage);

    /**
     * 查询所有任务列表（包括待处理任务和历史任务）
     *
     * @param origin     来源
     * @param reqAllTask 查询条件
     * @param reqPage    分页参数
     * @return 任务分页列表
     */
    @GetMapping("/process/tasks/all")
    Result<PageResult<MfTask>> getAllTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqAllTask reqAllTask, @SpringQueryMap ReqPage reqPage);

    /**
     * 查询我发起任务列表
     *
     * @param origin     来源
     * @param reqAllTask 查询条件
     * @param reqPage    分页参数
     * @return 我发起的任务分页列表
     */
    @GetMapping("/process/tasks/apply")
    Result<PageResult<MfTask>> getApplyTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqAllTask reqAllTask, @SpringQueryMap ReqPage reqPage);

    /**
     * 查询历史任务（包括当前正在处理的任务）
     *
     * @param origin            来源
     * @param processInstanceId 流程实例ID
     * @return 历史任务列表
     */
    @GetMapping("/process/tasks/history/{processInstanceId}")
    Result<List<MfTask>> getHistoryTasks(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String processInstanceId);

    /**
     * 查询当前用户任务统计
     *
     * @param origin  来源
     * @param reqTask 查询条件
     * @return 任务统计信息
     */
    @GetMapping("/process/tasks/total")
    Result<TaskTotal> getTaskTotal(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqTask reqTask);

    /**
     * 审核通过
     *
     * @param origin      来源
     * @param approveInfo 审批信息
     * @return 审批结果
     */
    @PostMapping("/process/tasks/approved")
    Result<String> approvedTask(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody ApproveInfo approveInfo);

    /**
     * 审核不通过
     *
     * @param origin      来源
     * @param approveInfo 审批信息
     * @return 审批结果
     */
    @PostMapping("/process/tasks/rejected")
    Result<String> rejectedTask(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody ApproveInfo approveInfo);

    /**
     * 查询流程管理信息
     *
      * @param origin            来源
      * @param processInstanceId 流程实例ID
      * @return 流程管理信息
     */
    @GetMapping("/process/flowManage/{processInstanceId}")
    Result<FlowManage> queryFlowManage(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String processInstanceId);

    /**
     * 查询流程实例当前活动的节点
      * @param origin            来源
      * @param processInstanceId 流程实例ID
      * @return 流程实例当前活动的节点
     */
    @GetMapping("/process/activeDefinitionKeys/{processInstanceId}")
    Result<List<String>> getActiveDefinitionKeys(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable String processInstanceId);
}
