package cn.com.mfish.common.oauth.cache;

import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.remote.RemoteUserService;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.common.redis.temp.BaseTempCache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author: mfish
 * @description: 用户权限缓存
 * @date: 2022/12/6 22:44
 */
@Component("userPermissionCache")
public class UserPermissionCache extends BaseTempCache<Set<String>> {

    @Resource
    RemoteUserService remoteUserService;

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
        Result<Set<String>> result = remoteUserService.getPermissions(CredentialConstants.INNER, key[0], key[1]);
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }
}