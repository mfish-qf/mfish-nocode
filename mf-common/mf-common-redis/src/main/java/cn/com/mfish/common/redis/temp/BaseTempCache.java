package cn.com.mfish.common.redis.temp;

import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.common.redis.config.CacheProperties;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: mfish
 * @description: 临时缓存基类
 * @date: 2020/2/14 17:20
 */
@Data
public abstract class BaseTempCache<T> {
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    CacheProperties cacheProperties;

    /**
     * 构建key
     *
     * @param key key
     * @return 返回key
     */
    protected abstract String buildKey(String... key);

    /**
     * 缓存中不存在，从数据库中获取
     *
     * @param key key
     * @return 返回数据库中获取的数据
     */
    protected abstract T getFromDB(String... key);

    /**
     * 获取缓存信息
     *
     * @param param 参数
     * @return 返回缓存数据
     */
    @SuppressWarnings("unchecked")
    public T getFromCache(String... param) {
        String key = buildKey(param);
        T value = (T) redisTemplate.opsForValue().get(key);
        if (value != null) {
            //缓存激活 延长更新缓存时间
            redisTemplate.expire(key, cacheProperties.getTime(), TimeUnit.DAYS);
            return value;
        }
        return null;
    }

    /**
     * 获取缓存信息如果不存在获取数据库中信息并存入缓存
     * redis存在直接返回，redis中不存在访问数据库
     * 5分钟内在请求数据库超过10次，不在访问数据库直接返回null
     * 获取到数据存入到redis缓存中，持久化一周
     * （后期可将时间做成配置）
     *
     * @param param 参数
     * @return 返回数据
     */
    public T getFromCacheAndDB(String... param) {
        String key = buildKey(param);
        T value = getFromCache(param);
        if (value != null) {
            return value;
        }
        RedisAtomicLong ral = new RedisAtomicLong(RedisPrefix.buildAtomicCountKey(key)
                , Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        long inc = ral.getAndIncrement();
        if (inc == 0) {
            ral.expire(5, TimeUnit.MINUTES);
        }
        if (inc >= 10) {
            return null;
        }
        value = getFromDB(param);
        if (value == null) {
            return null;
        }
        redisTemplate.delete(RedisPrefix.buildAtomicCountKey(key));
        setCacheInfo(key, value);
        return value;
    }

    /**
     * 更新缓存
     *
     * @param t      泛型对象
     * @param params 传入键未拼接前缀参数
     */
    public void updateCacheInfo(T t, String... params) {
        setCacheInfo(buildKey(params), t);
    }

    /**
     * 移除缓存
     *
     * @param params 传入键未拼接前缀参数
     */
    public void removeOneCache(String... params) {
        redisTemplate.delete(buildKey(params));
    }

    /**
     * 移除多条缓存
     *
     * @param keys 已拼接前缀的参数
     */
    public void removeMoreCache(List<String> keys) {
        redisTemplate.delete(keys);
    }


    /**
     * 设置缓存
     *
     * @param key   已拼接前缀
     * @param value 缓存数据
     */
    private void setCacheInfo(String key, T value) {
        redisTemplate.opsForValue().set(key, value, cacheProperties.getTime(), TimeUnit.DAYS);
    }

}
