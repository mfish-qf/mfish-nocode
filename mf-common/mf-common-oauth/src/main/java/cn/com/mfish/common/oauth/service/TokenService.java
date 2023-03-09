package cn.com.mfish.common.oauth.service;

/**
 * @author: mfish
 * @date: 2020/2/29
 */
public interface TokenService<T> {

    /**
     * 设置token
     * @param token
     */
    void setToken(T token);

    /**
     * 删除token
     * @param token
     */
    void delToken(String token);

    /**
     * 获取token
     * @param token
     * @return
     */
    T getToken(String token);

    /**
     * 设置refreshToken
     * @param token
     */
    void setRefreshToken(T token);

    /**
     * 更新refreshToken
     * @param token
     */
    void updateRefreshToken(T token);

    /**
     * 获取refreshToken
     * @param token
     * @return
     */
    T getRefreshToken(String token);

    /**
     * 删除refreshToken
     * @param token
     */
    void delRefreshToken(String token);
}
