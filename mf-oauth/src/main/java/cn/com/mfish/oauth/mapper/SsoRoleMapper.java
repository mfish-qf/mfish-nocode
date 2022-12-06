package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.SsoRole;
import cn.com.mfish.oauth.req.ReqSsoRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
public interface SsoRoleMapper extends BaseMapper<SsoRole> {
    int insertRoleMenus(@Param("roleId") String roleId, @Param("menuList") List<String> menuList);

    @Delete("delete from sso_role_menu where role_id=#{roleId}")
    int deleteRoleMenus(@Param("roleId") String roleId);

    int roleCodeExist(@Param("clientId") String clientId, @Param("roleId") String roleId, @Param("roleCode") String roleCode);

    List<SsoRole> getRoleList(ReqSsoRole reqSsoRole);

    /**
     * 获取角色下所有的用户
     *
     * @param roleId
     * @return
     */
    @Select("select userId from sso_user_role where role_id=#{roleId}")
    List<String> getRoleUser(String roleId);
}
