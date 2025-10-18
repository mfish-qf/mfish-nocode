package cn.com.mfish.common.workflow.api.fallback;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.*;
import cn.com.mfish.common.workflow.api.remote.RemoteWorkflowService;
import cn.com.mfish.common.workflow.api.req.ReqAllTask;
import cn.com.mfish.common.workflow.api.req.ReqProcess;
import cn.com.mfish.common.workflow.api.req.ReqTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 工作流接口降级处理
 * @author: mfish
 * @date: 2025/09/26
 */
@Component
@Slf4j
public class RemoteWorkflowFallBack implements FallbackFactory<RemoteWorkflowService> {
    @Override
    public RemoteWorkflowService create(Throwable cause) {
        log.error("错误:工作流接口调用异常", cause);
        return new RemoteWorkflowService() {

            @Override
            public Result<String> deployProcess(String origin, String name) {
                return Result.fail("错误：部署流程失败");
            }

            @Override
            public Result<PageResult<MfProcess>> getProcessList(String origin, ReqProcess reqProcess, ReqPage reqPage) {
                return Result.fail("错误：查询流程列表失败");
            }

            @Override
            public Result<PageResult<MfProcess>> getHistoryProcessList(String origin, ReqProcess reqProcess, ReqPage reqPage) {
                return Result.fail("错误：查询历史流程实例列表失败");
            }

            @Override
            public Result<String> startProcess(String origin, FlowableParam<?> flowableParam) {
                return Result.fail("错误：启动流程失败");
            }

            @Override
            public Result<String> delProcess(String origin, String processInstanceId, String reason) {
                return Result.fail("错误：删除流程失败");
            }

            @Override
            public Result<String> delProcessByBusinessKey(String origin, String businessKey, String reason) {
                return Result.fail("错误：删除流程失败");
            }

            @Override
            public Result<String> queryImage(String origin, String processInstanceId) {
                return Result.fail("错误：查询流程图片失败");
            }

            @Override
            public Result<List<MfTask>> getProcessTasks(String origin, String processInstanceId) {
                return Result.fail("错误：查询流程任务失败");
            }

            @Override
            public Result<List<AuditComment>> getAuditComments(String origin, String processInstanceId) {
                return Result.fail("错误：查询审批评论失败");
            }

            @Override
            public Result<PageResult<MfTask>> getPendingTasks(String origin, ReqTask reqTask, ReqPage reqPage) {
                return Result.fail("错误：查询待办任务失败");
            }

            @Override
            public Result<PageResult<MfTask>> getAllTasks(String origin, ReqAllTask reqAllTask, ReqPage reqPage) {
                return Result.fail("错误：查询所有任务列表失败");
            }

            @Override
            public Result<PageResult<MfTask>> getApplyTasks(String origin, ReqAllTask reqAllTask, ReqPage reqPage) {
                return Result.fail("错误：查询我发起任务列表失败");
            }

            @Override
            public Result<List<MfTask>> getHistoryTasks(String origin, String processInstanceId) {
                return Result.fail("错误：查询历史任务失败");
            }

            @Override
            public Result<TaskTotal> getTaskTotal(String origin, ReqTask reqTask) {
                return Result.fail("错误：查询当前用户任务统计失败");
            }

            @Override
            public Result<String> approvedTask(String origin, ApproveInfo approveInfo) {
                return Result.fail("错误：审批任务失败");
            }

            @Override
            public Result<String> rejectedTask(String origin, ApproveInfo approveInfo) {
                return Result.fail("错误：拒绝任务失败");
            }
        };

    }
}
