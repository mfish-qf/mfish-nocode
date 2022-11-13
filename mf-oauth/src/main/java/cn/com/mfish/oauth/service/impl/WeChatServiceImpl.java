package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.oauth.cache.temp.OpenIdTempCache;
import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.common.RedisPrefix;
import cn.com.mfish.oauth.entity.AccessToken;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.entity.WeChatToken;
import cn.com.mfish.oauth.service.SsoUserService;
import cn.com.mfish.oauth.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author ：qiufeng
 * @description：微信接口服务实现
 * @date ：2021/12/14 9:40
 */
@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {

    @Value("${oauth2.expire.token}")
    private long tokenExpire = 21600;
    @Value("${oauth2.expire.refreshToken}")
    private long reTokenExpire = 604800;

    @Resource
    OpenIdTempCache openIdTempCache;
    @Resource
    SsoUserService ssoUserService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;


    @Override
    public String getUserIdByOpenId(String openId) {
        return openIdTempCache.getCacheInfo(openId);
    }

    @Override
    public boolean bindWeChat(String openId, String userId) {
        SsoUser user = new SsoUser();
        user.setOpenid(openId);
        user.setId(userId);
        CheckWithResult<SsoUser> result = ssoUserService.update(user);
        return result.isSuccess();
    }

    @Override
    public boolean bindWeChat(String openId, String userId, String nickname) {
        return false;
    }

    @Override
    public WeChatToken buildWeChatToken(String openId, String sessionKey, String userId) {
        WeChatToken weChatToken = new WeChatToken();
        weChatToken.setOpenid(openId);
        weChatToken.setSession_key(sessionKey);
        weChatToken.setAccess_token(UUID.randomUUID().toString());
        weChatToken.setRefresh_token(UUID.randomUUID().toString());
        weChatToken.setUserId(userId);
        SsoUser user = ssoUserService.getUserById(userId);
        weChatToken.setAccount(user.getAccount());
        weChatToken.setExpires_in(tokenExpire);
        setToken(weChatToken);
        setRefreshToken(weChatToken);
        return weChatToken;
    }

    @Override
    public AccessToken convertToken(WeChatToken weChatToken) {
        //重新copy屏蔽不像外返回的属性
        return new AccessToken().setAccess_token(weChatToken.getAccess_token())
                .setRefresh_token(weChatToken.getRefresh_token())
                .setExpires_in(weChatToken.getExpires_in());
    }

    @Override
    public void setToken(WeChatToken token) {
        redisTemplate.opsForValue().set(RedisPrefix.buildAccessTokenKey(token.getAccess_token()), token, token.getExpires_in(), TimeUnit.SECONDS);
    }

    @Override
    public void delToken(String token) {
        redisTemplate.delete(RedisPrefix.buildAccessTokenKey(token));
    }

    @Override
    public WeChatToken getToken(String token) {
        return (WeChatToken) redisTemplate.opsForValue().get(RedisPrefix.buildAccessTokenKey(token));
    }

    @Override
    public void setRefreshToken(WeChatToken token) {
        redisTemplate.opsForValue().set(RedisPrefix.buildRefreshTokenKey(token.getRefresh_token()), token, reTokenExpire, TimeUnit.SECONDS);
    }

    @Override
    public void updateRefreshToken(WeChatToken token) {
        String key = RedisPrefix.buildRefreshTokenKey(token.getRefresh_token());
        Long expire = redisTemplate.getExpire(key);
        redisTemplate.opsForValue().set(key, token, expire, TimeUnit.SECONDS);
    }

    @Override
    public WeChatToken getRefreshToken(String token) {
        return (WeChatToken) redisTemplate.opsForValue().get(RedisPrefix.buildRefreshTokenKey(token));
    }

    @Override
    public void delRefreshToken(String token) {
        redisTemplate.delete(RedisPrefix.buildRefreshTokenKey(token));
    }
}