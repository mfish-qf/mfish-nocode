package cn.com.mfish.oauth.cache.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: mfish
 * @date: 2020/2/11 19:18
 */
@Component
@RefreshScope
public class RedisCache implements Cache<String, Object> {
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Value("${redisCache.expire}")
    private long expire = 0L;
    @Value("${redisCache.keyPrefix}")
    private String keyPrefix = "";

    /**
     * 获取当前Key
     *
     * @param k
     * @return
     */
    private String getKey(String k) {
        return this.keyPrefix + ":" + (k == null ? "*" : k);
    }

    @Override
    public Object get(String s) throws CacheException {
        return redisTemplate.opsForValue().get(getKey(s));
    }

    @Override
    public Object put(String s, Object o) throws CacheException {
        if (expire >= 0) {
            redisTemplate.opsForValue().set(getKey(s), o, this.expire * 1000, TimeUnit.MILLISECONDS);
        } else {
            redisTemplate.opsForValue().set(getKey(s), o);
        }
        return o;
    }

    @Override
    public Object remove(String s) throws CacheException {
        Object obj = get(s);
        redisTemplate.delete(getKey(s));
        return obj;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.delete(getKey("*"));
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<String> keys() {
        return redisTemplate.keys(getKey("*"));
    }

    @Override
    public Collection<Object> values() {
        return redisTemplate.opsForValue().multiGet(keys());
    }
}
