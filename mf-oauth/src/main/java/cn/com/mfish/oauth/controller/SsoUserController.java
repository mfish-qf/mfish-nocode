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
 * @description: 用户信息控制器，提供用户管理、权限查询、登录状态管理等功能
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

    /**
     * 获取用户、权限相关信息
     *
     * @return 用户权限信息
     */
    @Operation(summary = "获取用户、权限相关信息")
    @GetMapping("/info")
    public Result<UserInfoVo> getUserInfo() {
        return Result.ok(ssoUserService.getUserInfoAndRoles(AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId()));
    }

    /**
     * 获取用户权限
     *
     * @param userId   用户 ID，为空时默认当前登录用户
     * @param tenantId 租户 ID
     * @return 用户权限集合
     */
    @Operation(summary = "获取用户权限")
    @GetMapping("/permissions")
    @Parameters({
            @Parameter(name = "userId", description = "用户 ID"),
            @Parameter(name = "tenantId", description = "租户 ID")
    })
    @InnerUser
    public Result<Set<String>> getPermissions(String userId, String tenantId) {
        if (StringUtils.isEmpty(userId)) {
            userId = AuthInfoUtils.getCurrentUserId();
        }
        return Result.ok(ssoUserService.getUserPermissions(userId, tenantId));
    }

    /**
     * 获取用户角色列表
     *
     * @param userId   用户 ID，为空时默认当前登录用户
     * @param tenantId 租户 ID
     * @return 用户角色列表
     */
    @Operation(summary = "获取用户角色")
    @GetMapping("/roles")
    @Parameters({
            @Parameter(name = "userId", description = "用户 ID"),
            @Parameter(name = "tenantId", description = "租户 ID")
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
     * @return 返回当前用户租户列表
     */
    @Operation(summary = "获取当前用户租户列表", description = "获取当前用户租户列表")
    @GetMapping("/tenants")
    public Result<List<TenantVo>> getUserTenants(String userId) {
        if (StringUtils.isEmpty(userId)) {
            userId = AuthInfoUtils.getCurrentUserId();
        }
        return Result.ok(ssoUserService.getUserTenants(userId), "获取当前租户列表成功!");
    }

    /**
     * 获取用户组织树
     *
     * @param userId    用户 ID
     * @param direction 方向：all-返回所有父子节点，up-返回父节点，down-返回子节点
     * @return 用户组织树列表
     */
    @Operation(summary = "获取用户组织树")
    @GetMapping("/orgs/{userId}")
    @Parameters({
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up 返回父节点 down 返回子节点", required = true)
    })
    public Result<List<SsoOrg>> getOrgs(@PathVariable("userId") String userId, @RequestParam String direction) {
        return ssoUserService.getOrgs(userId, direction);
    }

    /**
     * 获取用户组织 ID 列表
     *
     * @param userId    用户 ID
     * @param tenantId  租户 ID
     * @param direction 方向：all-返回所有父子节点，up-返回父节点，down-返回子节点
     * @return 用户组织 ID 列表
     */
    @Operation(summary = "获取用户组织 ID 列表")
    @GetMapping("/orgIds/{userId}")
    @Parameters({
            @Parameter(name = "tenantId", description = "租户 id"),
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up 返回父节点 down 返回子节点", required = true)
    })
    public Result<List<String>> getOrgIds(@PathVariable("userId") String userId, @RequestParam String tenantId, @RequestParam String direction) {
        return ssoUserService.getOrgIds(tenantId, userId, direction);
    }

    /**
     * 通过用户 ID 获取用户信息
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @Operation(summary = "通过用户 ID 获取用户")
    @GetMapping("/{id}")
    public Result<UserInfo> getUserById(@Parameter(name = "id", description = "用户 ID") @PathVariable String id) {
        return Result.ok(ssoUserService.getUserByIdNoPwd(id));
    }

    /**
     * 根据账号获取用户信息
     *
     * @param account 账号名称
     * @return 用户信息
     */
    @Operation(summary = "根据账号获取用户信息")
    @GetMapping("/info/{account}")
    public Result<UserInfo> getUserByAccount(@Parameter(name = "account", description = "帐号名称") @PathVariable String account) {
        return Result.ok(ssoUserService.getUserByAccountNoPwd(account));
    }

    /**
     * 修改密码
     *
     * @param reqChangePwd 修改密码请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改密码")
    @PutMapping("/pwd")
    @Log(title = "修改密码", operateType = OperateType.UPDATE)
    @RequiresPermissions("sys:password:update")
    public Result<Boolean> changePassword(@RequestBody ReqChangePwd reqChangePwd) {
        if (StringUtils.isEmpty(reqChangePwd.getUserId())) {
            Result.fail(false, "错误:用户ID不允许为空");
        }
        return ssoUserService.changePassword(reqChangePwd.getUserId(), reqChangePwd.getOldPwd(), reqChangePwd.getNewPwd());
    }

    /**
     * 设置用户状态
     *
     * @param ssoUser 用户对象，包含 ID 和状态
     * @return 设置结果
     */
    @Log(title = "用户 - 设置状态", operateType = OperateType.UPDATE)
    @Operation(summary = "用户 - 设置状态", description = "用户 - 设置状态")
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

    /**
     * 用户登出
     * <p>该方法只适用于 web 前端登录的用户登出</p>
     *
     * @return 登出结果
     */
    @Operation(summary = "用户登出", description = "用户登出--该方法只适用于 web 前端登录的用户登出")
    @DeleteMapping("/revoke")
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
     * @param reqSsoUser 请求参数
     * @param reqPage    翻页参数
     * @return 返回结果
     */
    @Operation(summary = "用户信息-分页列表查询", description = "用户信息-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:account:query")
    public Result<PageResult<UserInfo>> queryPageList(ReqSsoUser reqSsoUser, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        List<UserInfo> pageList = ssoUserService.getUserList(reqSsoUser);
        return Result.ok(new PageResult<>(pageList), "用户信息-查询成功!");
    }

    /**
     * 检索用户列表（限制最多查询100人，需要租户用户新增权限）
     *
     * @param condition 检索条件，支持用户名、昵称、手机号模糊搜索
     * @return 用户信息列表
     */
    @Operation(summary = "检索用户列表-限制最多查询50人(有新增租户用户权限人允许检索)", description = "检索用户列表")
    @GetMapping("/search")
    @Parameters({
            @Parameter(name = "condition", description = "检索条件，可输入用户名、昵称、手机号")
    })
    @RequiresPermissions("sys:tenantUser:insert")
    public Result<List<UserInfo>> queryUserList(String condition) {
        // 只检索100条
        PageHelper.startPage(1, 100);
        return Result.ok(ssoUserService.getUserList(new ReqSsoUser().setCondition(condition)), "用户信息-检索成功!");
    }

    /**
     * 添加用户
     *
     * @param ssoUser 用户信息对象
     * @return 返回添加结果
     */
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
     * @param ssoUser 请求对象
     * @return 返回结果
     */
    @Log(title = "用户信息-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "用户信息-编辑", description = "用户信息-编辑")
    @PutMapping
    @RequiresPermissions("sys:account:update")
    public Result<SsoUser> edit(@RequestBody SsoUser ssoUser) {
        return ssoUserService.updateUser(ssoUser);
    }

    /**
     * 编辑当前登录用户自己的信息
     *
     * @param ssoUser 用户信息对象
     * @return 返回编辑结果
     */
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
     * @param id id
     * @return 返回结果
     */
    @Log(title = "用户信息-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "用户信息-通过id删除", description = "用户信息-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:account:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (AuthInfoUtils.SUPER_ACCOUNT_ID.equals(id)) {
            return Result.fail(false, "错误:admin帐号不允许删除!");
        }
        if (ssoUserService.removeUser(id)) {
            return Result.ok(true, "用户信息-删除成功!");
        }
        return Result.fail(false, "错误:用户信息-删除失败!");
    }

    /**
     * 判断用户账号是否已存在
     *
     * @param account 用户账号
     * @return 返回账号是否存在
     */
    @Operation(summary = "判断用户是否存在")
    @GetMapping("/exist/{account}")
    public Result<Boolean> isAccountExist(@Parameter(name = "account", description = "帐号名称") @PathVariable String account) {
        if (ssoUserService.isAccountExist(account, null)) {
            return Result.fail(true, "帐号[" + account + "]存在");
        }
        return Result.ok(false, "帐号[" + account + "]不存在");
    }

    /**
     * 获取在线用户列表
     *
     * @param reqPage 分页参数
     * @return 在线用户分页列表
     */
    @Operation(summary = "获取在线用户信息")
    @GetMapping("/online")
    @RequiresPermissions("sys:online:query")
    public Result<PageResult<OnlineUser>> userOnline(ReqPage reqPage) {
        return Result.ok(ssoUserService.getOnlineUser(reqPage), "获取在线用户成功");
    }

    /**
     * 踢出指定在线用户
     *
     * @param sid 加密的用户会话ID
     * @return 返回踢出结果
     */
    @Operation(summary = "踢出指定用户")
    @DeleteMapping("/revoke/{sid}")
    @Log(title = "踢出指定用户", operateType = OperateType.LOGOUT)
    @RequiresPermissions("sys:online:revoke")
    public Result<Boolean> revokeUser(@Parameter(name = "sid", description = "指定用户的sessionId") @PathVariable String sid) {
        userTokenCache.delDeviceTokenCache(ssoUserService.decryptSid(sid));
        return Result.ok(true, "成功登出");
    }

    /**
     * 根据账号获取用户ID列表（内部接口）
     *
     * @param accounts 用户账号，多个以逗号分隔
     * @return 用户ID列表
     */
    @Operation(summary = "根据账号获取用户id(内部接口)")
    @GetMapping("/userId/{accounts}")
    @InnerUser
    public Result<List<String>> getUserIdByAccount(@Parameter(name = "accounts", description = "帐号名称") @PathVariable String accounts) {
        return Result.ok(ssoUserService.getUserIdsByAccounts(List.of(accounts.split(","))), "获取用户id成功");
    }

    /**
     * 根据账号获取简单用户信息列表（内部接口）
     *
     * @param accounts 用户账号，多个以逗号分隔
     * @return 用户信息列表
     */
    @Operation(summary = "根据账号获取简单用户信息(内部接口)")
    @GetMapping("/users/{accounts}")
    @InnerUser
    public Result<List<UserInfo>> getUsersByAccount(@Parameter(name = "accounts", description = "帐号名称") @PathVariable String accounts) {
        return Result.ok(ssoUserService.getUsersByAccounts(List.of(accounts.split(","))), "获取用户信息成功");
    }

    /**
     * 判断用户是否已设置过密码
     *
     * @param userId 用户ID
     * @return 返回是否已设置密码
     */
    @Operation(summary = "判断账号是否设置过密码")
    @GetMapping("/pwdExist/{userId}")
    public Result<Boolean> isPasswordExist(@PathVariable("userId") String userId) {
        return Result.ok(ssoUserService.isPasswordExist(userId));
    }

    /**
     * 判断是否允许修改用户账号
     *
     * @param userId 用户ID
     * @return 返回是否允许修改
     */
    @Operation(summary = "是否允许修改账号")
    @GetMapping("/allowChangeAccount/{userId}")
    public Result<Boolean> allowChangeAccount(@PathVariable("userId") String userId) {
        return Result.ok(ssoUserService.allowChangeAccount(userId));
    }

    /**
     * 修改用户账号
     *
     * @param ssoUser 用户对象（包含用户ID和新账号）
     * @return 返回修改结果
     */
    @Operation(summary = "修改账号")
    @PutMapping("/changeAccount")
    @Log(title = "修改账号", operateType = OperateType.UPDATE)
    public Result<SsoUser> changeAccount(@RequestBody SsoUser ssoUser) {
        return ssoUserService.changeAccount(ssoUser.getId(), ssoUser.getAccount());
    }

    /**
     * 获取用户安全设置信息
     *
     * @param userId 用户ID
     * @return 用户安全设置信息
     */
    @Operation(summary = "获取用户安全设置")
    @GetMapping("/secureSetting/{userId}")
    public Result<SsoUser> getSecureSetting(@PathVariable("userId") String userId) {
        return Result.ok(ssoUserService.getSecureSetting(userId));
    }

    /**
     * 解绑Gitee第三方账号
     *
     * @param userId 用户ID
     * @return 返回解绑结果
     */
    @Operation(summary = "解绑gitee账号")
    @PutMapping("/unbind/gitee/{userId}")
    @Log(title = "解绑gitee账号", operateType = OperateType.UPDATE)
    public Result<Boolean> unbindGitee(@PathVariable("userId") String userId) {
        return ssoUserService.unbindGitee(userId);
    }


    /**
     * 解绑GitHub第三方账号
     *
     * @param userId 用户ID
     * @return 返回解绑结果
     */
    @Operation(summary = "解绑github账号")
    @PutMapping("/unbind/github/{userId}")
    @Log(title = "解绑github账号", operateType = OperateType.UPDATE)
    public Result<Boolean> unbindGithub(@PathVariable("userId") String userId) {
        return ssoUserService.unbindGithub(userId);
    }
}