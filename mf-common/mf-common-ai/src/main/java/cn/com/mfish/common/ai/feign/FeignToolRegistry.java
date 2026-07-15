package cn.com.mfish.common.ai.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Feign工具注册中心
 * <p>
 * 按微服务ID分组存储自动生成的ToolCallback，供各Assistant按需获取。
 * 启动时由FeignToolScanner扫描所有@FeignClient接口并注册。
 * <p>
 * 提供 {@link ToolCallbackProvider} 以适配 Spring AI 2.0 的 tools() API。
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class FeignToolRegistry {

    private final Map<String, List<ToolCallback>> toolsByService = new ConcurrentHashMap<>();

    /**
     * 注册某个服务的工具列表
     *
     * @param serviceId 微服务ID（如 mf-oauth、mf-sys）
     * @param callbacks  工具回调列表
     */
    void register(String serviceId, List<ToolCallback> callbacks) {
        toolsByService.put(serviceId, List.copyOf(callbacks));
        log.info("[Feign工具注册] service={} 工具数={} 工具列表={}",
                serviceId, callbacks.size(),
                callbacks.stream().map(c -> ((FeignToolCallback) c).getToolName()).toList());
    }

    /**
     * 获取指定服务的工具列表
     *
     * @param serviceId 微服务ID
     * @return 工具回调列表，无匹配时返回空列表
     */
    public List<ToolCallback> getToolCallbacks(String serviceId) {
        return toolsByService.getOrDefault(serviceId, List.of());
    }

    /**
     * 获取多个服务的工具列表（聚合）
     *
     * @param serviceIds 微服务ID数组
     * @return 聚合后的工具回调列表
     */
    public List<ToolCallback> getToolCallbacks(String... serviceIds) {
        List<ToolCallback> result = new ArrayList<>();
        for (String serviceId : serviceIds) {
            result.addAll(getToolCallbacks(serviceId));
        }
        return result;
    }

    /**
     * 获取指定服务的 ToolCallbackProvider（Spring AI 2.0 推荐方式）
     * <p>
     * 通过 {@code .tools(registry.getToolCallbackProvider(serviceId))} 传入ChatClient，
     * 替代已废弃的 {@code .toolCallbacks(...)} 方法。
     *
     * @param serviceId 微服务ID
     * @return ToolCallbackProvider，无匹配时返回空Provider
     */
    public ToolCallbackProvider getToolCallbackProvider(String serviceId) {
        return ToolCallbackProvider.from(getToolCallbacks(serviceId));
    }

    /**
     * 获取多个服务的 ToolCallbackProvider（聚合）
     *
     * @param serviceIds 微服务ID数组
     * @return 聚合后的 ToolCallbackProvider
     */
    public ToolCallbackProvider getToolCallbackProvider(String... serviceIds) {
        return ToolCallbackProvider.from(getToolCallbacks(serviceIds));
    }

    /**
     * 获取已注册的所有服务ID
     *
     * @return 服务ID集合
     */
    public Set<String> getRegisteredServices() {
        return toolsByService.keySet();
    }

    /**
     * 获取所有服务的所有工具
     *
     * @return 全部工具回调列表
     */
    public List<ToolCallback> getAllToolCallbacks() {
        List<ToolCallback> result = new ArrayList<>();
        for (List<ToolCallback> callbacks : toolsByService.values()) {
            result.addAll(callbacks);
        }
        return result;
    }
}
