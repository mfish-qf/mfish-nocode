package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.oauth.cache.temp.UserTempCache;
import cn.com.mfish.oauth.service.LoginVerifyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description: 登录验证服务实现类，提供登录重试限制、短信验证码校验和微信会话管理等功能
 * @author: mfish
 * @date: 2026/05/16
 */
@Service
@Slf4j
public class LoginVerifyServiceImpl implements LoginVerifyService {
    @Resource
    SsoUserService ssoUserService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    UserTempCache userTempCache;
    //允许连续出错时间间隔的最大错误数
    final static int ERROR_COUNT = 5;
    //允许连续出错的时间间隔 单位:分钟  30分钟内不允许连续出错5次
    final static long ERROR_TIME_INTERVAL = 30;

    @Override
    public boolean retryLimit(String userId, boolean matches) {
        SsoUser user = ssoUserService.getUserById(userId);
        if (user == null) {
            log.error("{}" + SerConstant.INVALID_USER_ID_DESCRIPTION, userId);
            throw new BadCredentialsException(SerConstant.INVALID_USER_ID_DESCRIPTION);
        }
        //超户不允许禁用、删除
        if (!AuthInfoUtils.isSuper(userId)) {
            if (SerConstant.AccountState.禁用.getValue() == user.getStatus()) {
                log.error("{}" + SerConstant.ACCOUNT_DISABLE_DESCRIPTION, userId);
                throw new BadCredentialsException(SerConstant.ACCOUNT_DISABLE_DESCRIPTION);
            }
            if (user.getDelFlag().equals(1)) {
                log.error("{}" + SerConstant.ACCOUNT_DELETE_DESCRIPTION, userId);
                throw new BadCredentialsException(SerConstant.ACCOUNT_DELETE_DESCRIPTION);
            }
        }
        int count = getLoginCount(userId);
        if (matches) {
            removeLoginCount(userId);
            return true;
        }
        if (count >= ERROR_COUNT) {
            String error = MessageFormat.format("{0}，连续输错{1}次密码，5分钟内禁用登录"
                    , SerConstant.INVALID_USER_SECRET_DESCRIPTION, ERROR_COUNT);
            user.setStatus(SerConstant.AccountState.禁用.getValue());
            userTempCache.updateCacheInfo(user, 5, TimeUnit.MINUTES, user.getId());
            userTempCache.setTimeIncrease(false);
            log.error("{}{}", userId, error);
            throw new LockedException(error);
        }
        String error = MessageFormat.format("{0}，密码错误{1}次，{2}次后禁用登录"
                , SerConstant.INVALID_USER_SECRET_DESCRIPTION, count, ERROR_COUNT);
        log.error("{}{}", userId, error);
        throw new BadCredentialsException(error);
    }

    @Override
    public void delSmsCode(String phone) {
        redisTemplate.delete(RedisPrefix.buildSMSCodeKey(phone));
    }

    @Override
    public String getSmsCode(String phone) {
        return (String) redisTemplate.opsForValue().get(RedisPrefix.buildSMSCodeKey(phone));
    }

    @Override
    public String getOpenIdBySessionKey(String sessionKey) {
        return (String) redisTemplate.opsForValue().get(RedisPrefix.buildSessionKey(sessionKey));
    }

    /**
     * 获取30分钟内登录次数
     *
     * @param userId 用户id
     * @return 次数
     */
    private int getLoginCount(String userId) {
        RedisAtomicLong ral = new RedisAtomicLong(RedisPrefix.buildLoginCountKey(userId)
                , Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        ral.incrementAndGet();
        if (ral.intValue() == 1) {
            ral.expire(ERROR_TIME_INTERVAL, TimeUnit.MINUTES);
        }
        return ral.intValue();
    }

    /**
     * 移除登录次数
     *
     * @param userId 用户id
     */
    private void removeLoginCount(String userId) {
        redisTemplate.delete(RedisPrefix.buildLoginCountKey(userId));
    }
}
