package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.oauth.cache.UserPermissionCache;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ：qiufeng
 * @description：用户权限缓存
 * @date ：2022/12/5 22:06
 */
@Component("userPermissionTempCache")
public class UserPermissionTempCache extends UserPermissionCache {
    @Resource
    SsoUserMapper ssoUserMapper;

    /**
     * key [0] userId [1] clientId
     * @param key
     * @return
     */
    @Override
    protected Set<String> getFromDB(String... key) {
        Set<String> perSet = new HashSet<>();
        if (OauthUtils.isSuper(key[0])) {
            perSet.add(SerConstant.ALL_PERMISSION);
        }
        String permissions = ssoUserMapper.getUserPermissions(key[0], key[1]);
        if (StringUtils.isEmpty(permissions)) {
            return perSet;
        }
        for (String per : permissions.split(",")) {
            if (StringUtils.isEmpty(per)) {
                continue;
            }
            perSet.add(per.trim());
        }
        return perSet;
    }
}