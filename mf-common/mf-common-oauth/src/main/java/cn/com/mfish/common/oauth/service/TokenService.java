package cn.com.mfish.common.oauth.service;

/**
 * @author: mfish
 * @date: 2020/2/29
 */
public interface TokenService<T> {

    /**
     * 设置token
     *
     * @param token 凭证
     */
    void setToken(T token);

    /**
     * 删除token
     *
     * @param token 凭证
     */
    void delToken(String token);

    /**
     * 获取token
     *
     * @param token 凭证
     * @return 返回token
     */
    T getToken(String token);

    /**
     * 设置refreshToken
     *
     * @param token 凭证
     */
    void setRefreshToken(T token);

    /**
     * 更新refreshToken
     *
     * @param token 凭证
     */
    void updateRefreshToken(T token);

    /**
     * 获取refreshToken
     *
     * @param token 凭证
     * @return 返回refreshToken
     */
    T getRefreshToken(String token);

    /**
     * 删除refreshToken
     *
     * @param token 凭证
     */
    void delRefreshToken(String token);
}
