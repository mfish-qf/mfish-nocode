package cn.com.mfish.common.api;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.api.remote.RemoteMenuService;
import cn.com.mfish.common.oauth.service.SsoMenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @description: 菜单远程接口的本地实现
 * @author: mfish
 * @date: 2024/4/22
 */
@Service("remoteMenuService")
public class BootMenuService implements RemoteMenuService {
    @Resource
    SsoMenuService ssoMenuService;

    @Override
    public Result<SsoMenu> add(String origin, SsoMenu ssoMenu) {
        return ssoMenuService.insertMenu(ssoMenu);
    }

    @Override
    public Result<Boolean> routeExist(String routePath) {
        return ssoMenuService.routeExist(routePath);
    }
}
