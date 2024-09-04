package cn.com.mfish.common.oauth.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.fallback.RemoteRoleFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: 角色远程接口
 * @author: mfish
 * @date: 2024/4/29
 */
@FeignClient(contextId = "remoteRoleService", value = ServiceConstants.OAUTH_SERVICE, fallbackFactory = RemoteRoleFallback.class)
public interface RemoteRoleService {

    @GetMapping("/role/ids")
    Result<List<String>> getRoleIdsByCode(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam("tenantId") String tenantId, @RequestParam("codes") String codes);

    @GetMapping("/role/users")
    Result<List<String>> getRoleUsers(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam("tenantId") String tenantId, @RequestParam("codes") String codes);
}
