package cn.com.mfish.common.core.constants;

/**
 * @author: mfish
 * @description: 凭证常量
 * @date: 2021/11/18 18:16
 */
public class RPCConstants {
    /**
     * 请求来源
     */
    public static final String REQ_ORIGIN = "req-origin";
    /**
     * 内部请求
     */
    public static final String INNER = "inner";
    /**
     * AI工具调用请求（非内部请求，需校验权限）
     */
    public static final String AI = "ai";

    /**
     * 当前请求租户ID
     */
    public static final String REQ_TENANT_ID = "req-tenant-id";

    /**
     * 当前请求用户ID
     */
    public static final String REQ_USER_ID = "req-user-id";

    /**
     * 当前请求帐号名称
     */
    public static final String REQ_ACCOUNT = "req-account";

    /**
     * 验证码校验异常
     */
    public static final String REQ_CHECK_CAPTCHA_EXCEPTION = "req-check-captcha-exception";

    // ===== ToolContext 内部参数 key（用于AI工具上下文传递，不暴露给LLM）=====

    /**
     * ToolContext key：访问令牌（对应 Authorization header）
     */
    public static final String REQ_TOKEN = "token";
    /**
     * ToolContext key：Servlet请求上下文（RequestAttributes）
     */
    public static final String REQ_REQUEST_ATTRIBUTES = "requestAttributes";
    /**
     * ToolContext key：WebFlux请求上下文（ServerWebExchange）
     */
    public static final String REQ_SERVER_WEB_EXCHANGE = "serverWebExchange";
}
