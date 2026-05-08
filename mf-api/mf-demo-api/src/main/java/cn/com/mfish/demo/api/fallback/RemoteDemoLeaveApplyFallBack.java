package cn.com.mfish.demo.api.fallback;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.demo.api.remote.RemoteDemoLeaveApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @description: demo请假申请审批回调接口降级处理
 * @author: mfish
 * @date: 2026/04/18
 */
@Component
@Slf4j
public class RemoteDemoLeaveApplyFallBack implements FallbackFactory<RemoteDemoLeaveApplyService> {
    /**
     * 创建demo请假申请审批服务降级实例
     *
     * @param cause 导致降级的异常原因
     * @return 降级后的请假申请审批服务实例
     */
    @Override
    public RemoteDemoLeaveApplyService create(Throwable cause) {
        log.error("错误: demo请假申请审批回调接口调用异常", cause);
        return new RemoteDemoLeaveApplyService() {
            @Override
            public Result<String> approved(String origin, String prefix, String id, WorkflowCompleteResult result) {
                return Result.fail("错误:请假审批通过回调接口异常");
            }

            @Override
            public Result<String> rejected(String origin, String prefix, String id, WorkflowCompleteResult result) {
                return Result.fail("错误:请假审批拒绝回调接口异常");
            }

            @Override
            public Result<String> canceled(String origin, String prefix, String id, WorkflowCompleteResult result) {
                return Result.fail("错误:请假审批取消回调接口异常");
            }
        };
    }
}
