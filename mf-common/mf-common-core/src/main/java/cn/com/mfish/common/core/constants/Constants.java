package cn.com.mfish.common.core.constants;

import org.springframework.http.HttpStatus;

/**
 * 通用常量信息
 *
 * @author: mfish
 * @date: 2021/8/12 11:27
 */
public class Constants {
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
    public static final Integer SUCCESS = HttpStatus.OK.value();

    /**
     * 失败标记
     */
    public static final Integer FAIL = HttpStatus.INTERNAL_SERVER_ERROR.value();

    /**
     * 验证码redisKey标签
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_code_key:";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRE = 5;


}
