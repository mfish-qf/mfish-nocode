package cn.com.mfish.common.oauth.api.remote;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.api.fallback.RemoteUserFallback;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.oauth.api.vo.UserInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * @author: mfish
 * @description: RPC用户服务
 * @date: 2021/12/1
 */
@FeignClient(contextId = "remoteUserService", value = ServiceConstants.OAUTH_SERVICE, fallbackFactory = RemoteUserFallback.class)
public interface RemoteUserService {
    /**
     * 根据token获取用户信息
     *
     * @param token  token
     * @param origin 来源
     * @return 用户信息
     */
    @GetMapping("/user/info")
    Result<UserInfoVo> getUserInfo(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestHeader(Constants.AUTHENTICATION) String token);

    /**
     * 根据id获取用户信息（不包括密码）
     *
     * @param origin 来源
     * @param id     用户id
     * @return 用户信息
     */
    @GetMapping("/user/{id}")
    Result<UserInfo> getUserById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id);

    /**
     * 根据账号获取用户信息（不包括密码）
     *
     * @param origin  来源
     * @param account 账号
     * @return 用户信息
     */
    @GetMapping("/user/info/{account}")
    Result<UserInfo> getUserByAccount(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("account") String account);

    /**
     * 获取用户角色
     *
     * @param origin   来源
     * @param userId   用户id
     * @param tenantId 租户id
     * @return 用户角色列表
     */
    @GetMapping("/user/roles")
    Result<List<UserRole>> getRoles(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);

    /**
     * 获取用户权限
     *
     * @param origin   来源
     * @param userId   用户id
     * @param tenantId 租户id
     * @return 返回权限集合
     */
    @GetMapping("/user/permissions")
    Result<Set<String>> getPermissions(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);

    /**
     * 获取当前租户列表
     *
     * @param origin 来源
     * @param userId 用户id
     * @return 租户列表
     */
    @GetMapping("/user/tenants")
    Result<List<TenantVo>> getTenants(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam("userId") String userId);

    /**
     * 获取用户组织
     *
     * @param origin    来源
     * @param userId    用户id
     * @param direction 查询方向
     * @return 用户组织列表
     */
    @GetMapping("/user/orgs/{userId}")
    Result<List<SsoOrg>> getOrgs(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("userId") String userId, @RequestParam("direction") String direction);

    /**
     * 获取用户相关组织id
     *
     * @param origin    来源
     * @param userId    用户id
     * @param tenantId  租户id
     * @param direction 查询方向
     * @return 组织id列表
     */
    @GetMapping("/user/orgIds/{userId}")
    Result<List<String>> getOrgIds(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("userId") String userId, @RequestParam("tenantId") String tenantId, @RequestParam("direction") String direction);

    /**
     * 根据账号获取用户id
     *
     * @param origin   来源
     * @param accounts 账号 多个逗号分割
     * @return 返回用户id列表
     */
    @GetMapping("/user/userId/{accounts}")
    Result<List<String>> getUserIdsByAccounts(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("accounts") String accounts);

    /**
     * 根据账号获取用户信息
     *
     * @param origin   来源
     * @param accounts 账号 多个逗号分割
     * @return 返回用户信息列表
     */
    @GetMapping("/user/users/{accounts}")
    Result<List<UserInfo>> getUsersByAccount(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("accounts") String accounts);
}