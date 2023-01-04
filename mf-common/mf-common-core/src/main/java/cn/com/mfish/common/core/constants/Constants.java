package cn.com.mfish.common.core.constants;

/**
 * 通用常量信息
 *
 * @author: mfish
 * @date: 2021/8/12 11:27
 */
public class Constants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * contentType类型
     */
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * head令牌自定义标识
     */
    public static final String AUTHENTICATION = "Authorization";

    /**
     * 内容长度
     */
    public static final String CONTENT_LENGTH="content-length";

    /**
     * 普通令牌键前缀
     */
    public final static String ACCESS_TOKEN = "access_token";

    /**
     * 令牌来源头
     */
    public static final String HEADER = "header";

    /**
     * head令牌前缀
     */
    public static final String OAUTH_HEADER_NAME = "Bearer ";

    /**
     * 令牌密钥
     */
    public final static String SECRET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 令牌缓存有效期，默认720（分钟）
     */
    public final static long EXPIRES_IN = 720;

    /**
     * 令牌缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = HttpStatus.SUCCESS;

    /**
     * 失败标记
     */
    public static final Integer FAIL = HttpStatus.ERROR;

    /**
     * 验证码redisKey标签
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_code_key:";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRE = 5;


}
