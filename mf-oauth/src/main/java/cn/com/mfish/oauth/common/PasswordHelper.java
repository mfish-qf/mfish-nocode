package cn.com.mfish.oauth.common;

import cn.com.mfish.oauth.config.properties.ShiroProperties;
import cn.com.mfish.oauth.security.PasswordHashUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @description: 密码助手类，提供密码加密和盐值生成功能
 * @author: mfish
 * @date: 2020/2/13 16:32
 */
@Schema(description = "密码助手，用于新增用户时密码加密")
@Component
public class PasswordHelper {
    /**
     * 生成hash盐
     *
     * @return 盐值
     */
    public static String buildSalt() {
        return PasswordHashUtils.buildSalt();
    }

    /**
     * 密码通过 MD5 x2 加密，与原 Shiro SimpleHash 输出一致
     *
     * @param userId   用户id
     * @param password 密码
     * @param salt     盐
     * @return 加密后的密码
     */
    public String encryptPassword(String userId, String password, String salt) {
        if (StringUtils.isEmpty(password)) {
            return null;
        }
        return PasswordHashUtils.md5Hash(password, userId + salt);
    }
}
