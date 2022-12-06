package cn.com.mfish.oauth.service;

import cn.com.mfish.oauth.entity.SsoMenu;
import cn.com.mfish.oauth.req.ReqSsoMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @Date: 2022-09-21
 * @Version: V1.0
 */
public interface SsoMenuService extends IService<SsoMenu> {
    boolean insertMenu(SsoMenu ssoMenu);

    List<SsoMenu> queryMenu(ReqSsoMenu reqSsoMenu, String userId);

    /**
     * 获取按钮权限用户
     *
     * @param menuId
     * @return
     */
    List<String> queryMenuUser(String menuId);
}
