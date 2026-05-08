package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @description: 微信openId临时缓存，通过openId获取关联的用户ID
 * @author: mfish
 * @date: 2020/2/29 15:07
 */
@Component
public class OpenIdTempCache extends BaseTempCache<String> {
    @Resource
    SsoUserMapper ssoUserMapper;

    @Override
    protected String buildKey(String... key) {
        return RedisPrefix.buildOpenId2userIdKey(key[0]);
    }

    @Override
    protected String getFromDB(String... key) {
        return ssoUserMapper.getUserIdByOpenId(key[0]);
    }
}
