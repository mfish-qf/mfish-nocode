package cn.com.mfish.oauth.service;

import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.entity.SsoUser;

/**
 * @author qiufeng
 * @date 2020/2/13 16:50
 */
public interface SsoUserService {
    CheckWithResult<SsoUser> changePassword(String userId, String newPassword);
    CheckWithResult<SsoUser> insert(SsoUser user);
    CheckWithResult<SsoUser> update(SsoUser user);
    SsoUser getUserByAccount(String account);
    SsoUser getUserById(String userId);
    Integer getUserClientExist(String account, String clientId);
}
