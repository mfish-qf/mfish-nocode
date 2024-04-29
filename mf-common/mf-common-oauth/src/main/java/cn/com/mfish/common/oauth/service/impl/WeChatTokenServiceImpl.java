package cn.com.mfish.common.oauth.service.impl;

import cn.com.mfish.common.oauth.entity.WeChatToken;
import cn.com.mfish.common.oauth.service.TokenService;
import cn.com.mfish.common.redis.common.RedisPrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.concurrent.TimeUnit;

/**
 * @description: 微信token操作
 * @author: mfish
 * @date: 2023/1/29 22:09
 */
@Service("weChatTokenService")
public class WeChatTokenServiceImpl implements TokenService<WeChatToken> {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

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
        redisTemplate.opsForValue().set(RedisPrefix.buildRefreshTokenKey(token.getRefresh_token()), token, token.getReTokenExpire(), TimeUnit.SECONDS);
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
