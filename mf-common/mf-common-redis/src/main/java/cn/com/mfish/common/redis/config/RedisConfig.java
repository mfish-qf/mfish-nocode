package cn.com.mfish.common.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

/**
 * redis配置
 *
 * @author: mfish
 * @date: 2021/8/12 15:11
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate，用于序列化和反序列化Redis中的数据。
     * 该方法特别定义了如何通过GenericJackson2JsonRedisSerializer来序列化value，
     * 以及通过StringRedisSerializer来序列化key，以适应Redis的数据存储需求。
     *
     * @param redisConnectionFactory 用于建立Redis连接的工厂，由Spring框架注入
     * @return 配置好的RedisTemplate实例，能够处理自定义序列化和反序列化逻辑
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Lazy RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置key的序列化方式，采用StringRedisSerializer
        GenericToStringSerializer<String> keySerializer = new GenericToStringSerializer<>(String.class);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        // 设置value的序列化方式，采用Jackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 处理String类型键值对
     *
     * @param redisConnectionFactory 用于建立Redis连接的工厂，由Spring框架注入
     * @return 配置好的RedisTemplate实例，能够处理自定义序列化和反序列化逻辑
     */
    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(@Lazy RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * redis存储session序列化方式使用GenericJackson2JsonRedisSerializer会造成反序列化失败
     * 单独定义template
     *
     * @param redisConnectionFactory redis连接工厂
     * @return redisTemplate
     */
    @Bean(name = "sessionRedisTemplate")
    public RedisTemplate<String, Object> sessionRedisTemplate(@Lazy RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> sessionRedisTemplate = new RedisTemplate<>();
        sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置key的序列化方式，采用StringRedisSerializer
        GenericToStringSerializer<String> keySerializer = new GenericToStringSerializer<>(String.class);
        sessionRedisTemplate.setKeySerializer(keySerializer);
        sessionRedisTemplate.setHashKeySerializer(keySerializer);
        sessionRedisTemplate.afterPropertiesSet();
        return sessionRedisTemplate;
    }
}
