package cn.com.mfish.ai.service.impl;

import cn.com.mfish.common.ai.capability.WorkflowExecutionProvider;
import cn.com.mfish.common.ai.capability.WorkflowResult;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.FlowableParam;
import cn.com.mfish.common.workflow.api.entity.MfTask;
import cn.com.mfish.common.workflow.api.enums.FlowKey;
import cn.com.mfish.common.workflow.api.remote.RemoteWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * {@link WorkflowExecutionProvider} 业务层实现
 * <p>
 * 通过 {@link RemoteWorkflowService} Feign 调用 mf-workflow 服务，
 * 启动 Flowable 流程实例并查询流程状态。
 * </p>
 * <p>
 * <b>AI 启动流程的特点</b>：
 * <ul>
 *     <li>callback 和 prefix 均为空，流程结束时 {@code CompleteCallbackHandler} 跳过业务回调</li>
 *     <li>startAccount 通过 startUserId 传入（若为空由 mf-workflow 端兜底为当前登录用户）</li>
 *     <li>流程变量通过 {@link FlowableParam#setParam(Map)} 传入</li>
 * </ul>
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowExecutionProviderImpl implements WorkflowExecutionProvider {

    private final RemoteWorkflowService remoteWorkflowService;

    @Override
    public WorkflowResult startProcess(String flowKey, String businessKey,
                                       Map<String, Object> variables, String startUserId) {
        WorkflowResult result = new WorkflowResult();
        // 通过 FlowableParam.of 统一创建：流程key、callback/prefix 均取自 FlowKey 枚举
        //  - 枚举配了 callback（如请假流程）：流程结束回调业务系统更新单据状态
        //  - 枚举未配 callback（纯自动化流程）：callback 为空，流程结束自然跳过回调
        FlowableParam<String> param = FlowableParam.of(FlowKey.getByKey(flowKey), businessKey)
                .setStartAccount(startUserId)
                .setParam(variables);

        Result<String> res;
        try {
            res = remoteWorkflowService.startProcess(RPCConstants.AI, param);
        } catch (Exception e) {
            log.error("[WorkflowExecutionProvider] 启动流程失败 flowKey={} businessKey={}", flowKey, businessKey, e);
            return result.setSuccess(false).setMessage("启动流程失败: " + e.getMessage());
        }
        if (res == null || !res.isSuccess()) {
            String msg = res == null ? "null response" : res.getMsg();
            log.warn("[WorkflowExecutionProvider] 启动流程失败 flowKey={} msg={}", flowKey, msg);
            return result.setSuccess(false).setMessage(msg);
        }
        String processInstanceId = res.getData();
        log.info("[WorkflowExecutionProvider] 启动流程成功 flowKey={} businessKey={} pid={}",
                flowKey, businessKey, processInstanceId);
        // 启动后立即查询一次状态，获取当前任务节点
        WorkflowResult status = getProcessStatus(processInstanceId);
        if (status.isSuccess()) {
            // 用查询到的状态覆盖，但保留成功标志
            return status.setSuccess(true);
        }
        // 查询状态失败时，仅返回基础信息
        return result.setSuccess(true)
                .setProcessInstanceId(processInstanceId)
                .setStatus("RUNNING")
                .setMessage("流程已启动，但当前任务节点查询失败");
    }

    @Override
    public WorkflowResult getProcessStatus(String processInstanceId) {
        WorkflowResult result = new WorkflowResult().setProcessInstanceId(processInstanceId);
        Result<List<MfTask>> res;
        try {
            // 使用 history 接口（包含当前正在处理的任务），全面了解流程进度
            res = remoteWorkflowService.getHistoryTasks(RPCConstants.AI, processInstanceId);
        } catch (Exception e) {
            log.error("[WorkflowExecutionProvider] 查询流程状态失败 pid={}", processInstanceId, e);
            return result.setSuccess(false).setMessage("查询流程状态失败: " + e.getMessage());
        }
        if (res == null || !res.isSuccess() || res.getData() == null) {
            String msg = res == null ? "null response" : res.getMsg();
            log.warn("[WorkflowExecutionProvider] 查询流程状态失败 pid={} msg={}", processInstanceId, msg);
            return result.setSuccess(false).setMessage(msg);
        }
        List<MfTask> tasks = res.getData();
        if (tasks.isEmpty()) {
            // 无任务记录：可能是流程实例不存在或已被彻底清除
            return result.setSuccess(true)
                    .setStatus("NOT_FOUND")
                    .setMessage("未找到流程任务记录，流程实例可能不存在");
        }
        // 查找当前活跃任务（endTime 为 null）
        String currentTaskName = null;
        String deleteReason = null;
        boolean hasActiveTask = false;
        for (MfTask task : tasks) {
            if (task.getEndTime() == null) {
                hasActiveTask = true;
                currentTaskName = task.getTaskName();
                break;
            }
            if (task.getDeleteReason() != null && !task.getDeleteReason().isEmpty()) {
                deleteReason = task.getDeleteReason();
            }
        }
        if (hasActiveTask) {
            return result.setSuccess(true)
                    .setStatus("RUNNING")
                    .setCurrentTaskName(currentTaskName)
                    .setMessage("流程进行中，当前任务: " + currentTaskName);
        }
        // 所有任务都已结束
        String status = (deleteReason != null) ? "TERMINATED" : "COMPLETED";
        String message = (deleteReason != null)
                ? "流程已终止: " + deleteReason
                : "流程已完成";
        return result.setSuccess(true)
                .setStatus(status)
                .setMessage(message);
    }
}
