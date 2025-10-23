package cn.com.mfish.common.oauth.service;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.oauth.api.vo.UserInfoVo;
import cn.com.mfish.common.oauth.entity.OnlineUser;
import cn.com.mfish.common.oauth.entity.SimpleUserInfo;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.oauth.req.ReqSsoUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @author: mfish
 * @date: 2020/2/13 16:50
 */
public interface SsoUserService extends IService<SsoUser> {
    Result<Boolean> changePassword(String userId, String oldPwd, String newPwd);

    Result<SsoUser> insertUser(SsoUser user);

    Result<SsoUser> updateUser(SsoUser user);

    void validateUser(SsoUser user, String operate);

    boolean removeUser(String id);

    SsoUser getUserByAccount(String account);

    SsoUser getUserByGitee(String gitee);

    SsoUser getUserByGithub(String github);

    UserInfo getUserByAccountNoPwd(String account);

    List<String> getUserIdsByAccounts(List<String> accounts);

    List<UserInfo> getUsersByAccounts(List<String> accounts);

    SsoUser getUserById(String userId);

    UserInfo getUserByIdNoPwd(String userId);

    List<UserInfo> getUserList(ReqSsoUser reqSsoUser);

    List<UserRole> getUserRoles(String userId, String tenantId);

    /**
     * 通过用户ID获取按钮权限
     *
     * @param userId   用户id
     * @param tenantId 租户id
     * @return 返回权限集合
     */
    Set<String> getUserPermissions(String userId, String tenantId);

    List<TenantVo> getUserTenants(String userId);

    Result<List<SsoOrg>> getOrgs(String userId, String direction);

    Result<List<String>> getOrgIds(String tenantId, String userId, String direction);

    /**
     * 判断帐号是否存在
     *
     * @param account 帐号
     * @param userId  如果存在userId，排除当前userId
     * @return true存在，false不存在
     */
    boolean isAccountExist(String account, String userId);

    /**
     * 插入用户角色关系
     *
     * @param userId 用户id
     * @param roles  角色id集合
     * @return 插入数量
     */
    int insertUserRole(String userId, List<String> roles);

    /**
     * 插入用户组织关系
     *
     * @param userId  用户id
     * @param orgList 组织id集合
     * @return 返回插入数量
     */
    int insertUserOrg(String userId, List<String> orgList);

    int deleteUserOrg(String userId, String... orgList);

    boolean isExistUserOrg(String userId, String orgId);

    List<SimpleUserInfo> searchUserList(String condition);

    UserInfo getUserInfo(String userId);

    UserInfoVo getUserInfoAndRoles(String userId, String tenantId);

    /**
     * 获取在线用户
     *
     * @return 返回在线用户
     */
    PageResult<OnlineUser> getOnlineUser(ReqPage reqPage);

    /**
     * 解密token
     *
     * @param sid sid
     * @return 返回解密后的sid
     */
    String decryptSid(String sid);

    boolean isPasswordExist(String userId);

    boolean allowChangeAccount(String userId);

    /**
     * 修改账号
     *
     * @param userId  用户id
     * @param account 账号
     * @return 返回修改后的用户
     */
    Result<SsoUser> changeAccount(String userId, String account);

    /**
     * 获取用户安全设置
     *
     * @param userId 用户id
     * @return 返回用户安全设置
     */
    SsoUser getSecureSetting(String userId);

    /**
     * 解绑gitee账号
     *
     * @param userId 用户id
     * @return 返回解绑结果
     */
    Result<Boolean> unbindGitee(String userId);

    /**
     * 绑定gitee账号
     *
     * @param giteeAccount gitee账号
     * @return 返回绑定结果
     */
    Result<Boolean> bindGitee(String giteeAccount);

    /**
     * 解绑github账号
     *
     * @param userId 用户id
     * @return 返回解绑结果
     */
    Result<Boolean> unbindGithub(String userId);

    /**
     * 绑定github账号
     *
     * @param githubAccount github账号
     * @return 返回绑定结果
     */
    Result<Boolean> bindGithub(String githubAccount);
}
