package cn.com.mfish.oauth.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @description: 密码助手类，提供密码加密和盐值生成功能
 * @author: mfish
 * @date: 2020/2/13 16:32
 */
@Schema(description = "密码助手，用于新增用户时密码加密")
public class PasswordHelper {
    private static final String ALGORITHM = "MD5";
    private static final int ITERATIONS = 2;

    /**
     * 密码通过 MD5 x2 加密，与原 Shiro SimpleHash 输出一致
     *
     * @param userId   用户id
     * @param password 密码
     * @param salt     盐
     * @return 加密后的密码
     */
    public static String encryptPassword(String userId, String password, String salt) {
        if (StringUtils.isEmpty(password)) {
            return null;
        }
        return md5Hash(password, userId + salt);
    }



    /**
     * 生成随机盐值（替代 Shiro SecureRandomNumberGenerator）
     */
    public static String buildSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * MD5 哈希加密，与 Shiro SimpleHash("MD5", password, ByteSource.Util.bytes(userId + salt), 2).toHex() 输出一致
     *
     * @param password 明文密码
     * @param salt     盐值字符串（应为 userId + salt）
     * @return hex 编码的哈希值
     */
    private static String md5Hash(String password, String salt) {
        try {
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            byte[] hash = password.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            // 第一次迭代：加盐，与 Shiro SimpleHash.hash() 一致
            md.reset();
            md.update(saltBytes);
            hash = md.digest(hash);
            // 后续迭代：不加盐
            for (int i = 1; i < ITERATIONS; i++) {
                md.reset();
                hash = md.digest(hash);
            }
            return toHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
