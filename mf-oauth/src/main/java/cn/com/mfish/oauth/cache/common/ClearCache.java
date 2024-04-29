package cn.com.mfish.oauth.cache.common;

import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.oauth.cache.temp.UserPermissionTempCache;
import cn.com.mfish.oauth.cache.temp.UserRoleTempCache;
import cn.com.mfish.oauth.cache.temp.UserTempCache;
import cn.com.mfish.oauth.cache.temp.UserTenantTempCache;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @description: 缓存清理
 * @author: mfish
 * @date: 2023/6/26
 */
@Component
public class ClearCache {

    @Resource
    UserTenantTempCache userTenantTempCache;
    @Resource
    UserRoleTempCache userRoleTempCache;
    @Resource
    UserPermissionTempCache userPermissionTempCache;
    @Resource
    UserTempCache userTempCache;

    /**
     * 移除用户相关缓存
     *
     * @param userId
     */
    public void removeUserCache(String userId) {
        userTempCache.removeOneCache(userId);
        userTenantTempCache.removeOneCache(userId);
        removeUserAuthCache(Collections.singletonList(userId));
    }

    /**
     * 移除用户角色，权限相关缓存
     *
     * @param userIds
     */
    public void removeUserAuthCache(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        List<String> roleKeys = new ArrayList<>();
        List<String> permissionKeys = new ArrayList<>();
        for (String userId : userIds) {
            List<TenantVo> list = userTenantTempCache.getFromCacheAndDB(userId);
            if (list == null) {
                continue;
            }
            //移除通用界面用户角色，权限缓存
            roleKeys.add(RedisPrefix.buildUser2RolesKey(userId, ""));
            for (TenantVo vo : list) {
                roleKeys.add(RedisPrefix.buildUser2RolesKey(userId, vo.getId()));
                permissionKeys.add(RedisPrefix.buildUser2PermissionsKey(userId, vo.getId()));
            }
        }
        userRoleTempCache.removeMoreCache(roleKeys);
        userPermissionTempCache.removeMoreCache(permissionKeys);
    }

}
