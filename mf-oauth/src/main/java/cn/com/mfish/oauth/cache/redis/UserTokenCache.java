package cn.com.mfish.oauth.cache.redis;

import cn.com.mfish.common.core.enums.DeviceType;
import cn.com.mfish.common.oauth.common.LoginMutexEntity;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.common.redis.common.RedisPrefix;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.UnknownSessionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: mfish
 * 用户token缓存信息
 * @date: 2020/2/22 13:13
 */
@Component
@RefreshScope
@Slf4j
public class UserTokenCache {
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    RedisSessionDAO redisSessionDAO;
    //登录是否互斥 默认不互斥
    @Value("${oauth2.login.mutex}")
    private boolean loginMutex = false;
    @Value("${oauth2.expire.token}")
    private long expire = 0L;


    /**
     * 打开互斥：
     * 同一个用户 允许同时登录手机,微信,浏览器
     * 一个用户只允许在一个手机，一个微信，一个浏览器上登录
     * 关闭互斥后：
     * 用户允许多地登录
     *
     * @param deviceType 设备类型
     * @param deviceId   设备ID web端为sessionId app端为mac地址 微信 openid
     * @param userId     用户id
     * @param token      token值
     */
    public void addUserTokenCache(DeviceType deviceType, String deviceId, String userId, String token) {
        //如果不互斥，忽略用户id，直接通过设备id进行token关系缓存
        if (!loginMutex) {
            userId = "";
        }
        //互斥场景下oldDeviceId始终为空，因为并未设置userId与设备的对应关系
        String oldDeviceId = getUserDevice(deviceType, userId);
        if (StringUtils.isEmpty(oldDeviceId)) {
            setUserDevice(deviceType, deviceId, userId, token);
            return;
        }
        //如果是为同一设备，设置token关系
        if (deviceId.equals(oldDeviceId)) {
            setToken(deviceId, token);
            return;
        }
        //如果互斥删除老设备用户信息
        delUserTokenCache(deviceType, oldDeviceId, userId);
        setUserDevice(deviceType, deviceId, userId, token);
    }

    /**
     * 删除用户设备id 同时删除设备下token信息
     *
     * @param deviceType 设备类型
     * @param deviceId   设备id
     * @param userId     用户id
     */
    public void delUserTokenCache(DeviceType deviceType, String deviceId, String userId) {
        //如果不互斥，忽略用户id，直接通过设备id进行token关系缓存
        if (!loginMutex) {
            userId = "";
        }
        delDeviceTokenCache(deviceId);
        //如果当前用户为空不删除用户设备对应关系，因为并未存储
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        redisTemplate.delete(RedisPrefix.buildUser2DeviceKey(userId, deviceType));
    }

    public void delDeviceTokenCache(String deviceId) {
        delTokenList(deviceId);
        try {
            redisSessionDAO.delete(redisSessionDAO.readSession(deviceId));
        } catch (UnknownSessionException ex) {
            log.info("sessionId不存在");
        } catch (Exception ex) {
            log.error("删除session异常", ex);
        }
    }

    /**
     * 设置用户设备以及相关token信息
     * <p>
     * 此方法主要用于更新用户设备信息和与之关联的认证令牌它接受设备类型、设备ID、用户ID和令牌作为参数，
     * 首先调用setUserDevice方法更新或设置用户设备信息，然后调用setToken方法更新该设备的认证令牌
     *
     * @param deviceType 设备类型，指明了用户所使用的设备类型，如移动设备、桌面设备等
     * @param deviceId 设备的唯一标识符，用于区分不同的设备
     * @param userId 用户的唯一标识符，用于区分不同的用户
     * @param token 认证令牌，用于设备在系统中的认证和通信
     */
    private void setUserDevice(DeviceType deviceType, String deviceId, String userId, String token) {
        setUserDevice(deviceType, deviceId, userId);
        setToken(deviceId, token);
    }

    /**
     * 设置当前用户设备
     * 当用户关闭浏览器重新登录会重新设置该信息
     *
     * @param deviceType 设备类型，指明了用户所使用的设备类型，如移动设备、桌面设备等
     * @param deviceId 设备的唯一标识符，用于区分不同的设备
     * @param userId 用户的唯一标识符，用于区分不同的用户
     */
    private void setUserDevice(DeviceType deviceType, String deviceId, String userId) {
        //如果当前用户为空则不设置该属性
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        redisTemplate.opsForValue().set(RedisPrefix.buildUser2DeviceKey(userId, deviceType), deviceId, expire, TimeUnit.SECONDS);
    }

    /**
     * 获取用户设备id
     *
     * @param deviceType 设备类型，指明了用户所使用的设备类型，如移动设备、桌面设备等
     * @param userId 用户的唯一标识符，用于区分不同的用户
     * @return 用户设备id
     */
    private String getUserDevice(DeviceType deviceType, String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return (String) redisTemplate.opsForValue().get(RedisPrefix.buildUser2DeviceKey(userId, deviceType));
    }

    /**
     * 一个设备可能对应多个token  存储token列表信息
     *
     * @param deviceId 设备的唯一标识符，用于区分不同的设备
     * @param token 令牌
     */
    private void setToken(String deviceId, String token) {
        redisTemplate.opsForList().leftPush(RedisPrefix.buildDevice2TokenKey(deviceId), token);
        redisTemplate.expire(RedisPrefix.buildDevice2TokenKey(deviceId), expire, TimeUnit.SECONDS);
    }

    /**
     * 删除token列表
     *
     * @param deviceId 设备的唯一标识符，用于区分不同的设备
     */
    private void delTokenList(String deviceId) {
        List<Object> list = redisTemplate.opsForList().range(RedisPrefix.buildDevice2TokenKey(deviceId), 0, -1);
        if (list == null) {
            return;
        }
        for (Object obj : list) {
            LoginMutexEntity entity = OauthUtils.delTokenAndRefreshToken(obj.toString());
            if (entity != null) {
                log.info(entity.toString());
            }
        }
        redisTemplate.delete(RedisPrefix.buildDevice2TokenKey(deviceId));
    }
}
