package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SsoRole;
import cn.com.mfish.oauth.req.ReqSsoRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.0
 */
public interface SsoRoleService extends IService<SsoRole> {
    Result<SsoRole> insertRole(SsoRole ssoRole);

    Result<SsoRole> updateRole(SsoRole ssoRole);

    boolean deleteRole(String id);

    boolean roleCodeExist(String clientId, String roleId, String roleCode);

    List<SsoRole> getRoleList(ReqSsoRole reqSsoRole);
}
