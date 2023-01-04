package cn.com.mfish.common.oauth.service.impl;

import cn.com.mfish.common.oauth.service.TokenService;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: mfish
 * @description: token操作服务
 * @date: 2021/12/9 16:03
 */
@Service("webTokenService")
public class WebTokenServiceImpl implements TokenService<RedisAccessToken> {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setToken(RedisAccessToken token) {
        redisTemplate.opsForValue().set(RedisPrefix.buildAccessTokenKey(token.getAccessToken()), token, token.getExpire(), TimeUnit.SECONDS);
    }

    @Override
    public void delToken(String token) {
        redisTemplate.delete(RedisPrefix.buildAccessTokenKey(token));
    }

    @Override
    public RedisAccessToken getToken(String token) {
        return (RedisAccessToken) redisTemplate.opsForValue().get(RedisPrefix.buildAccessTokenKey(token));
    }

    @Override
    public void setRefreshToken(RedisAccessToken token) {
        redisTemplate.opsForValue().set(RedisPrefix.buildRefreshTokenKey(token.getRefreshToken()), token, token.getReTokenExpire(), TimeUnit.SECONDS);
    }

    @Override
    public void updateRefreshToken(RedisAccessToken token) {
        String key = RedisPrefix.buildRefreshTokenKey(token.getRefreshToken());
        Long expire = redisTemplate.getExpire(key);
        redisTemplate.opsForValue().set(key, token, expire, TimeUnit.SECONDS);
    }

    @Override
    public RedisAccessToken getRefreshToken(String token) {
        return (RedisAccessToken) redisTemplate.opsForValue().get(RedisPrefix.buildRefreshTokenKey(token));
    }

    @Override
    public void delRefreshToken(String token) {
        redisTemplate.delete(RedisPrefix.buildRefreshTokenKey(token));
    }
}
