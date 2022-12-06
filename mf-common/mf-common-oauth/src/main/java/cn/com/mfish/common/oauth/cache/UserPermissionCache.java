package cn.com.mfish.common.oauth.cache;

import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.common.redis.temp.BaseTempCache;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author ：qiufeng
 * @description：用户权限缓存
 * @date ：2022/12/6 22:44
 */
@Component("userPermissionCache")
public class UserPermissionCache extends BaseTempCache<Set<String>> {

    /**
     * key [0] userId [1] clientId
     *
     * @param key
     * @return
     */
    @Override
    protected String buildKey(String... key) {
        return RedisPrefix.buildUser2PermissionsKey(key[0], key[1]);
    }

    /**
     * 不查询库直接返回null
     *
     * @param key
     * @return
     */
    @Override
    protected Set<String> getFromDB(String... key) {
        return null;
    }
}