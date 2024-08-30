package cn.com.mfish.common.redis.common;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description: 构建ID
 * @author: mfish
 * @date: 2023/3/5 14:25
 */
@Slf4j
public class IDBuild {
    private static final RedisTemplate<String, Long> redisTemplate;

    static {
        redisTemplate = SpringBeanFactory.getBean("redisTemplate");
    }

    /**
     * 生成序列ID
     *
     * @param prefix ID前缀
     * @return 返回ID
     */
    public static String getID(String prefix) {
        String id = prefix + new Date().getTime();
        return id + getSuffix(id);
    }

    /**
     * 通过redis获取并发后缀编码
     *
     * @param key 序列key
     * @return 返回后缀
     */
    private static String getSuffix(String key) {
        return getSequence(key).toString();
    }

    /**
     * 同一时间点请求序列递增，过期时间30秒
     *
     * @param key 序列key
     * @return 返回序列
     */
    private static Long getSequence(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        counter.expire(30, TimeUnit.SECONDS);
        return counter.getAndAdd(1);
    }
}
