package cn.com.mfish.common.nocode.api.remote;

import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.entity.RemoteAuditApi;
import cn.com.mfish.common.nocode.api.fallback.RemoteNocodeFallBack;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description: 低代码服务接口
 * @author: mfish
 * @date: 2025/09/26
 */
@FeignClient(contextId = "remoteNocodeService", value = ServiceConstants.NOCODE_SERVICE, fallbackFactory = RemoteNocodeFallBack.class)
public interface RemoteNocodeService extends RemoteAuditApi<String> {
}
