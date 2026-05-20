package cn.com.mfish.oauth.service;

/**
 * @description: 登录验证服务接口，提供登录重试限制、短信验证码校验和微信会话管理等安全认证辅助功能
 * @author: mfish
 * @date: 2026/05/16
 */
public interface LoginVerifyService {
    /**
     * 登录重试计数
     *
     * @param userId  用户id
     * @param matches 是否验证通过
     * @return 是否
     */
    boolean retryLimit(String userId, boolean matches);

    /**
     * 删除短信验证码
     *
     * @param phone 手机号
     */
    void delSmsCode(String phone);

    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     * @return 返回验证码
     */
    String getSmsCode(String phone);

    /**
     * 通过sessionKey获取openId
     *
     * @param sessionKey sessionKey
     * @return 返回openId
     */
    String getOpenIdBySessionKey(String sessionKey);
}
