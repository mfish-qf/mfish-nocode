package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.annotation.InnerUser;
import cn.com.mfish.common.core.enums.DeviceType;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.oauth.api.vo.UserInfoVo;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.common.oauth.entity.*;
import cn.com.mfish.common.oauth.req.ReqSsoUser;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.oauth.cache.redis.UserTokenCache;
import cn.com.mfish.oauth.req.ReqChangePwd;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author: mfish
 * @date: 2020/2/17 18:49
 */
@Tag(name = "用户信息")
@RestController
@RequestMapping("/user")
@Slf4j
public class SsoUserController {

    @Resource
    UserTokenCache userTokenCache;
    @Resource
    SsoUserService ssoUserService;

    @Operation(summary = "获取用户、权限相关信息")
    @GetMapping("/info")
    @Log(title = "获取用户、权限相关信息", operateType = OperateType.QUERY)
    public Result<UserInfoVo> getUserInfo() {
        return Result.ok(ssoUserService.getUserInfoAndRoles(AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId()));
    }

    @Operation(summary = "获取用户权限")
    @GetMapping("/permissions")
    @Log(title = "获取用户权限", operateType = OperateType.QUERY)
    @Parameters({
            @Parameter(name = "userId", description = "用户ID"),
            @Parameter(name = "tenantId", description = "租户ID")
    })
    @InnerUser
    public Result<Set<String>> getPermissions(String userId, String tenantId) {
        if (StringUtils.isEmpty(userId)) {
            userId = AuthInfoUtils.getCurrentUserId();
        }
        return Result.ok(ssoUserService.getUserPermissions(userId, tenantId));
    }

    @Operation(summary = "获取用户角色")
    @GetMapping("/roles")
    @Log(title = "获取用户角色", operateType = OperateType.QUERY)
    @Parameters({
            @Parameter(name = "userId", description = "用户ID"),
            @Parameter(name = "tenantId", description = "租户ID")
    })
    public Result<List<UserRole>> getRoles(String userId, String tenantId) {
        if (StringUtils.isEmpty(userId)) {
            userId = AuthInfoUtils.getCurrentUserId();
        }
        return Result.ok(ssoUserService.getUserRoles(userId, tenantId));
    }

    /**
     * 获取当前用户租户列表
     *
     * @return
     */
    @Operation(summary = "获取当前用户租户列表", description = "获取当前用户租户列表")
    @GetMapping("/tenants")
    public Result<List<TenantVo>> getUserTenants(String userId) {
        if (StringUtils.isEmpty(userId)) {
            userId = AuthInfoUtils.getCurrentUserId();
        }
        return Result.ok(ssoUserService.getUserTenants(userId), "获取当前租户列表成功!");
    }

