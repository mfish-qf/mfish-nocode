package cn.com.mfish.common.oauth.cache;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.remote.RemoteUserService;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: mfish
 * @description: 用户角色缓存
 * @date: 2022/12/6 22:38
 */
@Component("userRoleCache")
public class UserRoleCache extends BaseTempCache<List<UserRole>> {

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
        return RedisPrefix.buildUser2RolesKey(key[0], key[1]);
    }

    /**
     * 只查询缓存不查库
     *
     * @param key
     * @return
     */
    @Override
    protected List<UserRole> getFromDB(String... key) {
        Result<List<UserRole>> result = remoteUserService.getRoles(RPCConstants.INNER, key[0], key[1]);
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }
}