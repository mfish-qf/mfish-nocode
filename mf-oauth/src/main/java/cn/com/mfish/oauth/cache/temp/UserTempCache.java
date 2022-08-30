package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.oauth.mapper.SSOUserMapper;
import cn.com.mfish.oauth.model.SSOUser;
import cn.com.mfish.oauth.common.RedisPrefix;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/14 17:46
 */
@Component
public class UserTempCache extends BaseTempCache<SSOUser> {
    @Resource
    SSOUserMapper ssoUserMapper;

    @Override
    protected String buildKey(String key) {
        return RedisPrefix.buildUserDetailKey(key);
    }

    @Override
    protected SSOUser getFromDB(String key) {
        return ssoUserMapper.getUserByAccount(key);
    }

}
