package cn.com.mfish.common.ai.engine;

import cn.com.mfish.common.ai.provider.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API工具引擎 —— 顶层工具注册中心
 * <p>
 * 聚合所有 {@link ToolProvider} 实现提供的工具，按微服务ID合并存储，对外提供统一查询入口。
 * 各 Assistant 通过 {@code apiToolEngine.getToolCallbackProvider(serviceId)} 获取聚合后的工具列表，
 * 无需关心工具来源（Feign / HTTP / MCP）。
 * </p>
 * <p>
 * 架构示意：
 * <pre>
 *                  ApiToolEngine
 *                       │
 *       ┌───────────────┼───────────────┐
 *       │               │               │
 *   Feign Provider  HTTP Provider   MCP Provider
 *       │               │               │
 *   Spring Cloud    OpenAPI接口       MCP Server
 *       │
 *   Controller → Service
 * </pre>
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class ApiToolEngine {

    /**
     * serviceId → 聚合后的工具列表
     * 多个 Provider 产生的同 serviceId 工具会合并到此列表
     */
    private final Map<String, List<ToolCallback>> toolsByService = new ConcurrentHashMap<>();

    /**
     * 注册一组由某个 Provider 提供的、属于指定服务的工具
     * <p>
     * 同一 serviceId 可被多个 Provider 注册（如 Feign 与 HTTP 同时覆盖某服务），
     * 新工具会追加到已存在的列表中。
     * </p>
     *
     * @param serviceId 微服务ID（如 mf-oauth、mf-sys）
     * @param callbacks 工具回调列表
     */
    public synchronized void register(String serviceId, List<ToolCallback> callbacks) {
        if (callbacks == null || callbacks.isEmpty()) {
            return;
        }
        toolsByService.computeIfAbsent(serviceId, k -> new ArrayList<>()).addAll(callbacks);
        log.info("[ApiToolEngine] 注册 service={} 工具数={} 累计={}",
                serviceId, callbacks.size(), toolsByService.get(serviceId).size());
    }

    /**
     * 替换指定服务的全部工具（先清空再注册）
     * <p>
     * 用于 HttpToolProvider 异步重试成功后，动态更新某服务的工具列表。
     * 与 {@link #register} 的区别：register 是追加，replace 是覆盖。
     * </p>
     *
     * @param serviceId 微服务ID
     * @param callbacks 新的工具回调列表
     */
    public synchronized void replace(String serviceId, List<ToolCallback> callbacks) {
        if (serviceId == null || serviceId.isEmpty()) return;
        int oldSize = toolsByService.getOrDefault(serviceId, List.of()).size();
        if (callbacks == null || callbacks.isEmpty()) {
            toolsByService.remove(serviceId);
        } else {
            toolsByService.put(serviceId, new ArrayList<>(callbacks));
        }
        log.info("[ApiToolEngine] 替换 service={} 工具数: {} → {}",
                serviceId, oldSize, callbacks != null ? callbacks.size() : 0);
    }

    /**
     * 初始化：遍历所有 ToolProvider，聚合其发现的工具
     * <p>
     * 由 {@code ApiToolAutoConfiguration} 在启动期调用。每个 Provider 独立发现工具，
     * Engine 按 serviceId 合并，实现"来源透明"。
     * </p>
     *
     * @param providers 所有已注册的 ToolProvider 实现
     */
    public void initialize(List<ToolProvider> providers) {
        if (providers == null || providers.isEmpty()) {
            log.warn("[ApiToolEngine] 未发现任何 ToolProvider，工具集为空");
            return;
        }
        int totalTools = 0;
        for (ToolProvider provider : providers) {
            try {
                Map<String, List<ToolCallback>> tools = provider.discoverTools();
                if (tools == null || tools.isEmpty()) {
                    log.info("[ApiToolEngine] Provider={} 未发现工具", provider.getType());
                    continue;
                }
                for (Map.Entry<String, List<ToolCallback>> entry : tools.entrySet()) {
                    register(entry.getKey(), entry.getValue());
                    totalTools += entry.getValue().size();
                }
                log.info("[ApiToolEngine] Provider={} 注册完成，服务数={}",
                        provider.getType(), tools.size());
            } catch (Exception e) {
                log.error("[ApiToolEngine] Provider={} 发现工具失败", provider.getType(), e);
            }
        }
        log.info("[ApiToolEngine] 初始化完成，共注册 {} 个服务，{} 个工具",
                toolsByService.size(), totalTools);
    }

    /**
     * 获取指定服务的全部工具（跨所有 Provider 聚合）
     *
     * @param serviceId 微服务ID
     * @return 工具回调列表，无匹配时返回空列表
     */
    private List<ToolCallback> getToolCallbacks(String serviceId) {
        return toolsByService.getOrDefault(serviceId, List.of());
    }

    /**
     * 获取指定服务的 ToolCallbackProvider（Spring AI 2.0 推荐方式）
     * <p>
     * 通过 {@code .tools(apiToolEngine.getToolCallbackProvider(serviceId))} 传入 ChatClient。
     * </p>
     *
     * @param serviceId 微服务ID
     * @return ToolCallbackProvider，无匹配时返回空 Provider
     */
    public ToolCallbackProvider getToolCallbackProvider(String serviceId) {
        return ToolCallbackProvider.from(getToolCallbacks(serviceId));
    }
}
