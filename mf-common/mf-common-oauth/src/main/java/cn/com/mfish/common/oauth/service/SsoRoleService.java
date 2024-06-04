package cn.com.mfish.common.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.SsoRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.3.0
 */
public interface SsoRoleService extends IService<SsoRole> {
    Result<SsoRole> insertRole(SsoRole ssoRole);

    Result<SsoRole> updateRole(SsoRole ssoRole);

    Result<Boolean> deleteRole(String id);

    boolean roleCodeExist(String roleId, String tenantId, String roleCode);

    List<String> getRoleMenus(String roleId);

    boolean isTenantRole(String roleId, String tenantId);

    Result<List<String>> getRoleIdsByCode(String tenantId, List<String> roleCodes);

    /**
     * 通过角色编码获取角色下所有用户
     *
     * @param tenantId 租户id
     * @param roleCodes 角色编码
     * @return 用户id列表
     */
    Result<List<String>> getRoleUsers(String tenantId, List<String> roleCodes);
}
