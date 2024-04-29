package cn.com.mfish.oauth.config;

import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 注解开启session共享
 *
 * @author: mfish
 * @date: 2020/2/10 15:54
 */
@EnableRedisHttpSession
public class SessionConfig {
}
