package cn.com.mfish.ai.tools;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.api.remote.RemoteUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.Set;

/**
 * @description: 用户工具
 * @author: mfish
 * @date: 2025/8/23
 */
@Description("用户工具")
@RequiredArgsConstructor
public class UserTools {
    private final String userId;
    private final String tenantId;
    private final RemoteUserService remoteUserService;

    @Tool(description = "获取当前用户角色信息")
    public List<UserRole> getUserRoles() {
        return remoteUserService.getRoles(RPCConstants.INNER, userId, tenantId).getData();
    }

    @Tool(description = "获取当前用户组织树")
    public List<SsoOrg> getOrgs() {
        return remoteUserService.getOrgs(RPCConstants.INNER, userId, "all").getData();
    }

    @Tool(description = "获取当前用户权限")
    public Set<String> getPermissions() {
        return remoteUserService.getPermissions(RPCConstants.INNER, userId, tenantId).getData();
    }

}
