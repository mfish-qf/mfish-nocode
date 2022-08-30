package cn.com.mfish.oauth.common;


/**
 * @author qiufeng
 * @date 2020/2/14 15:46
 */
public class RedisPrefix {
    //交换code 存储登录信息供交换token时判断使用
    public static final String AUTH_CODE = "auth_code:";
    //访问token前缀 存储登录传递信息
    public static final String ACCESS_TOKEN = "access_token:";
    //重置token前缀
    public static final String REFRESH_TOKEN = "refresh_token:";
    //存储用户密码数据 键:用户唯一ID
    public static final String USER2SECRET = "user2secret:";
    //存储账号到ID的对应关系 键:账号 account,email,phone任意一种
    public static final String ACCOUNT2ID = "account2id:";
    //存储用户信息 键:用户唯一ID
    public static final String USER2DETAIL = "user2detail:";
    //请求计数，防止一段时间内重复请求数据库
    public static final String ATOMIC_COUNT = "atomic_count:";
    //一段时间内的登录次数,防止多次重试
    public static final String LOGIN_COUNT = "login_count:";
    //客户端信息存储
    public static final String OAUTH_CLIENT = "oauth_client:";
    //根据用户Id找到设备id  web为session app为认证APP token
    public static final String USER2DEVICE_ID = "user2device_id:";
    //通过设备Id查找相关设备token列表
    public static final String DEVICE2TOKEN = "device2token:";
    //短信验证码
    public static final String SMS_CODE = "sms_code:";
    //短信验证码倒计时 一分钟内部允许防止重复发送
    public static final String SMS_CODE_TIME = "sms_code_time:";
    //通过微信openid获取用户id
    public static final String OPENID2USER_ID = "openid2user_id:";
    //二维码登录二维码
    public static final String QR_CODE = "qrCode:";
    //微信登录获取sessionKey
    public static final String SESSION_KEY = "sessionKey:";

    /**
     * 构建交换code
     *
     * @param code
     * @return
     */
    public static String buildAuthCodeKey(String code) {
        return AUTH_CODE + code;
    }

    /**
     * 构建访问token
     *
     * @param token
     * @return
     */
    public static String buildAccessTokenKey(String token) {
        return ACCESS_TOKEN + token;
    }

    /**
     * 构建刷新token
     *
     * @param token
     * @return
     */
    public static String buildRefreshTokenKey(String token) {
        return REFRESH_TOKEN + token;
    }

    /**
     * 构建用户密码key
     *
     * @param userId 用户唯一ID
     * @return
     */
    public static String buildUserPasswordKey(String userId) {
        return USER2SECRET + userId;
    }

    /**
     * 构建账号到用户ID对应转换key
     *
     * @param account
     * @return
     */
    public static String buildAccount2IdKey(String account) {
        return ACCOUNT2ID + account;
    }

    /**
     * 构建用户详情key
     *
     * @param userId 用户唯一ID
     * @return
     */
    public static String buildUserDetailKey(String userId) {
        return USER2DETAIL + userId;
    }

    /**
     * 在请求KEY增加计数前缀
     *
     * @param key
     * @return
     */
    public static String buildAtomicCountKey(String key) {
        return ATOMIC_COUNT + key;
    }

    /**
     * 构建登录计数键
     *
     * @param userId
     * @return
     */
    public static String buildLoginCountKey(String userId) {
        return LOGIN_COUNT + userId;
    }

    /**
     * 构建客户端键
     *
     * @param clientId 客户端ID
     * @return
     */
    public static String buildClientKey(String clientId) {
        return OAUTH_CLIENT + clientId;
    }

    /**
     * 构建用户查找当前登录设备键
     *
     * @param userId
     * @param deviceType
     * @return
     */
    public static String buildUser2DeviceKey(String userId, SerConstant.DeviceType deviceType) {
        return USER2DEVICE_ID + deviceType.toString() + userId;
    }

    /**
     * 构建设备查找相关token信息键
     *
     * @param deviceId
     * @return
     */
    public static String buildDevice2TokenKey(String deviceId) {
        return DEVICE2TOKEN + deviceId;
    }

    /**
     * 构建短信验证码缓存key
     *
     * @param phone
     * @return
     */
    public static String buildSMSCodeKey(String phone) {
        return SMS_CODE + phone;
    }

    /**
     * 构建短信验证码倒计时key
     *
     * @param phone
     * @return
     */
    public static String buildSMSCodeTimeKey(String phone) {
        return SMS_CODE_TIME + phone;
    }

    /**
     * 构建微信openId查找用户id的key
     *
     * @param openId
     * @return
     */
    public static String buildOpenId2userIdKey(String openId) {
        return OPENID2USER_ID + openId;
    }

    /**
     * 构建二维码key
     * @param code
     * @return
     */
    public static String buildQrCodeKey(String code) {
        return QR_CODE + code;
    }

    /**
     * 构建微信sessionKey临时缓存标签
     *
     * @param sessionKey
     * @return
     */
    public static String buildSessionKey(String sessionKey) {
        return SESSION_KEY + sessionKey;
    }
}
