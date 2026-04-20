package cn.com.mfish.common.api;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.demo.service.DemoLeaveApplyService;
import cn.com.mfish.demo.api.remote.RemoteDemoLeaveApplyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @description: demo请假审批回调单体服务实现
 * @author: mfish
 * @date: 2026/4/18
 */
@Service("remoteDemoLeaveApplyService")
public class BootDemoLeaveApplyService implements RemoteDemoLeaveApplyService {
    @Resource
    private DemoLeaveApplyService demoLeaveApplyService;

    @Override
    public Result<String> approved(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, 1, result);
    }

    @Override
    public Result<String> rejected(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, 2, result);
    }

    @Override
    public Result<String> canceled(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, null, result);
    }
}
