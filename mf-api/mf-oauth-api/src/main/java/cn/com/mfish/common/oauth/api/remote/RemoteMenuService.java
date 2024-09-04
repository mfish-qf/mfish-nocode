package cn.com.mfish.common.oauth.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.api.fallback.RemoteMenuFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 菜单远程接口
 * @author: mfish
 * @date: 2024/4/22
 */
@FeignClient(contextId = "remoteMenuService", value = ServiceConstants.OAUTH_SERVICE, fallbackFactory = RemoteMenuFallback.class)
public interface RemoteMenuService {
    @PostMapping("/menu")
    Result<SsoMenu> add(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody SsoMenu ssoMenu);

    @GetMapping("/menu/routeExist")
    Result<Boolean> routeExist(@RequestParam("routePath") String routePath, @RequestParam("parentId") String parentId);
}
