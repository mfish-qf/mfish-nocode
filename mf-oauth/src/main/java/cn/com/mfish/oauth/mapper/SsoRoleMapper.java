package cn.com.mfish.oauth.mapper;

import cn.com.mfish.common.oauth.entity.SsoRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @version: V2.0.0
 */
public interface SsoRoleMapper extends BaseMapper<SsoRole> {
    int insertRoleMenus(@Param("roleId") String roleId, @Param("menuList") List<String> menuList);

    @Delete("delete from sso_role_menu where role_id=#{roleId}")
    int deleteRoleMenus(@Param("roleId") String roleId);

    int roleCodeExist(@Param("roleId") String roleId, @Param("tenantId") String tenantId, @Param("roleCode") String roleCode);

    /**
     * 获取角色下所有的用户
     *
     * @param roleIds        角色id列表
     * @param containInvalid 是否包含失效用户 true包含 false不包含
     * @return 用户id列表
     */
    List<String> getRoleUsers(@Param("roleIds") List<String> roleIds, @Param("containInvalid") boolean containInvalid);

    @Select("select menu_id from sso_role_menu where role_id=#{roleId}")
    List<String> getRoleMenus(String roleId);

    int isTenantRole(@Param("roleId") String roleId, @Param("tenantId") String tenantId);

    List<String> getRoleIdsByCode(@Param("tenantId") String tenantId, @Param("roleCodes") List<String> roleCodes);
}
