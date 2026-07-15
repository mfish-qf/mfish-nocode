package cn.com.mfish.common.ai.feign;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ServerWebExchange;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Feign接口方法 → Spring AI ToolCallback 适配器
 * <p>
 * 将Feign接口的每个方法包装为ToolCallback，实现：
 * 1. 自动生成JSON Schema（仅暴露LLM需要的参数，内部参数如origin/tenantId/userId自动填充）
 * 2. AI调用Tool时，解析JSON输入，填充内部参数，通过反射调用Feign代理方法
 * 3. 调用前临时注入RequestContextHolder，使Feign的BearerTokenInterceptor能正常转发RPC头部
 * 4. 将Result返回值提取data转为JSON字符串供LLM理解
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class FeignToolCallback implements ToolCallback {

    /**
     * 自动填充的参数名集合（不暴露给LLM，从请求上下文自动获取）
     */
    private static final Set<String> AUTO_FILL_NAMES = Set.of("origin", "tenantId", "userId", "token");

    private final Object feignProxy;
    private final Method method;
    private final String toolName;
    private final String toolDescription;
    private final List<ParamInfo> paramInfos;
    private final ToolDefinition toolDefinition;

    /**
     * 参数元数据
     */
    private record ParamInfo(int index, String name, Class<?> type, boolean internal,
                             String autoFillKey, String description) {
    }

    public FeignToolCallback(Object feignProxy, Method method) {
        this.feignProxy = feignProxy;
        this.method = method;
        this.toolName = method.getName();
        this.toolDescription = buildDescription(method);
        this.paramInfos = analyzeParameters();
        this.toolDefinition = DefaultToolDefinition.builder()
                .name(toolName)
                .description(toolDescription)
                .inputSchema(generateInputSchema())
                .build();
    }

    private String buildDescription(Method method) {
        return "调用远程接口: " + method.getName();
    }

    /**
     * 分析方法参数，区分内部参数（自动填充）和LLM参数（暴露给模型）
     */
    private List<ParamInfo> analyzeParameters() {
        List<ParamInfo> infos = new ArrayList<>();
        java.lang.reflect.Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            java.lang.reflect.Parameter param = params[i];
            String name = resolveParamName(param);
            String autoFillKey = determineAutoFillKey(name, param);
            boolean internal = autoFillKey != null;
            String desc = resolveDescription(param);
            infos.add(new ParamInfo(i, name, param.getType(), internal, autoFillKey, desc));
        }
        return infos;
    }

    /**
     * 判断参数是否为内部参数，返回标准化的 autoFillKey
     * <p>
     * 映射规则：
     * - @RequestHeader("req-origin")   → "origin"
     * - @RequestHeader("req-tenant-id") → "tenantId"
     * - @RequestHeader("req-user-id")   → "userId"
     * - @RequestHeader("Authorization") → "token"
     * - 其他 @RequestHeader → "header"
     */
    private String determineAutoFillKey(String paramName, java.lang.reflect.Parameter param) {
        if (param.isAnnotationPresent(RequestHeader.class)) {
            if (RPCConstants.REQ_ORIGIN.equals(paramName)) return "origin";
            if (RPCConstants.REQ_TENANT_ID.equals(paramName)) return "tenantId";
            if (RPCConstants.REQ_USER_ID.equals(paramName)) return "userId";
            if ("Authorization".equals(paramName) || "access_token".equals(paramName)) return "token";
            return "header";
        }
        if (AUTO_FILL_NAMES.contains(paramName)) {
            return paramName;
        }
        return null;
    }

    private String resolveParamName(java.lang.reflect.Parameter param) {
        RequestParam rp = param.getAnnotation(RequestParam.class);
        if (rp != null && !rp.value().isEmpty()) return rp.value();
        PathVariable pv = param.getAnnotation(PathVariable.class);
        if (pv != null && !pv.value().isEmpty()) return pv.value();
        RequestHeader rh = param.getAnnotation(RequestHeader.class);
        if (rh != null && !rh.value().isEmpty()) return rh.value();
        return param.getName();
    }

    private String resolveDescription(java.lang.reflect.Parameter param) {
        Parameter swaggerParam = param.getAnnotation(Parameter.class);
        if (swaggerParam != null && !swaggerParam.description().isEmpty()) {
            return swaggerParam.description();
        }
        return "";
    }

    /**
     * 为LLM可见的参数生成JSON Schema
     */
    private String generateInputSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");
        JSONObject properties = new JSONObject();
        List<String> required = new ArrayList<>();
        Set<Class<?>> visiting = new java.util.HashSet<>();

        for (ParamInfo info : paramInfos) {
            if (info.internal()) continue;
            JSONObject propSchema = generateTypeSchema(info.type(), visiting);
            if (!info.description().isEmpty()) {
                propSchema.put("description", info.description());
            }
            properties.put(info.name(), propSchema);
            required.add(info.name());
        }

        schema.put("properties", properties);
        if (!required.isEmpty()) schema.put("required", required);
        return schema.toJSONString();
    }

    private JSONObject generateTypeSchema(Class<?> type, Set<Class<?>> visiting) {
        JSONObject schema = new JSONObject();
        if (type == String.class || type == CharSequence.class
                || type == char.class || type == Character.class) {
            schema.put("type", "string");
        } else if (type == Integer.class || type == int.class
                || type == Long.class || type == long.class) {
            schema.put("type", "integer");
        } else if (type == Double.class || type == double.class
                || type == Float.class || type == float.class
                || type == java.math.BigDecimal.class || type == java.math.BigInteger.class) {
            schema.put("type", "number");
        } else if (type == Boolean.class || type == boolean.class) {
            schema.put("type", "boolean");
        } else if (type == java.time.temporal.Temporal.class || type == java.util.Date.class
                || type == java.time.LocalDate.class || type == java.time.LocalDateTime.class
                || type == java.time.LocalTime.class || type == java.time.Instant.class
                || type.isEnum()) {
            schema.put("type", "string");
        } else if (type.isArray() || java.util.Collection.class.isAssignableFrom(type)
                || java.util.Map.class.isAssignableFrom(type)) {
            schema.put("type", "string");
        } else if (type == Object.class || visiting.contains(type)) {
            schema.put("type", "object");
        } else {
            visiting.add(type);
            schema.put("type", "object");
            JSONObject props = new JSONObject();
            for (Field field : getAllFields(type)) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic() || field.getType() == type) continue;
                props.put(field.getName(), generateTypeSchema(field.getType(), visiting));
            }
            schema.put("properties", props);
            visiting.remove(type);
        }
        return schema;
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null && type != Object.class) {
            for (Field field : type.getDeclaredFields()) fields.add(field);
            type = type.getSuperclass();
        }
        return fields;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return toolDefinition;
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return ToolMetadata.builder().build();
    }

    @Override
    public String call(String toolInput) {
        return call(toolInput, null);
    }

    @Override
    public String call(String toolInput, ToolContext context) {
        // 恢复请求上下文：将请求线程的上下文传播到工具执行线程
        // Servlet: RequestContextHolder → BearerTokenInterceptor 读取头部
        // WebFlux: ServletUtils.EXCHANGE_HOLDER → AuthInfoUtils 兜底读取头部
        RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
        ServerWebExchange previousExchange = ServletUtils.getExchange();
        RequestAttributes inheritedAttributes = extractFromContext(context, "requestAttributes", RequestAttributes.class);
        ServerWebExchange inheritedExchange = extractFromContext(context, "serverWebExchange", ServerWebExchange.class);
        boolean servletInjected = false;
        boolean exchangeInjected = false;
        try {
            // Servlet 环境：恢复 RequestContextHolder
            if (previousAttributes == null && inheritedAttributes != null) {
                RequestContextHolder.setRequestAttributes(inheritedAttributes);
                servletInjected = true;
            }
            // WebFlux 环境：恢复 ServletUtils.EXCHANGE_HOLDER
            if (previousExchange == null && inheritedExchange != null) {
                ServletUtils.setExchange(inheritedExchange);
                exchangeInjected = true;
            }
            JSONObject input = StringUtils.isEmpty(toolInput) || "{}".equals(toolInput.trim())
                    ? new JSONObject() : JSON.parseObject(toolInput);
            Object[] args = new Object[paramInfos.size()];
            for (ParamInfo info : paramInfos) {
                if (info.internal()) {
                    args[info.index()] = resolveInternalValue(info.autoFillKey(), context);
                } else {
                    args[info.index()] = resolveInputValue(input, info);
                }
            }
            Object result = method.invoke(feignProxy, args);
            return convertResult(result);
        } catch (Exception e) {
            log.error("[Feign工具] 调用失败: {} input={}", toolName, toolInput, e);
            return JSON.toJSONString(Map.of("error", e.getMessage() != null ? e.getMessage() : "工具调用异常"));
        } finally {
            if (servletInjected) {
                RequestContextHolder.resetRequestAttributes();
            }
            if (exchangeInjected) {
                ServletUtils.removeExchange();
            }
        }
    }

    /**
     * 从 ToolContext 中提取指定类型的值
     */
    private <T> T extractFromContext(ToolContext context, String key, Class<T> type) {
        if (context == null || context.getContext() == null) return null;
        Object value = context.getContext().get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    /**
     * 解析内部参数值：优先从 ToolContext 获取，其次从 RequestContextHolder 获取
     */
    private Object resolveInternalValue(String key, ToolContext context) {
        // 优先从 ToolContext 获取
        if (context != null && context.getContext() != null && context.getContext().containsKey(key)) {
            return context.getContext().get(key);
        }
        // 兜底：从 RequestContextHolder（已由上方注入）获取
        return switch (key) {
            case "origin" -> {
                try { yield AuthInfoUtils.isInnerRequest() ? RPCConstants.INNER : RPCConstants.INNER; }
                catch (Exception e) { yield RPCConstants.INNER; }
            }
            case "tenantId" -> {
                try { yield AuthInfoUtils.getCurrentTenantId(); }
                catch (Exception e) { yield AuthInfoUtils.SUPER_TENANT_ID; }
            }
            case "userId" -> {
                try { yield AuthInfoUtils.getCurrentUserId(); }
                catch (Exception e) { yield null; }
            }
            case "token" -> {
                try { yield AuthInfoUtils.getAccessToken(); }
                catch (Exception e) { yield null; }
            }
            default -> null;
        };
    }

    private Object resolveInputValue(JSONObject input, ParamInfo info) {
        if (!input.containsKey(info.name())) return getDefaultValue(info.type());
        Object value = input.get(info.name());
        if (value == null) return getDefaultValue(info.type());
        if (info.type() == String.class) return value.toString();
        if (isSimpleType(info.type())) return input.getObject(info.name(), info.type());
        return JSON.parseObject(input.getJSONObject(info.name()).toJSONString(), info.type());
    }

    private boolean isSimpleType(Class<?> type) {
        return type == String.class || type == Integer.class || type == int.class
                || type == Long.class || type == long.class
                || type == Double.class || type == double.class
                || type == Float.class || type == float.class
                || type == Boolean.class || type == boolean.class;
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        return null;
    }

    private String convertResult(Object result) {
        if (result == null) return "{\"success\":true,\"data\":null}";
        if (result instanceof Result<?> r) {
            if (r.isSuccess()) return JSON.toJSONString(r.getData());
            return JSON.toJSONString(Map.of("error", r.getMsg() != null ? r.getMsg() : "操作失败", "code", r.getCode()));
        }
        return JSON.toJSONString(result);
    }

    public String getToolName() {
        return toolName;
    }
}
