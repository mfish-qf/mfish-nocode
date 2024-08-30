package cn.com.mfish.common.core.secret;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * @description: 国密SM4加解密通用类
 * @author: mfish
 * @date: 2023/3/9
 */
@Slf4j
public class SM4Utils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ENCODING = "UTF-8";
    public static final String ALGORITHM_NAME = "SM4";
    // 加密算法/分组加密模式/分组填充方式
    // PKCS5Padding-以8个字节为一组进行分组加密
    // 定义分组加密模式使用：PKCS5Padding
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    // 128-32位16进制；256-64位16进制
    public static final int DEFAULT_KEY_SIZE = 128;

    /**
     * 自动生成密钥（16进制字符串，长度32）
     *
     * @return 返回生成的密钥，以16进制字符串形式表示
     * @throws Exception 如果密钥生成过程中发生错误，则抛出异常
     */
    public static String generateKey() throws Exception {
        return Hex.toHexString(generateKey(DEFAULT_KEY_SIZE));
    }

    /**
     * 根据指定的密钥长度生成密钥
     *
     * @param keySize 密钥长度，以位为单位，决定了生成密钥的安全强度
     * @return 生成的密钥，以字节数组的形式表示
     * @throws Exception 如果密钥生成过程中发生错误，抛出异常
     */
    public static byte[] generateKey(int keySize) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(keySize, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    /**
     * 生成ECB加密算法的Cipher实例
     * ECB模式（电子密码本模式：Electronic codebook）是分组密码算法的一种基本加密模式，
     * 它对每一个数据块独立加密，不依赖于其他数据块。此方法用于初始化和返回一个Cipher实例，
     * 该实例使用指定算法和模式以及给定的密钥进行加密或解密。
     *
     * @param algorithmName 算法名称，如"AES"或"SM4"
     * @param mode          Cipher模式，如Cipher.ENCRYPT_MODE或Cipher.DECRYPT_MODE
     * @param key           加密密钥的字节数组
     * @return 初始化后的Cipher实例
     * @throws Exception 如果算法名称无效或密钥不合法，则抛出异常
     */
    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    /**
     * sm4加密
     * 加密模式：ECB
     *
     * @param hexKey   16进制密钥（忽略大小写）
     * @param paramStr 待加密字符串
     * @return 返回16进制的加密字符串
     * 密文长度不固定，会随着被加密字符串长度的变化而变化
     */
    public static String encryptEcb(String hexKey, String paramStr) {
        try {
            String cipherText = "";
            // 16进制字符串-->byte[]
            org.bouncycastle.util.Arrays.fill(new byte[hexKey.length() / 2], (byte) 0);

            byte[] keyData = Hex.decode(hexKey);
            // String-->byte[]
            byte[] srcData = paramStr.getBytes(ENCODING);
            // 加密后的数组
            byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
            // byte[]-->hexString
            cipherText = Hex.toHexString(cipherArray);
            return cipherText;
        } catch (Exception e) {
            log.error("ECB加密异常" + e.getMessage(), e);
            return paramStr;
        }
    }

    /**
     * 使用电子密码本（ECB）模式进行加密
     * 电子密码本（ECB）模式是最简单的对称加密模式，它独立地加密每一个数据块，不依赖于其他块
     * 这种模式下，相同密钥和明文块总是产生相同的密文块，因此对于固定的数据输入，输出总是相同的
     * 该方法使用指定的算法名称（包含密钥生成器和加密/解密算法）和加密模式来初始化密码器
     *
     * @param key  加密密钥，必须是有效长度的密钥
     * @param data 待加密的数据
     * @return 加密后的字节数组
     * @throws Exception 如果加密过程中出现任何错误，将抛出异常
     */
    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * sm4解密
     * 解密模式：采用ECB
     *
     * @param hexKey     16进制密钥
     * @param cipherText 16进制的加密字符串（忽略大小写）
     * @return 解密后的字符串
     */
    public static String decryptEcb(String hexKey, String cipherText) {
        // 用于接收解密后的字符串
        String decryptStr = "";
        // hexString-->byte[]

        byte[] keyData = Hex.decode(hexKey);
        // hexString-->byte[]
        byte[] cipherData = Hex.decode(cipherText);
        // 解密
        byte[] srcData = new byte[0];
        try {
            srcData = decrypt_Ecb_Padding(keyData, cipherData);
            // byte[]-->String
            decryptStr = new String(srcData, ENCODING);
        } catch (Exception e) {
            log.error("ECB解密异常" + e.getMessage(), e);
        }
        return decryptStr;
    }

    /**
     * 使用ECB模式和填充方式对密文进行解密
     *
     * @param key        密钥，用于解密的密钥必须与加密时使用的密钥完全相同
     * @param cipherText 已加密的密文字节数组
     * @return 解密后的原文字节数组
     * @throws Exception 如果解密过程中发生任何错误，将抛出异常
     */
    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    /**
     * 校验加密前后的字符串是否为同一数据
     *
     * @param hexKey     16进制密钥（忽略大小写）
     * @param cipherText 16进制加密后的字符串
     * @param paramStr   加密前的字符串
     * @return 是否为同一数据
     */
    public static boolean verifyEcb(String hexKey, String cipherText, String paramStr) throws Exception {
        // 用于接收校验结果
        boolean flag = false;
        // hexString-->byte[]
        byte[] keyData = Hex.decode(hexKey);
        // 将16进制字符串转换成数组
        byte[] cipherData = Hex.decode(cipherText);
        // 解密
        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
        // 将原字符串转换成byte[]
        byte[] srcData = paramStr.getBytes(ENCODING);
        // 判断2个数组是否一致
        flag = Arrays.equals(decryptData, srcData);
        return flag;
    }

    public static void main(String[] args) {
        try {
            String value = "测试内容";
            System.out.println("明文：" + value);

            // 生成32位16进制密钥
            String key = generateKey();
            System.out.println("密钥：" + key);

            String cipher = encryptEcb(key, value);
            System.out.println("密文：" + cipher);

            System.out.println("数据是否有效：" + verifyEcb(key, cipher, value));

            String res = decryptEcb(key, cipher);
            System.out.println("解密后数据：" + res);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
