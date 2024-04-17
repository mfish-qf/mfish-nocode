package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SsoMenu;
import cn.com.mfish.oauth.req.ReqSsoMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @date: 2022-09-21
 * @Version: V1.2.1
 */
public interface SsoMenuService extends IService<SsoMenu> {
    Result<SsoMenu> insertMenu(SsoMenu ssoMenu);

    Result<List<SsoMenu>> queryMenuTree(ReqSsoMenu reqSsoMenu, String userId);

    List<SsoMenu> queryMenu(ReqSsoMenu reqSsoMenu, String userId);

    Result<SsoMenu> updateMenu(SsoMenu ssoMenu);

    Result<Boolean> deleteMenu(String menuId);
}
