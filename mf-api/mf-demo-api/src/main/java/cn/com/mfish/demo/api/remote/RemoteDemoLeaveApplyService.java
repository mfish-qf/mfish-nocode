package cn.com.mfish.demo.api.remote;

import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.entity.RemoteAuditApi;
import cn.com.mfish.demo.api.fallback.RemoteDemoLeaveApplyFallBack;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description: demo请假申请审批回调接口
 * @author: mfish
 * @date: 2026/04/18
 */
@FeignClient(contextId = "remoteDemoLeaveApplyService", value = ServiceConstants.DEMO_SERVICE, fallbackFactory = RemoteDemoLeaveApplyFallBack.class)
public interface RemoteDemoLeaveApplyService extends RemoteAuditApi<String> {
}
