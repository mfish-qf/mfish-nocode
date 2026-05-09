package cn.com.mfish.oauth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 密码哈希工具，使用 MD5 x2 哈希，与原 Shiro SimpleHash 输出完全一致
 * 盐值格式: (userId + salt).getBytes(UTF-8)
 */
public class PasswordHashUtils {

    private static final String ALGORITHM = "MD5";
    private static final int ITERATIONS = 2;

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
    public static String md5Hash(String password, String salt) {
        try {
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            byte[] hash = password.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < ITERATIONS; i++) {
                MessageDigest md = MessageDigest.getInstance(ALGORITHM);
                md.reset();
                md.update(saltBytes);
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
