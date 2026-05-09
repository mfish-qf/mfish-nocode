package cn.com.mfish.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Redis Session 配置，将 HttpSession 存储到 Redis 以支持多实例部署
 */
@Configuration
@EnableRedisHttpSession(redisNamespace = "sso_session", maxInactiveIntervalInSeconds = 50400)
public class RedisSessionConfig {
}
