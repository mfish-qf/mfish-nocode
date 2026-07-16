package cn.com.mfish.common.ai.http;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * OpenAPI文档解析器
 * <p>
 * 解析 springdoc-openapi 生成的 /v3/api-docs 文档，为每个 REST operation 生成 {@link HttpToolCallback}。
 * </p>
 * <p>
 * 解析流程：
 * <ol>
 *     <li>遍历 paths 下每个 path × method（get/post/put/delete/patch）</li>
 *     <li>提取 operationId 作为工具名，summary + description 作为工具描述</li>
 *     <li>解析 parameters（path/query/header）和 requestBody（body）</li>
 *     <li>解析 $ref 引用，获取完整 schema</li>
 *     <li>过滤内部接口（actuator、/v3/api-docs 等）和 @InnerUser 标识的接口</li>
 * </ol>
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class OpenApiDocParser {

    private static final Set<String> HTTP_METHODS = Set.of("get", "post", "put", "delete", "patch");
    /**
     * 需要跳过的路径前缀（监控、文档、错误等非业务接口）
     */
    private static final Set<String> SKIP_PATH_PREFIXES = Set.of(
            "/actuator", "/v3/api-docs", "/error", "/swagger", "/webjars"
    );
    private static final int MAX_DEPTH = 10;

    /**
     * 解析 OpenAPI 文档，生成工具列表
     *
     * @param openApiJson OpenAPI 文档 JSON 字符串
     * @param webClient   已配置好 baseUrl 的 WebClient
     * @param serviceId   服务ID（用于生成工具描述）
     * @return 工具回调列表
     */
    public List<ToolCallback> parse(String openApiJson, WebClient webClient, String serviceId) {
        List<ToolCallback> callbacks = new ArrayList<>();
        if (openApiJson == null || openApiJson.isEmpty()) {
            return callbacks;
        }

        JSONObject doc = JSON.parseObject(openApiJson);
        JSONObject paths = doc.getJSONObject("paths");
        if (paths == null || paths.isEmpty()) {
            log.warn("[OpenAPI解析] service={} 文档无 paths", serviceId);
            return callbacks;
        }

        JSONObject components = doc.getJSONObject("components");
        JSONObject schemas = components != null ? components.getJSONObject("schemas") : null;

        for (Map.Entry<String, Object> entry : paths.entrySet()) {
            String path = entry.getKey();
            if (shouldSkipPath(path)) continue;

            JSONObject pathItem = (JSONObject) entry.getValue();
            for (String method : HTTP_METHODS) {
                JSONObject operation = pathItem.getJSONObject(method);
                if (operation == null) continue;

                try {
                    HttpToolCallback callback = parseOperation(path, method, operation, schemas, webClient, serviceId);
                    callbacks.add(callback);
                    log.debug("[OpenAPI解析] service={} 生成工具: {} {} {}",
                            serviceId, method.toUpperCase(), path, callback.getToolName());
                } catch (Exception e) {
                    log.warn("[OpenAPI解析] service={} 解析失败: {} {}",
                            serviceId, method.toUpperCase(), path, e);
                }
            }
        }

        log.info("[OpenAPI解析] service={} 共生成 {} 个HTTP工具", serviceId, callbacks.size());
        return callbacks;
    }

    /**
     * 解析 OpenAPI 文档，按路径前缀分组到不同 serviceId（单实例模式）
     * <p>
     * 单实例模式下所有服务的接口聚合在同一个 OpenAPI 文档中，
     * 通过 {@code WebMvcConfig.addPathPrefix} 给 Controller 路径添加了服务前缀（如 /sys、/oauth2）。
     * 本方法根据 pathMappings 配置的"路径前缀 → serviceId"映射，将接口分组到不同服务。
     * </p>
     *
     * @param openApiJson  OpenAPI 文档 JSON 字符串
     * @param webClient    已配置好 baseUrl 的 WebClient（指向单实例服务）
     * @param pathMappings 路径前缀 → serviceId 映射（如 {"/sys"="mf-sys", "/oauth2"="mf-oauth"}）
     * @return serviceId → 工具回调列表
     */
    public Map<String, List<ToolCallback>> parseByPathPrefix(String openApiJson, WebClient webClient,
                                                             Map<String, String> pathMappings) {
        Map<String, List<ToolCallback>> result = new HashMap<>();
        if (openApiJson == null || openApiJson.isEmpty() || pathMappings == null || pathMappings.isEmpty()) {
            return result;
        }

        JSONObject doc = JSON.parseObject(openApiJson);
        JSONObject paths = doc.getJSONObject("paths");
        if (paths == null || paths.isEmpty()) {
            log.warn("[OpenAPI解析] 单实例文档无 paths");
            return result;
        }

        JSONObject components = doc.getJSONObject("components");
        JSONObject schemas = components != null ? components.getJSONObject("schemas") : null;

        // 将 pathMappings 按前缀长度降序排列，确保最长前缀优先匹配（如 /sysLog 优先于 /sys）
        List<Map.Entry<String, String>> sortedMappings = pathMappings.entrySet().stream()
                .sorted((a, b) -> b.getKey().length() - a.getKey().length())
                .toList();

        for (Map.Entry<String, Object> entry : paths.entrySet()) {
            String path = entry.getKey();
            if (shouldSkipPath(path)) continue;

            // 根据路径前缀找到对应的 serviceId
            String serviceId = resolveServiceId(path, sortedMappings);
            if (serviceId == null) {
                log.debug("[OpenAPI解析] 单实例模式 路径 {} 无匹配的服务前缀，跳过", path);
                continue;
            }

            JSONObject pathItem = (JSONObject) entry.getValue();
            for (String method : HTTP_METHODS) {
                JSONObject operation = pathItem.getJSONObject(method);
                if (operation == null) continue;

                try {
                    HttpToolCallback callback = parseOperation(path, method, operation, schemas, webClient, serviceId);
                    result.computeIfAbsent(serviceId, k -> new ArrayList<>()).add(callback);
                    log.debug("[OpenAPI解析] 单实例 service={} 生成工具: {} {} {}",
                            serviceId, method.toUpperCase(), path, callback.getToolName());
                } catch (Exception e) {
                    log.warn("[OpenAPI解析] 单实例 service={} 解析失败: {} {}",
                            serviceId, method.toUpperCase(), path, e);
                }
            }
        }

        for (Map.Entry<String, List<ToolCallback>> e : result.entrySet()) {
            log.info("[OpenAPI解析] 单实例 service={} 共生成 {} 个HTTP工具",
                    e.getKey(), e.getValue().size());
        }
        return result;
    }

    /**
     * 根据路径前缀匹配 serviceId
     * <p>
     * 路径如 /sys/dict 匹配前缀 /sys，返回对应的 serviceId mf-sys。
     * 已按前缀长度降序排列，确保最长前缀优先匹配。
     * </p>
     */
    private String resolveServiceId(String path, List<Map.Entry<String, String>> sortedMappings) {
        for (Map.Entry<String, String> mapping : sortedMappings) {
            String prefix = mapping.getKey();
            // 精确匹配或前缀匹配（前缀后跟 / 或刚好等于前缀）
            if (path.equals(prefix) || path.startsWith(prefix + "/")) {
                return mapping.getValue();
            }
        }
        return null;
    }

    /**
     * 解析单个 operation，生成 HttpToolCallback
     */
    private HttpToolCallback parseOperation(String path, String method, JSONObject operation,
                                            JSONObject schemas, WebClient webClient, String serviceId) {
        // 1. 工具名：优先用 operationId，否则用 method+path 生成
        String operationId = operation.getString("operationId");
        if (operationId == null || operationId.isEmpty()) {
            operationId = method + path.replaceAll("[{}:/]", "_");
        }

        // 2. 工具描述：summary + description
        String summary = operation.getString("summary");
        String description = operation.getString("description");
        String toolDescription = "调用" + serviceId + "服务接口: " + (summary != null ? summary : path);
        if (description != null && !description.isEmpty()) {
            toolDescription += " - " + description;
        }

        // 3. 解析参数
        List<HttpToolCallback.HttpParamInfo> paramInfos = new ArrayList<>();

        // 3.1 parameters（path/query/header）
        JSONArray parameters = operation.getJSONArray("parameters");
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                JSONObject param = parameters.getJSONObject(i);
                String name = param.getString("name");
                String location = param.getString("in");
                String paramDesc = param.getString("description");
                boolean required = param.getBooleanValue("required");
                JSONObject schema = param.getJSONObject("schema");
                if (schema != null) {
                    schema = resolveRef(schema, schemas);
                }
                paramInfos.add(HttpToolCallback.HttpParamInfo.of(
                        name, location, paramDesc != null ? paramDesc : "", required, schema));
            }
        }

        // 3.2 requestBody（body 参数）
        JSONObject requestBody = operation.getJSONObject("requestBody");
        if (requestBody != null) {
            JSONObject content = requestBody.getJSONObject("content");
            if (content != null) {
                JSONObject jsonContent = content.getJSONObject("application/json");
                if (jsonContent != null) {
                    JSONObject schema = jsonContent.getJSONObject("schema");
                    if (schema != null) {
                        schema = resolveRef(schema, schemas);
                    }
                    String bodyDesc = requestBody.getString("description");
                    boolean bodyRequired = requestBody.getBooleanValue("required");
                    paramInfos.add(HttpToolCallback.HttpParamInfo.of(
                            "body", "body",
                            bodyDesc != null ? bodyDesc : "请求体JSON",
                            bodyRequired, schema));
                }
            }
        }

        return new HttpToolCallback(webClient, operationId, toolDescription, method, path, paramInfos);
    }

    /**
     * 解析 $ref 引用，返回完整内联 schema
     * <p>
     * OpenAI 的 tool schema 不支持 {@code $ref}，所有引用必须完全内联展开。
     * 本方法递归解析 {@code #/components/schemas/XXX} 引用，并处理：
     * <ul>
     *     <li>properties 中每个属性的 {@code $ref}</li>
     *     <li>items（数组元素）的 {@code $ref}</li>
     *     <li>allOf / anyOf / oneOf 组合器中的 {@code $ref}</li>
     * </ul>
     * </p>
     * <p>
     * <b>循环引用处理</b>：如 TreeView.children: TreeView[] 这类自引用会无限递归。
     * 通过 resolvingChain 记录正在解析的 ref 路径，遇到循环时返回简化 schema
     * {@code {"type":"object"}}，避免无限递归和 OpenAI 报 undefined schema。
     * </p>
     */
    private JSONObject resolveRef(JSONObject schema, JSONObject schemas) {
        return resolveRefInternal(schema, schemas, 0, new HashSet<>());
    }

    /**
     * 递归解析 $ref 引用
     *
     * @param schema         当前要解析的 schema 片段
     * @param schemas        OpenAPI 文档中的 components.schemas 全集
     * @param depth          当前递归深度，超过 MAX_DEPTH 返回简化 schema
     * @param resolvingChain 正在解析中的 ref 路径集合，用于检测循环引用
     */
    private JSONObject resolveRefInternal(JSONObject schema, JSONObject schemas,
                                          int depth, Set<String> resolvingChain) {
        if (schema == null) {
            return null;
        }
        if (depth > MAX_DEPTH) {
            // 深度超限，返回简化 schema 避免无限递归
            return simpleObjectSchema();
        }

        String ref = schema.getString("$ref");
        if (ref != null) {
            // 循环引用检测：如 ref 已在解析链路中，返回简化 schema
            if (resolvingChain.contains(ref)) {
                log.debug("[OpenAPI解析] 检测到循环引用: {}，返回简化schema", ref);
                return simpleObjectSchema();
            }
            // 解析引用路径：#/components/schemas/XXX
            String[] parts = ref.split("/");
            if (parts.length >= 4 && "components".equals(parts[1]) && "schemas".equals(parts[2])) {
                String schemaName = parts[3];
                JSONObject resolved = schemas != null ? schemas.getJSONObject(schemaName) : null;
                if (resolved != null) {
                    Set<String> newChain = new HashSet<>(resolvingChain);
                    newChain.add(ref);
                    return resolveRefInternal(resolved, schemas, depth + 1, newChain);
                }
            }
            // 引用无法解析，返回简化 schema（避免 $ref 残留导致 OpenAI 报错）
            return simpleObjectSchema();
        }

        // 非引用类型，递归处理子 schema
        JSONObject result = new JSONObject(schema);

        // 1. 处理 properties 中每个属性的 $ref
        JSONObject properties = schema.getJSONObject("properties");
        if (properties != null) {
            JSONObject resolvedProps = new JSONObject();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof JSONObject propSchema) {
                    resolvedProps.put(entry.getKey(),
                            resolveRefInternal(propSchema, schemas, depth + 1, resolvingChain));
                } else {
                    resolvedProps.put(entry.getKey(), value);
                }
            }
            result.put("properties", resolvedProps);
        }

        // 2. 处理 items（数组类型的元素 schema）
        JSONObject items = schema.getJSONObject("items");
        if (items != null) {
            result.put("items", resolveRefInternal(items, schemas, depth + 1, resolvingChain));
        }

        // 3. 处理 allOf / anyOf / oneOf 组合器
        for (String combiner : new String[]{"allOf", "anyOf", "oneOf"}) {
            JSONArray arr = schema.getJSONArray(combiner);
            if (arr != null) {
                JSONArray resolvedArr = new JSONArray();
                for (Object item : arr) {
                    if (item instanceof JSONObject itemSchema) {
                        resolvedArr.add(resolveRefInternal(itemSchema, schemas, depth + 1, resolvingChain));
                    } else {
                        resolvedArr.add(item);
                    }
                }
                result.put(combiner, resolvedArr);
            }
        }

        return result;
    }

    /**
     * 构造简化 schema，用于循环引用或深度超限时的兜底
     */
    private JSONObject simpleObjectSchema() {
        JSONObject simple = new JSONObject();
        simple.put("type", "object");
        return simple;
    }

    /**
     * 判断路径是否应跳过（非业务接口）
     */
    private boolean shouldSkipPath(String path) {
        if (path == null || path.isEmpty()) return true;
        for (String prefix : SKIP_PATH_PREFIXES) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }
}
