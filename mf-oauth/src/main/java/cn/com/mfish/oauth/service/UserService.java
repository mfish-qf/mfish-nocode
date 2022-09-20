package cn.com.mfish.oauth.service;

import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.entity.SSOUser;

/**
 * @author qiufeng
 * @date 2020/2/13 16:50
 */
public interface UserService {
    CheckWithResult<SSOUser> changePassword(String userId, String newPassword);
    CheckWithResult<SSOUser> insert(SSOUser user);
    CheckWithResult<SSOUser> update(SSOUser user);
    SSOUser getUserByAccount(String account);
    SSOUser getUserById(String userId);
    Integer getUserClientExist(String account, String clientId);
}
