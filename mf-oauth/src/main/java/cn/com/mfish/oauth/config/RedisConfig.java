package cn.com.mfish.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

/**
 * @author: mfish
 * @date: 2020/2/11 18:05
 */
@Configuration
public class RedisConfig {

    /**
     * redis存储session序列化方式使用GenericJackson2JsonRedisSerializer会造成反序列化失败
     * 单独定义template
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "sessionRedisTemplate")
    public RedisTemplate<String, Object> sessionRedisTemplate(@Lazy RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate sessionRedisTemplate = new RedisTemplate();
        sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置key的序列化方式，采用StringRedisSerializer
        GenericToStringSerializer<String> keySerializer = new GenericToStringSerializer<>(String.class);
        sessionRedisTemplate.setKeySerializer(keySerializer);
        sessionRedisTemplate.setHashKeySerializer(keySerializer);
        sessionRedisTemplate.afterPropertiesSet();
        return sessionRedisTemplate;
    }

}
