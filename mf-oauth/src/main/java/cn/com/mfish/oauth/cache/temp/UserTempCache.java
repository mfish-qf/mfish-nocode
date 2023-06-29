package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.common.redis.common.RedisPrefix;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
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
        return ssoUserMapper.getUserById(key[0],null);
    }

}
