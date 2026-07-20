package cn.com.mfish.common.ai.agent;

/**
 * 租户上下文快照
 * <p>
 * 在请求线程捕获认证相关信息，供异步编排线程使用。
 * 解决 AgentRuntime 编排流程切到 boundedElastic 线程后，
 * AuthInfoUtils 无法从 RequestAttributes 获取租户信息的问题。
 * </p>
 * <p>
 * 字段说明：
 * <ul>
 *   <li>tenantId: 租户ID，用于路由 ChatModel 和工具上下文</li>
 *   <li>userId: 用户ID，用于工具上下文和审计</li>
 *   <li>accessToken: 访问令牌，供 Feign/Http 工具回调注入 Authorization header</li>
 *   <li>requestAttributes: Servlet 请求属性，供 BearerTokenInterceptor 恢复上下文</li>
 *   <li>serverWebExchange: WebFlux 交换对象，供异步线程恢复响应式上下文</li>
 * </ul>
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
public class TenantContext {

    private final String tenantId;
    private final String userId;
    private final String accessToken;
    private final Object requestAttributes;
    private final Object serverWebExchange;

    public TenantContext(String tenantId, String userId, String accessToken,
                         Object requestAttributes, Object serverWebExchange) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.accessToken = accessToken;
        this.requestAttributes = requestAttributes;
        this.serverWebExchange = serverWebExchange;
    }

    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public String getAccessToken() { return accessToken; }
    public Object getRequestAttributes() { return requestAttributes; }
    public Object getServerWebExchange() { return serverWebExchange; }
}
