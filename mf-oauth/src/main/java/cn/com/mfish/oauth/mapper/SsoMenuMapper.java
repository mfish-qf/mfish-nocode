package cn.com.mfish.oauth.mapper;

import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.req.ReqSsoMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @date: 2022-09-21
 * @version: V2.0.0
 */
public interface SsoMenuMapper extends BaseMapper<SsoMenu> {
    int insertMenu(SsoMenu ssoMenu);

    Integer queryMaxMenuLevel(@Param("reqSsoMenu") ReqSsoMenu reqSsoMenu, @Param("roleIds") List<String> roleIds);

    List<SsoMenu> queryMenu(@Param("reqSsoMenu") ReqSsoMenu reqSsoMenu, @Param("levels") List<Integer> levels, @Param("roleIds") List<String> roleIds);

    /**
     * 获取按钮权限用户
     *
     * @param menuId 菜单ID
     * @return 用户ID集合
     */
    List<String> queryMenuUser(@Param("menuId") String menuId);

    @Delete("delete from sso_role_menu where menu_id=#{menuId}")
    int deleteMenuRoles(@Param("menuId") String menuId);
}
