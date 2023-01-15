package cn.com.mfish.oauth.common;

import cn.com.mfish.oauth.config.ShiroProperties;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

/**
 * @author: mfish
 * @date: 2020/2/13 16:32
 */
@ApiModel("密码助手，用于新增用户时密码加密")
@Component
public class PasswordHelper {
    /**
     * 生成hash盐
     *
     * @return
     */
    public static String buildSalt() {
        return new SecureRandomNumberGenerator().nextBytes().toHex();
    }

    /**
     * 密码通过shiro配置的加密模式和hash次数进行加密
     *
     * @param userId
     * @param password
     * @param salt
     * @return
     */
    public String encryptPassword(String userId, String password, String salt) {
        if (StringUtils.isEmpty(password)) {
            return null;
        }
        SimpleHash simpleHash = new SimpleHash(
                ShiroProperties.algorithmName,
                password,
                ByteSource.Util.bytes(userId + salt),
                ShiroProperties.hashIterations);
        if (ShiroProperties.hexEncoded) {
            return simpleHash.toHex();
        }
        return simpleHash.toBase64();

    }

//    public static void main(String[] args){
//        System.out.println(buildSalt());
//    }
}
