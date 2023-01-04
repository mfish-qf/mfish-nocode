package cn.com.mfish.oauth.cache.temp;

import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import cn.com.mfish.oauth.entity.SsoUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * @author: mfish
 * @date: 2020/2/14 18:56
 */
@Component
@Slf4j
public class Account2IdTempCache extends BaseTempCache<String> {
    @Resource
    SsoUserMapper ssoUserMapper;

    @Override
    protected String buildKey(String... key) {
        return RedisPrefix.buildAccount2IdKey(key[0]);
    }

    @Override
    protected String getFromDB(String... key) {
        SsoUser user = ssoUserMapper.getUserByAccount(key[0]);
        if (user == null) {
            log.info(MessageFormat.format("错误:账号{0}未找到对应用户!", key));
            return null;
        }
        return user.getId();
    }

}
