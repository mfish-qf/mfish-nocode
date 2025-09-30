package cn.com.mfish.workflow.handler;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.entity.RemoteAuditApi;
import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.FlowableParam;
import cn.com.mfish.common.workflow.api.enums.FlowKey;
import cn.com.mfish.common.workflow.common.Constants;
import cn.com.mfish.common.workflow.enums.AuditOperator;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

/**
 * @description: 审核完成回调处理
 * @author: mfish
 * @date: 2025/9/16
 */
@Slf4j
public class CompleteCallbackHandler implements ExecutionListener {
    private final RuntimeService runtimeService = SpringBeanFactory.getBean(RuntimeService.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void notify(DelegateExecution execution) {
        FlowableParam param = (FlowableParam) runtimeService.getVariable(execution.getId(), Constants.WORKFLOW_PARAM);
        AuditOperator auditOperator = AuditOperator.of(execution.getVariable(Constants.AUDIT_TYPE).toString());
        if (param == null || auditOperator == null) {
            log.error("任务{}：任务id{},参数异常", execution.getEventName(), execution.getId());
            throw new MyRuntimeException("错误：回调异常，参数或审批操作为空");
        }
        log.info("{}任务{}：任务id{}", FlowKey.getByKey(param.getKey()).name(), auditOperator.name(), execution.getId());
        String comment = (String) execution.getVariable(Constants.AUDIT_COMMENT);
        RemoteAuditApi remoteAuditApi;
        try {
            remoteAuditApi = (RemoteAuditApi) SpringBeanFactory.getRemoteService(Class.forName(param.getCallback()));
        } catch (ClassNotFoundException e) {
            throw new MyRuntimeException("错误：回调类不存在", e);
        }
        WorkflowCompleteResult result = new WorkflowCompleteResult().setProcessInstanceId(execution.getProcessInstanceId())
                .setComment(comment).setEventName(execution.getEventName());
        Result<String> res;
        switch (auditOperator) {
            case 审核通过 -> res = remoteAuditApi.approved(RPCConstants.INNER, param.getPrefix(), param.getId(), result);
            case 审核拒绝 -> res = remoteAuditApi.rejected(RPCConstants.INNER, param.getPrefix(), param.getId(), result);
            case 取消 -> res = remoteAuditApi.canceled(RPCConstants.INNER, param.getPrefix(), param.getId(), result);
            default -> throw new MyRuntimeException("错误：未知审批操作");
        }
        if (!res.isSuccess()) {
            throw new MyRuntimeException(res.getMsg());
        }
    }
}
