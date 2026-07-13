package cn.com.mfish.common.oauth.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.api.fallback.RemoteMenuFallback;
import cn.com.mfish.common.oauth.api.req.ReqSsoMenu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 菜单远程接口
 * @author: mfish
 * @date: 2024/4/22
 */
@FeignClient(contextId = "remoteMenuService", value = ServiceConstants.OAUTH_SERVICE, fallbackFactory = RemoteMenuFallback.class)
public interface RemoteMenuService {

    /**
     * 查询菜单树
     * @param origin 来源
     * @param reqSsoMenu 查询参数
     * @return 菜单树
     */
    @GetMapping("/menu/tree")
    Result<List<SsoMenu>> queryMenuTree(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqSsoMenu reqSsoMenu);

    /**
     * 新增菜单
     *
     * @param origin  来源
     * @param ssoMenu 菜单信息
     * @return 新增后的菜单信息
     */
    @PostMapping("/menu")
    Result<SsoMenu> add(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody SsoMenu ssoMenu);

    /**
     * 判断路由路径是否已存在
     *
     * @param origin    来源
     * @param routePath 路由路径
     * @param parentId  父菜单ID
     * @return 是否存在
     */
    @GetMapping("/menu/routeExist")
    Result<Boolean> routeExist(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam("routePath") String routePath, @RequestParam("parentId") String parentId);
}
