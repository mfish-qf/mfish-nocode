package cn.com.mfish.common.oauth.common;

/**
 * @author: mfish
 * @date: 2020/2/10 19:32
 */
public class SerConstant {
    public static final String ACCOUNT_DELETE_DESCRIPTION = "登录失败:该账号已删除，请联系管理员";
    public static final String ACCOUNT_DISABLE_DESCRIPTION = "登录失败:该帐号已禁用，请联系管理员";
    public static final String ACCOUNT_LOCK_DESCRIPTION = "登录失败:该帐号已锁定，请联系管理员";
    public static final String INVALID_USER_SECRET_DESCRIPTION = "登录失败:错误的帐号或密码";
    public static final String INVALID_PHONE_CODE_DESCRIPTION = "登录失败:错误的验证码";
    public static final String INVALID_USER_ID_DESCRIPTION = "登录失败:错误的用户ID";
    public static final String INVALID_WX_ID_DESCRIPTION = "登录失败:错误的微信ID";
    public static final String INVALID_NEW_USER_DESCRIPTION = "登录失败:插入新用户失败";
    public static final String REMEMBER_ME = "rememberMe";
    public static final String CLIENT_ID = "client_id";
    public static final String LOGIN_TYPE = "loginType";
    public static final String ERROR_MSG = "errorMsg";
    public static final String QR_CODE = "code";
    public static final String QR_SECRET = "qrSecret";
    public static final String NICKNAME = "nickname";
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";
    public static final String ALL_PERMISSION = "*:*:*";
    public static final String SUPER_ROLE = "superAdmin";
    public static final String SUPER_USER = "admin";
    //微信token前缀
    public static final String WX_PREFIX = "wx-";

    /**
     * 登录类型
     */
    public enum LoginType {
        // 用户名密码登录
        密码登录("user_password", 0),
        // 短信验证码登录
        短信登录("phone_smsCode", 1),
        // 扫码
        扫码登录("qr_code", 2),
        // 同一个session免密登录
        直接登录("direct", 3),
        // 微信确认登录
        微信登录("weChat_recognition", 4),
        // 人脸识别登录
        人脸识别("face_recognition", 5);

        private String loginType;
        private int index;

        LoginType(String type, int index) {
            this.loginType = type;
            this.index = index;
        }

        @Override
        public String toString() {
            return loginType;
        }

        public static LoginType getLoginType(String value) {
            for (LoginType type : LoginType.values()) {
                if (type.toString().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return LoginType.密码登录;
        }

        public static LoginType getLoginType(int index) {
            for (LoginType type : LoginType.values()) {
                if (type.getIndex() == index) {
                    return type;
                }
            }
            return LoginType.密码登录;
        }

        public int getIndex() {
            return index;
        }

    }

    /**
     * 账号状态
     */
    public enum AccountState {
        正常(0),
        禁用(1),
        锁定(2);

        private int value;

        AccountState(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.name();
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 扫码状态
     */
    public enum ScanStatus {
        未扫描("0"),
        已扫描("1"),
        已确认("2"),
        已取消("3");
        private String value;

        ScanStatus(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static ScanStatus getScanState(String value) {
            for (ScanStatus state : ScanStatus.values()) {
                if (state.toString().equalsIgnoreCase(value)) {
                    return state;
                }
            }
            return ScanStatus.未扫描;
        }
    }
}
