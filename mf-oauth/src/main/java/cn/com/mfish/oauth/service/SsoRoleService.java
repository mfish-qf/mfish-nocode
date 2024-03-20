package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SsoRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.2.0
 */
public interface SsoRoleService extends IService<SsoRole> {
    Result<SsoRole> insertRole(SsoRole ssoRole);

    Result<SsoRole> updateRole(SsoRole ssoRole);

    Result<Boolean> deleteRole(String id);

    boolean roleCodeExist(String roleId, String tenantId, String roleCode);

    List<String> getRoleMenus(String roleId);

    boolean isTenantRole(String roleId, String tenantId);
}
