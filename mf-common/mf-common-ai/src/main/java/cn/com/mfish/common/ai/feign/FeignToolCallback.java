package cn.com.mfish.common.ai.feign;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
import java.util.*;

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
     * 复用 RPCConstants 中已有的 header 名常量
     */
    private static final Set<String> AUTO_FILL_NAMES = Set.of(
            RPCConstants.REQ_ORIGIN, RPCConstants.REQ_TENANT_ID,
            RPCConstants.REQ_USER_ID, RPCConstants.REQ_TOKEN);

    private final Object feignProxy;
    private final Method method;
    @Getter
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
        // 优先从方法上的 Swagger @Operation 注解提取描述
        io.swagger.v3.oas.annotations.Operation op = method.getAnnotation(io.swagger.v3.oas.annotations.Operation.class);
        if (op != null && !op.summary().isEmpty()) {
            return op.summary();
        }
        if (op != null && !op.description().isEmpty()) {
            return op.description();
        }
        // 兜底：方法名转可读描述（如 queryMenuTree → query menu tree）
        return method.getName() + " (调用远程接口)";
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
     * 判断参数是否为内部参数，返回 autoFillKey（用于从 ToolContext 中取值）
     * <p>
     * RPC header 参数（req-origin/req-tenant-id/req-user-id）直接用 header 名作为 key，
     * Authorization/access_token 映射为 REQ_TOKEN，其他 @RequestHeader 视为普通 header 转发
     */
    private String determineAutoFillKey(String paramName, java.lang.reflect.Parameter param) {
        if (param.isAnnotationPresent(RequestHeader.class)) {
            if (RPCConstants.REQ_ORIGIN.equals(paramName)
                    || RPCConstants.REQ_TENANT_ID.equals(paramName)
                    || RPCConstants.REQ_USER_ID.equals(paramName)) {
                return paramName;
            }
            if (Constants.AUTHENTICATION.equals(paramName) || Constants.ACCESS_TOKEN.equals(paramName)) return RPCConstants.REQ_TOKEN;
            return Constants.HEADER;
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
            Collections.addAll(fields, type.getDeclaredFields());
            type = type.getSuperclass();
        }
        return fields;
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
        // 恢复请求上下文：将请求线程的上下文传播到工具执行线程
        // Servlet: RequestContextHolder → BearerTokenInterceptor 读取头部
        // WebFlux: ServletUtils.EXCHANGE_HOLDER → AuthInfoUtils 兜底读取头部
        RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
        ServerWebExchange previousExchange = ServletUtils.getExchange();
        RequestAttributes inheritedAttributes = extractFromContext(context, RPCConstants.REQ_REQUEST_ATTRIBUTES, RequestAttributes.class);
        ServerWebExchange inheritedExchange = extractFromContext(context, RPCConstants.REQ_SERVER_WEB_EXCHANGE, ServerWebExchange.class);
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
            // method.invoke 反射调用时，目标方法抛出的异常会被包装成 InvocationTargetException
            // 直接取 e.getMessage() 只能拿到外层包装信息（通常为 null），拿不到业务异常的真实消息
            // 需要解包取出真正的 cause，才能把"错误:该用户无此操作权限"等业务提示透传给 LLM
            Throwable cause = e;
            while (cause instanceof java.lang.reflect.InvocationTargetException
                    || cause instanceof java.lang.reflect.UndeclaredThrowableException) {
                if (cause.getCause() == null) break;
                cause = cause.getCause();
            }
            String msg = cause.getMessage() != null ? cause.getMessage() : "工具调用异常";
            log.error("[Feign工具] 调用失败: {} input={}", toolName, toolInput, cause);
            return JSON.toJSONString(Map.of("error", msg));
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
        if (context == null) return null;
        Object value = context.getContext().get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    /**
     * 解析内部参数值：优先从 ToolContext 获取，其次从 RequestContextHolder 获取
     */
    private Object resolveInternalValue(String key, ToolContext context) {
        // 优先从 ToolContext 获取
        if (context != null && context.getContext().containsKey(key)) {
            return context.getContext().get(key);
        }
        // 兜底：从 RequestContextHolder（已由上方注入）获取
        return switch (key) {
            case RPCConstants.REQ_ORIGIN -> {
                try { yield AuthInfoUtils.isInnerRequest() ? RPCConstants.INNER : RPCConstants.AI; }
                catch (Exception e) { yield RPCConstants.AI; }
            }
            case RPCConstants.REQ_TENANT_ID -> {
                try { yield AuthInfoUtils.getCurrentTenantId(); }
                catch (Exception e) { yield AuthInfoUtils.SUPER_TENANT_ID; }
            }
            case RPCConstants.REQ_USER_ID -> {
                try { yield AuthInfoUtils.getCurrentUserId(); }
                catch (Exception e) { yield null; }
            }
            case RPCConstants.REQ_TOKEN -> {
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
            // 微服务场景下Feign降级返回Result.fail（而非抛异常），此处提取msg返回给LLM
            // 返回格式与异常路径（catch块）保持一致：{"error": msg}
            return JSON.toJSONString(Map.of("error", r.getMsg() != null ? r.getMsg() : "操作失败"));
        }
        return JSON.toJSONString(result);
    }

}
