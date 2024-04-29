package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: mfish
 * @description: 用户权限缓存
 * @date: 2022/12/5 22:06
 */
@Component("userPermissionTempCache")
public class UserPermissionTempCache extends BaseTempCache<Set<String>> {
    @Resource
    SsoUserMapper ssoUserMapper;

    /**
     * key [0] userId [1] tenantId
     *
     * @param key
     * @return
     */
    @Override
    protected String buildKey(String... key) {
        return RedisPrefix.buildUser2PermissionsKey(key[0], key[1]);
    }

    /**
     * key [0] userId [1] tenantId
     *
     * @param key
     * @return
     */
    @Override
    protected Set<String> getFromDB(String... key) {
        Set<String> perSet = new HashSet<>();
        if (AuthInfoUtils.isSuper(key[0])) {
            perSet.add(AuthInfoUtils.ALL_PERMISSION);
        }
        List<String> permissions = ssoUserMapper.getUserPermissions(key[0], key[1]);
        if (permissions == null || permissions.isEmpty()) {
            return perSet;
        }
        for (String per : StringUtils.join(permissions, ",").split(",")) {
            if (StringUtils.isEmpty(per)) {
                continue;
            }
            perSet.add(per.trim());
        }
        return perSet;
    }
}