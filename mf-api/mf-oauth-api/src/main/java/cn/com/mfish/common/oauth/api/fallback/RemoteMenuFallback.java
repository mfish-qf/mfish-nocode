package cn.com.mfish.common.oauth.api.fallback;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.api.remote.RemoteMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @description: 菜单远程调用失败处理
 * @author: mfish
 * @date: 2024/4/22
 */
@Slf4j
public class RemoteMenuFallback implements FallbackFactory<RemoteMenuService> {
    @Override
    public RemoteMenuService create(Throwable cause) {
        log.error("菜单服务调用失败:{}", cause.getMessage());
        return new RemoteMenuService() {
            @Override
            public Result<SsoMenu> add(String origin, SsoMenu ssoMenu) {
                return Result.fail("错误:保存菜单失败" + cause.getMessage());
            }

            @Override
            public Result<Boolean> routeExist(String routePath) {
                return Result.fail("错误:路由判断失败" + cause.getMessage());
            }
        };
    }
}
