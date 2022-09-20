package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.oauth.common.RedisPrefix;
import cn.com.mfish.oauth.mapper.ClientMapper;
import cn.com.mfish.oauth.entity.OAuthClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/16 15:54
 */
@Component
public class ClientTempCache extends BaseTempCache<OAuthClient> {
    @Resource
    ClientMapper clientMapper;

    @Override
    protected String buildKey(String key) {
        return RedisPrefix.buildClientKey(key);
    }

    @Override
    protected OAuthClient getFromDB(String key) {
        return clientMapper.getClientById(key);
    }
}
