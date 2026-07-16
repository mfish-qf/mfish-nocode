package cn.com.mfish.common.ai.provider;

import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.Map;

/**
 * 工具来源提供者抽象接口
 * <p>
 * 统一不同来源的接口转换为 Spring AI Tool 的接入方式，例如：
 * <ul>
 *     <li>{@code feign} —— 扫描 Spring Cloud OpenFeign 接口（{@link cn.com.mfish.common.ai.feign.FeignToolProvider}）</li>
 *     <li>{@code http}  —— 基于目标服务 OpenAPI 文档，通过 WebClient 调用 REST 接口（{@code HttpToolProvider}）</li>
 *     <li>{@code mcp}   —— 基于 MCP Server 协议（未来扩展）</li>
 * </ul>
 * 各 Provider 独立完成"接口发现 → ToolCallback 构建"，由 {@link cn.com.mfish.common.ai.engine.ApiToolEngine}
 * 聚合后供 {@code BaseAssistant} 按服务ID统一获取。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/15
 */
public interface ToolProvider {

    /**
     * Provider 类型标识（feign / http / mcp），用于日志和调试
     *
     * @return 类型字符串
     */
    String getType();

    /**
     * 发现并构建本 Provider 提供的全部工具，按微服务ID分组返回
     * <p>
     * 由 {@link cn.com.mfish.common.ai.engine.ApiToolEngine} 在启动期调用，
     * 多个 Provider 的结果会按 serviceId 合并，再对外提供统一的 {@code getToolCallbacks(serviceId)}。
     * </p>
     *
     * @return serviceId → ToolCallback 列表，无工具时返回空 Map
     */
    Map<String, List<ToolCallback>> discoverTools();
}
