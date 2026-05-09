package cn.com.mfish.oauth.security;

/**
 * 登录会话ID持有者，基于ThreadLocal存储每次登录生成的会话UUID
 * 替代 Shiro 的 SecurityUtils.getSubject().getSession().getId()
 */
public class LoginSessionHolder {

    private static final ThreadLocal<String> SESSION_HOLDER = new ThreadLocal<>();

    public static void set(String sessionId) {
        SESSION_HOLDER.set(sessionId);
    }

    public static String get() {
        return SESSION_HOLDER.get();
    }

    public static void clear() {
        SESSION_HOLDER.remove();
    }
}
