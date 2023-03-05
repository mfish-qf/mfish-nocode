package cn.com.mfish.common.redis.common;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description: 构建ID
 * @author: qiufeng
 * @date: 2023/3/5 14:25
 */
@Slf4j
public class IDBuild {
    private static RedisTemplate<String, Long> redisTemplate;

    static {
        redisTemplate = SpringBeanFactory.getBean("redisTemplate");
    }

    /**
     * 生成序列ID
     *
     * @param prefix ID前缀
     * @return
     */
    public static String getID(String prefix) {
        String id = prefix + new Date().getTime();
        StringBuilder sb = new StringBuilder(id);
        sb.append(getSuffix(id));
        return sb.toString();
    }

    /**
     * 通过redis获取并发后缀编码
     *
     * @param key
     * @return
     */
    private static String getSuffix(String key) {
        StringBuilder seq = new StringBuilder(getSequence(key).toString());
        while (seq.length() < 4) {
            seq.insert(0, "0");
        }
        return seq.toString();
    }

    /**
     * 同一时间点请求序列递增，过期时间30秒
     *
     * @param key
     * @return
     */
    private static Long getSequence(String key) {
        return getSequence(key, 1, 30);
    }

    private static Long getSequence(String key, int increment, long expire) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        if (expire > 0) {
            counter.expire(expire, TimeUnit.SECONDS);
        }
        return counter.getAndAdd(increment);
    }
}
