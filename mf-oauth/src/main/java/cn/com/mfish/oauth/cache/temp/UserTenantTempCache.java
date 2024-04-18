package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @description: 用户租户缓存
 * @author: mfish
 * @date: 2023/6/25
 */
@Component
public class UserTenantTempCache extends BaseTempCache<List<TenantVo>> {
    @Resource
    SsoUserMapper ssoUserMapper;

    @Override
    protected String buildKey(String... key) {
        return RedisPrefix.buildUser2TenantsKey(key[0]);
    }

    @Override
    protected List<TenantVo> getFromDB(String... key) {
        return ssoUserMapper.getUserTenants(key[0]);
    }

}