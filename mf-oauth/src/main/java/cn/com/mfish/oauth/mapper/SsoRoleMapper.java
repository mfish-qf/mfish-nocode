package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.SsoRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

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

    int roleCodeExist(@Param("roleId") String roleId, @Param("roleCode") String roleCode);
}