    @Operation(summary = "获取用户组织树")
    @GetMapping("/orgs/{userId}")
    @Parameters({
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true)
    })
    public Result<List<SsoOrg>> getOrgs(@PathVariable("userId") String userId, @RequestParam String direction) {
        return ssoUserService.getOrgs(userId, direction);
    }

    @Operation(summary = "获取用户组织ID列表")
    @GetMapping("/orgIds/{userId}")
    @Parameters({
            @Parameter(name = "tenantId", description = "租户id"),
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true)
    })
    public Result<List<String>> getOrgIds(@PathVariable("userId") String userId, @RequestParam String tenantId, @RequestParam String direction) {
        return ssoUserService.getOrgIds(tenantId, userId, direction);
    }

    @Operation(summary = "通过用户ID获取用户")
    @GetMapping("/{id}")
    public Result<UserInfo> getUserById(@Parameter(name = "id", description = "用户ID") @PathVariable String id) {
        return Result.ok(ssoUserService.getUserByIdNoPwd(id));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/pwd")
    @Log(title = "修改密码", operateType = OperateType.UPDATE)
    public Result<Boolean> changePassword(@RequestBody ReqChangePwd reqChangePwd) {
        if (StringUtils.isEmpty(reqChangePwd.getUserId())) {
            Result.fail(false, "错误:用户ID不允许为空");
        }
        //除了超户，其他用户修改密码需要传入旧密码
        //超户修改自己密码需要输入旧密码
        if (StringUtils.isEmpty(reqChangePwd.getOldPwd()) && (!AuthInfoUtils.isSuper() || AuthInfoUtils.isSuper(reqChangePwd.getUserId()))) {
            return Result.fail(true, "错误:未输入旧密码");
        }
        return ssoUserService.changePassword(reqChangePwd.getUserId(), reqChangePwd.getOldPwd(), reqChangePwd.getNewPwd());
    }

    @Log(title = "用户-设置状态", operateType = OperateType.UPDATE)
    @Operation(summary = "用户-设置状态", description = "用户-设置状态")
    @PutMapping("/status")
    @RequiresPermissions("sys:account:update")
    public Result<Boolean> setStatus(@RequestBody SsoUser ssoUser) {
        SsoUser newUser = new SsoUser();
        newUser.setStatus(ssoUser.getStatus()).setId(ssoUser.getId());
        Result<SsoUser> result = ssoUserService.updateUser(newUser);
        if (result.isSuccess()) {
            return Result.ok(true, "用户-设置状态成功!");
        }
        return Result.fail(false, "错误:用户-设置状态失败!");
    }

    @Operation(summary = "用户登出", description = "用户登出--该方法只适用于web前端登录的用户登出")
    @GetMapping("/revoke")
    @Log(title = "用户登出", operateType = OperateType.LOGOUT)
    public Result<Boolean> revoke() {
        Subject subject = SecurityUtils.getSubject();
        String userId = null;
        //session中存在userId,优先使用session中的userId
        if (subject != null) {
            userId = (String) subject.getPrincipal();
        }
        //session中不存在userId获取token中的userId
        if (StringUtils.isEmpty(userId)) {
            try {
                userId = AuthInfoUtils.getCurrentUserId();
            } catch (OAuthValidateException e) {
                log.error(e.getMessage());
            }
        } else {
            subject.logout();
        }
        if (StringUtils.isEmpty(userId)) {
            String error = "未获取到用户登录状态，无需登出";
            log.error(error);
            return Result.ok(true, error);
        }
        Object token = OauthUtils.getToken();
        if (token != null) {
            String sId = "";
            if (token instanceof RedisAccessToken redisAccessToken) {
                sId = redisAccessToken.getTokenSessionId();
                userId = redisAccessToken.getUserId();
            } else if (token instanceof WeChatToken weChatToken) {
                sId = weChatToken.getOpenid();
                userId = weChatToken.getUserId();
            }
            userTokenCache.delUserTokenCache(DeviceType.Web, sId, userId);
        }
        return Result.ok(true, "成功登出");
    }

    /**
     * 分页列表查询
     *
     * @param reqSsoUser
     * @param reqPage
     * @return
     */
    @Operation(summary = "用户信息-分页列表查询", description = "用户信息-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:account:query")
    public Result<PageResult<UserInfo>> queryPageList(ReqSsoUser reqSsoUser, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        List<UserInfo> pageList = ssoUserService.getUserList(reqSsoUser);
        return Result.ok(new PageResult<>(pageList), "用户信息-查询成功!");
    }

    @Operation(summary = "检索用户列表-限制最多查询50人(有新增租户用户权限人允许检索)", description = "检索用户列表")
    @GetMapping("/search")
    @Parameters({
            @Parameter(name = "condition", description = "检索条件，可输入用户名、昵称、手机号")
    })
    @RequiresPermissions("sys:tenantUser:insert")
    public Result<List<SimpleUserInfo>> queryUserList(String condition) {
        return Result.ok(ssoUserService.searchUserList(condition), "用户信息-检索成功!");
    }

    @Log(title = "用户信息-添加", operateType = OperateType.INSERT)
    @Operation(summary = "用户信息-添加", description = "用户信息-添加")
    @PostMapping
    @RequiresPermissions(value = {"sys:account:insert", "sys:tenantUser:insert"})
    public Result<SsoUser> add(@RequestBody SsoUser ssoUser) {
        return ssoUserService.insertUser(ssoUser);
    }

    /**
     * 编辑
     *
     * @param ssoUser
     * @return
     */
    @Log(title = "用户信息-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "用户信息-编辑", description = "用户信息-编辑")
    @PutMapping
    @RequiresPermissions("sys:account:update")
    public Result<SsoUser> edit(@RequestBody SsoUser ssoUser) {
        return ssoUserService.updateUser(ssoUser);
    }

    @Log(title = "用户信息-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "用户信息-编辑", description = "用户信息-编辑")
    @PutMapping("/me")
    public Result<SsoUser> editMe(@RequestBody SsoUser ssoUser) {
        if (!ssoUser.getId().equals(AuthInfoUtils.getCurrentUserId())) {
            return Result.fail(ssoUser, "错误:只允许修改自己的信息");
        }
        return ssoUserService.updateUser(ssoUser);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "用户信息-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "用户信息-通过id删除", description = "用户信息-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:account:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if ("1".equals(id)) {
            return Result.fail(false, "错误:admin帐号不允许删除!");
        }
        if (ssoUserService.removeUser(id)) {
            return Result.ok(true, "用户信息-删除成功!");
        }
        return Result.fail(false, "错误:用户信息-删除失败!");
    }

    @Operation(summary = "判断用户是否存在")
    @GetMapping("/exist/{account}")
    public Result<Boolean> isAccountExist(@Parameter(name = "account", description = "帐号名称") @PathVariable String account) {
        if (ssoUserService.isAccountExist(account, null)) {
            return Result.fail(true, "帐号[" + account + "]存在");
        }
        return Result.ok(false, "帐号[" + account + "]不存在");
    }

    @Operation(summary = "获取在线用户信息")
    @GetMapping("/online")
    @RequiresPermissions("sys:online:query")
    public Result<PageResult<OnlineUser>> userOnline(ReqPage reqPage) {
        return Result.ok(ssoUserService.getOnlineUser(reqPage), "获取在线用户成功");
    }

    @Operation(summary = "踢出指定用户")
    @GetMapping("/revoke/{sid}")
    @Log(title = "踢出指定用户", operateType = OperateType.LOGOUT)
    @RequiresPermissions("sys:online:revoke")
    public Result<Boolean> revokeUser(@Parameter(name = "sid", description = "指定用户的sessionId") @PathVariable String sid) {
        userTokenCache.delDeviceTokenCache(ssoUserService.decryptSid(sid));
        return Result.ok(true, "成功登出");
    }

    @Operation(summary = "根据账号获取用户id(内部接口)")
    @GetMapping("/userId/{accounts}")
    @InnerUser
    public Result<List<String>> getUserIdByAccount(@Parameter(name = "accounts", description = "帐号名称") @PathVariable String accounts) {
        return Result.ok(ssoUserService.getUserIdsByAccounts(List.of(accounts.split(","))), "获取用户id成功");
    }
}
