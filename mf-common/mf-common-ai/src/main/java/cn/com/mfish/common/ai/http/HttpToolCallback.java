package cn.com.mfish.common.ai.http;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP工具回调 —— 基于OpenAPI元数据，通过WebClient调用REST接口
 * <p>
 * 与 {@link cn.com.mfish.common.ai.feign.FeignToolCallback} 对应：
 * FeignToolCallback 基于 Java 反射调用 Feign 代理；
 * HttpToolCallback 基于 OpenAPI 文档元数据，通过 WebClient 直接发起 HTTP 请求。
 * </p>
 * <p>
 * 两者共用相同的上下文恢复、内部参数自动填充、Result 解包机制，
 * 区别仅在"执行方式"：FeignToolCallback 调 method.invoke，HttpToolCallback 调 WebClient。
 * </p>
 * <p>
 * 内部参数（不暴露给 LLM）识别规则：
 * <ul>
 *     <li>header 类型，name = req-origin → 内部，自动填充 REQ_ORIGIN</li>
 *     <li>header 类型，name = req-tenant-id → 内部，自动填充 REQ_TENANT_ID</li>
 *     <li>header 类型，name = req-user-id → 内部，自动填充 REQ_USER_ID</li>
 *     <li>header 类型，name = Authorization / access_token → 内部，自动填充 REQ_TOKEN</li>
 * </ul>
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class HttpToolCallback implements ToolCallback {

    /** header 参数中需要自动填充的内部参数名集合 */
    private static final java.util.Set<String> INTERNAL_HEADER_NAMES = java.util.Set.of(
            RPCConstants.REQ_ORIGIN, "origin",
            RPCConstants.REQ_TENANT_ID, "tenantId", "tenant-id",
            RPCConstants.REQ_USER_ID, "userId", "user-id",
            "Authorization", "access_token"
    );

    private final WebClient webClient;
    private final String httpMethod;
    private final String pathTemplate;
    private final List<HttpParamInfo> paramInfos;
    private final ToolDefinition toolDefinition;

    public HttpToolCallback(WebClient webClient, String toolName, String toolDescription,
                            String httpMethod, String pathTemplate,
                            List<HttpParamInfo> paramInfos) {
        this.webClient = webClient;
        this.httpMethod = httpMethod;
        this.pathTemplate = pathTemplate;
        this.paramInfos = paramInfos;
        this.toolDefinition = ToolDefinition.builder()
                .name(toolName)
                .description(toolDescription)
                .inputSchema(generateInputSchema())
                .build();
    }

    /**
     * 为 LLM 可见参数生成 JSON Schema
     * <p>
     * 与 FeignToolCallback 不同，参数的 schema 直接来自 OpenAPI 文档（本身就是 JSON Schema），
     * 无需通过 Java 反射生成。
     * </p>
     */
    private String generateInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");
        JSONObject properties = new JSONObject();
        List<String> required = new ArrayList<>();

        for (HttpParamInfo info : paramInfos) {
            if (info.internal()) continue;
            JSONObject propSchema;
            if (info.schema() != null) {
                propSchema = info.schema();
            } else {
                propSchema = new JSONObject();
                propSchema.put("type", "string");
            }
            if (!info.description().isEmpty()) {
                propSchema.put("description", info.description());
            }
            properties.put(info.name(), propSchema);
            if (info.required()) {
                required.add(info.name());
            }
        }

        schema.put("properties", properties);
        if (!required.isEmpty()) schema.put("required", required);
        return schema.toJSONString();
    }

    @Override
    public @NonNull ToolDefinition getToolDefinition() {
        return toolDefinition;
    }

    @Override
    public @NonNull ToolMetadata getToolMetadata() {
        return ToolMetadata.builder().build();
    }

    @Override
    public @NonNull String call(@NonNull String toolInput) {
        return call(toolInput, null);
    }

    @Override
    public @NonNull String call(@NonNull String toolInput, ToolContext context) {
        // 恢复请求上下文（与 FeignToolCallback 相同逻辑）
        RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
        ServerWebExchange previousExchange = ServletUtils.getExchange();
        RequestAttributes inheritedAttributes = extractFromContext(context, RPCConstants.REQ_REQUEST_ATTRIBUTES, RequestAttributes.class);
        ServerWebExchange inheritedExchange = extractFromContext(context, RPCConstants.REQ_SERVER_WEB_EXCHANGE, ServerWebExchange.class);
        boolean servletInjected = false;
        boolean exchangeInjected = false;
        try {
            if (previousAttributes == null && inheritedAttributes != null) {
                RequestContextHolder.setRequestAttributes(inheritedAttributes);
                servletInjected = true;
            }
            if (previousExchange == null && inheritedExchange != null) {
                ServletUtils.setExchange(inheritedExchange);
                exchangeInjected = true;
            }

            // 解析 LLM 输入
            JSONObject input = StringUtils.isNotEmpty(toolInput) ? JSON.parseObject(toolInput) : new JSONObject();

            // 构造 HTTP 请求并执行
            String responseBody = executeHttpRequest(input, context);
            return convertResult(responseBody);
        } catch (Exception e) {
            // 解包反射异常，取出真正 cause（与 FeignToolCallback 相同逻辑）
            Throwable cause = e;
            while (cause instanceof java.lang.reflect.InvocationTargetException
                    || cause instanceof java.lang.reflect.UndeclaredThrowableException) {
                if (cause.getCause() == null) break;
                cause = cause.getCause();
            }
            String msg = cause.getMessage() != null ? cause.getMessage() : "工具调用异常";
            log.error("[HTTP工具] 调用失败: {} input={}", toolDefinition.name(), toolInput, cause);
            return JSON.toJSONString(Map.of("error", msg));
        } finally {
            if (servletInjected) RequestContextHolder.resetRequestAttributes();
            if (exchangeInjected) ServletUtils.setExchange(previousExchange);
        }
    }

    /**
     * 构造并发送 HTTP 请求
     * <p>
     * 处理顺序：path 参数替换 → query 参数 → header 参数（内部 + 普通）→ body
     * </p>
     */
    private String executeHttpRequest(JSONObject input, ToolContext context) {
        // 1. 替换 path 参数
        String resolvedPath = pathTemplate;
        for (HttpParamInfo info : paramInfos) {
            if ("path".equals(info.location())) {
                Object val = input.get(info.name());
                if (val != null) {
                    resolvedPath = resolvedPath.replace("{" + info.name() + "}", String.valueOf(val));
                }
            }
        }

        // 2. 收集 query 参数
        //    不再手动 URLEncoder.encode，交给 WebClient 的 uriBuilder 统一处理编码。
        //    原实现中先手动编码再 .uri(String) 会触发 WebClient 再次扫描 % 字符并编码为 %25，
        //    导致中文值被双重编码为 %25E5%A4%A7...，后端解码一次后得到的仍是 %E5%A4%A7...。
        Map<String, Object> queryParams = new HashMap<>();
        for (HttpParamInfo info : paramInfos) {
            if ("query".equals(info.location())) {
                Object val = input.get(info.name());
                if (val != null) {
                    queryParams.put(info.name(), val);
                }
            }
        }

        // 3. 构造请求（用 RequestBodySpec 类型保持引用，支持 header + contentType + bodyValue）
        //    使用 uriBuilder Lambda 方式：path 参数已替换好直接传入，query 参数通过 queryParam 传入。
        //    WebClient 会按 URI 规范对 query 参数值做一次正确的 URL 编码（中文 → %E5%A4...），
        //    后端 Servlet 容器会自动解码一次，恢复为中文。
        final String finalResolvedPath = resolvedPath;
        WebClient.RequestBodySpec request = webClient.method(HttpMethod.valueOf(httpMethod.toUpperCase()))
                .uri(uriBuilder -> {
                    uriBuilder.path(finalResolvedPath);
                    for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                        uriBuilder.queryParam(entry.getKey(), entry.getValue());
                    }
                    return uriBuilder.build();
                });

        // 4. header 参数（内部参数自动填充 + 普通参数从 input 取）
        //    OpenAPI 文档显式声明的 Authorization header 会被这里处理；
        //    未声明时由步骤 4.1 兜底注入，确保请求始终携带 token。
        boolean authorizationHeaderSet = false;
        for (HttpParamInfo info : paramInfos) {
            if (!"header".equals(info.location())) continue;
            String headerValue;
            if (info.internal()) {
                headerValue = resolveInternalValue(info.autoFillKey(), context);
            } else {
                Object val = input.get(info.name());
                headerValue = val != null ? String.valueOf(val) : null;
            }
            if (headerValue != null && !headerValue.isEmpty()) {
                request.header(info.headerName(), headerValue);
                // 标记 Authorization 是否已被显式处理（避免步骤 4.1 重复注入）
                if (Constants.AUTHENTICATION.equals(info.headerName())) {
                    authorizationHeaderSet = true;
                }
            }
        }

        // 4.1 无条件兜底注入 Authorization header
        //     大多数 Controller 不会在 @Parameter 中显式声明 Authorization header，
        //     但网关/拦截器需要从 Authorization 头读取 token 进行鉴权。
        //     从 ToolContext 获取 token（由 BaseAssistant.buildToolContext 在请求线程捕获），
        //     若 context 中无 token，兜底从 AuthInfoUtils.getAccessToken() 获取（依赖步骤1恢复的请求上下文）。
        if (!authorizationHeaderSet) {
            String token = resolveToken(context);
            if (token != null && !token.isEmpty()) {
                request.header(Constants.AUTHENTICATION, Constants.OAUTH_HEADER_NAME + token);
            }
        }

        // 5. body 参数
        for (HttpParamInfo info : paramInfos) {
            if ("body".equals(info.location())) {
                Object val = input.get(info.name());
                request.contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(val != null ? JSON.toJSONString(val) : "{}");
                break;
            }
        }

        // 6. 执行请求（同步阻塞，ToolCallback.call 是同步的）
        //     使用 exchangeToMono 而非 retrieve，确保 4xx/5xx 响应也能读取响应体。
        //     后端抛业务异常时，全局异常处理器会返回 Result.fail(msg) 放在响应体，
        //     若用 retrieve() 默认会对 4xx/5xx 抛 WebClientResponseException 丢弃响应体，
        //     导致 LLM 无法获取真实错误信息（如"该用户无此操作权限"）。
        return request.exchangeToMono(response -> response.bodyToMono(String.class)
                        .defaultIfEmpty(""))
                .block();
    }

    /**
     * 解析响应体，复用 FeignToolCallback 的 Result 解包逻辑
     * <p>
     * 响应体为 Result JSON：成功提取 data，失败提取 msg
     * 非 Result 格式：直接返回原始响应
     * </p>
     */
    private String convertResult(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "{\"success\":true,\"data\":null}";
        }
        try {
            JSONObject json = JSON.parseObject(responseBody);
            // Result 格式：{"success":true/false,"code":xxx,"msg":"...","data":...}
            if (json.containsKey("success") && json.containsKey("code")) {
                boolean success = json.getBooleanValue("success");
                if (success) {
                    return JSON.toJSONString(json.get("data"));
                }
                String msg = json.getString("msg");
                return JSON.toJSONString(Map.of("error", msg != null ? msg : "操作失败"));
            }
            // 非 Result 格式：直接返回
            return responseBody;
        } catch (Exception e) {
            log.debug("[HTTP工具] 响应体非JSON格式: {}", responseBody);
            return responseBody;
        }
    }

    /**
     * 从 ToolContext 获取内部参数值
     * 与 FeignToolCallback.resolveInternalValue 逻辑一致
     */
    private String resolveInternalValue(String key, ToolContext context) {
        if (context == null) return "";
        Map<String, Object> ctx = context.getContext();
        Object val = ctx.get(key);
        if (val != null) return String.valueOf(val);
        // 兜底：从 AuthInfoUtils 获取
        if (RPCConstants.REQ_USER_ID.equals(key)) {
            return cn.com.mfish.common.core.utils.AuthInfoUtils.getCurrentUserId();
        }
        if (RPCConstants.REQ_TENANT_ID.equals(key)) {
            return cn.com.mfish.common.core.utils.AuthInfoUtils.getCurrentTenantId();
        }
        if (RPCConstants.AI.equals(key)) {
            return RPCConstants.AI;
        }
        return "";
    }

    /**
     * 获取当前请求的 token（不带 Bearer 前缀）
     * <p>
     * 优先从 ToolContext 中获取（由 BaseAssistant.buildToolContext 在请求线程捕获）；
     * 若 context 中无 token，兜底从 AuthInfoUtils.getAccessToken() 获取
     * （依赖 call() 方法开头恢复的请求上下文）。
     * </p>
     */
    private String resolveToken(ToolContext context) {
        // 1. 优先从 context 取（请求线程捕获的 token）
        if (context != null) {
            Map<String, Object> ctx = context.getContext();
            Object val = ctx.get(RPCConstants.REQ_TOKEN);
            if (val != null && !String.valueOf(val).isEmpty()) {
                return String.valueOf(val);
            }
        }
        // 2. 兜底：从当前请求上下文获取（call() 方法已恢复 RequestContextHolder/ServletUtils）
        return cn.com.mfish.common.core.utils.AuthInfoUtils.getAccessToken();
    }

    @SuppressWarnings("unchecked")
    private <T> T extractFromContext(ToolContext context, String key, Class<T> type) {
        if (context == null) return null;
        Map<String, Object> ctx = context.getContext();
        Object val = ctx.get(key);
        if (type.isInstance(val)) {
            return (T) val;
        }
        return null;
    }

    public String getToolName() {
        return toolDefinition.name();
    }

    /**
     * 判断 OpenAPI 参数是否为内部参数（不暴露给 LLM）
     * 供 OpenApiDocParser 构造 HttpParamInfo 时调用
     */
    public static boolean isInternalParam(String location, String paramName) {
        if (!"header".equals(location)) return false;
        return INTERNAL_HEADER_NAMES.contains(paramName);
    }

    /**
     * 获取内部参数的自动填充 key
     */
    public static String resolveAutoFillKey(String paramName) {
        if (RPCConstants.REQ_ORIGIN.equals(paramName) || "origin".equals(paramName)) return RPCConstants.REQ_ORIGIN;
        if (RPCConstants.REQ_TENANT_ID.equals(paramName) || "tenantId".equals(paramName) || "tenant-id".equals(paramName))
            return RPCConstants.REQ_TENANT_ID;
        if (RPCConstants.REQ_USER_ID.equals(paramName) || "userId".equals(paramName) || "user-id".equals(paramName))
            return RPCConstants.REQ_USER_ID;
        if ("Authorization".equals(paramName) || "access_token".equals(paramName)) return RPCConstants.REQ_TOKEN;
        return null;
    }

    /**
     * HTTP 参数信息（record）
     *
     * @param name        参数名（暴露给 LLM 的名称）
     * @param headerName  header 参数的实际请求头名（与 name 可能不同，如 Authorization）
     * @param location    参数位置：path / query / header / body
     * @param description 参数描述
     * @param required    是否必填
     * @param internal    是否内部参数（不暴露给 LLM）
     * @param autoFillKey 内部参数的自动填充 key（REQ_ORIGIN 等）
     * @param schema      OpenAPI schema 片段（JSON Schema 格式）
     */
    public record HttpParamInfo(
            String name,
            String headerName,
            String location,
            String description,
            boolean required,
            boolean internal,
            String autoFillKey,
            JSONObject schema
    ) {
        public static HttpParamInfo of(String name, String location, String description,
                                       boolean required, JSONObject schema) {
            boolean internal = isInternalParam(location, name);
            String autoFillKey = internal ? resolveAutoFillKey(name) : null;
            String headerName = "header".equals(location) ? name : null;
            return new HttpParamInfo(name, headerName, location, description, required, internal, autoFillKey, schema);
        }
    }
}
