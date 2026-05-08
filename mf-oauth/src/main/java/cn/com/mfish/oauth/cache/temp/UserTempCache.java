package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.redis.common.RedisPrefix;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @description: 用户信息临时缓存，通过用户ID获取用户详细信息
 * @author: mfish
 * @date: 2020/2/14 17:46
 */
@Component
public class UserTempCache extends BaseTempCache<SsoUser> {
    @Resource
    SsoUserMapper ssoUserMapper;

    @Override
    protected String buildKey(String... key) {
        return RedisPrefix.buildUserDetailKey(key[0]);
    }

    @Override
    protected SsoUser getFromDB(String... key) {
        return ssoUserMapper.getUserById(key[0], null);
    }

}
