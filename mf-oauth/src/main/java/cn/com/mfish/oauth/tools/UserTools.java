package cn.com.mfish.oauth.tools;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.service.SsoUserService;
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
    private final SsoUserService ssoUserService;

    @Tool(description = "获取当前用户角色信息")
    public List<UserRole> getUserRoles() {
        return ssoUserService.getUserRoles(userId, tenantId);
    }

    @Tool(description = "获取当前用户组织树")
    public Result<List<SsoOrg>> getOrgs() {
        return ssoUserService.getOrgs(userId, "all");
    }

    @Tool(description = "获取当前用户权限")
    public Result<Set<String>> getPermissions() {
        return Result.ok(ssoUserService.getUserPermissions(userId, tenantId));
    }

}
