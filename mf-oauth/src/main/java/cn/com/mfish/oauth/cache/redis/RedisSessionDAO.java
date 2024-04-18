package cn.com.mfish.oauth.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: mfish
 * @date: 2020/2/10 20:06
 */
@Slf4j
@Component
@RefreshScope
public class RedisSessionDAO extends AbstractSessionDAO {
    @Resource
    private RedisTemplate<String, Object> sessionRedisTemplate;
    @Value("${redisSession.expire}")
    private long expire = 0L;
    @Value("${redisSession.keyPrefix}")
    private String keyPrefix = "";

    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            log.error("创建session为空");
            throw new UnknownSessionException("创建session为空");
        }
        Serializable sessionId = super.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            log.error("读取sessionId为空");
            throw new UnknownSessionException("读取sessionId为空");
        }
        return (SimpleSession) sessionRedisTemplate.opsForValue().get(getKey(sessionId));
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            delete(session);
            return;
        }
        saveSession(session);
    }

    /**
     * 保存session
     *
     * @param session
     */
    private void saveSession(Session session) {
        if (session == null || session.getId() == null) {
            log.error("session或sessionId 为空");
            return;
        }
        String key = getKey(session.getId());
        log.debug("保存session");
        session.setTimeout(this.expire * 1000);
        sessionRedisTemplate.opsForValue().set(key, session, this.expire, TimeUnit.SECONDS);
    }

    /**
     * 获取当前Key
     *
     * @param key
     * @return
     */
    private String getKey(Object key) {
        return this.keyPrefix + ":" + (key == null ? "*" : key);
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            log.error("创建session为空");
            return;
        }
        sessionRedisTemplate.delete(getKey(session.getId()));
    }

    @Override
    public Collection<Session> getActiveSessions() {
        List<Session> result = new ArrayList<>();
        Set<String> keys = sessionRedisTemplate.keys(getKey("*"));
        if (CollectionUtils.isEmpty(keys)) {
            return result;
        }
        List<Object> values = sessionRedisTemplate.opsForValue().multiGet(keys);
        if (CollectionUtils.isEmpty(values)) {
            return result;
        }
        for (Object value : values) {
            result.add((Session) value);
        }
        return result;
    }
}
