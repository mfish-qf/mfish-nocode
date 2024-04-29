package cn.com.mfish.common.api;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.remote.RemoteRoleService;
import cn.com.mfish.common.oauth.service.SsoRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 角色远程接口的本地实现
 * @author: mfish
 * @date: 2024/4/29
 */
@Service("remoteRoleService")
public class BootRoleService implements RemoteRoleService {
    @Resource
    SsoRoleService ssoRoleService;

    @Override
    public Result<List<String>> getRoleIdsByCode(String origin, String tenantId, String codes) {
        return ssoRoleService.getRoleIdsByCode(tenantId, List.of(codes.split(",")));
    }
}
