package cn.com.mfish.oauth.service;

import cn.com.mfish.common.oauth.entity.AuthorizationCode;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.oauth.oltu.as.request.OAuthAuthzRequest;
import cn.com.mfish.oauth.oltu.as.request.OAuthTokenRequest;

/**
 * @description: OAuth2服务接口，提供授权码构建、令牌生成和刷新令牌等OAuth2核心功能
 * @author: mfish
 * @date: 2020/2/13 12:51
 */
public interface OAuth2Service {
    /**
     * 构建授权码
     *
     * @param request OAuth2授权请求对象
     * @return 授权码对象
     */
    AuthorizationCode buildCode(OAuthAuthzRequest request);

    /**
     * 保存授权码到缓存
     *
     * @param code 授权码对象
     */
    void setCode(AuthorizationCode code);

    /**
     * 删除授权码
     *
     * @param code 授权码值
     */
    void delCode(String code);

    /**
     * 获取授权码信息
     *
     * @param code 授权码值
     * @return 授权码对象
     */
    AuthorizationCode getCode(String code);

    /**
     * 密码模式直接构建访问令牌
     *
     * @param request OAuth2令牌请求对象
     * @return 访问令牌对象
     */
    RedisAccessToken buildToken(OAuthTokenRequest request);

    /**
     * 通过授权码换取访问令牌
     *
     * @param request OAuth2令牌请求对象
     * @param code    授权码对象
     * @return 访问令牌对象
     */
    RedisAccessToken code2Token(OAuthTokenRequest request, AuthorizationCode code);

    /**
     * 通过刷新令牌获取新的访问令牌
     *
     * @param token 原始访问令牌对象
     * @return 新的访问令牌对象
     */
    RedisAccessToken refresh2Token(RedisAccessToken token);

}
