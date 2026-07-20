package cn.com.mfish.common.ai.agent;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * 工具调用能力接口
 * <p>
 * 抽象"按服务集合调用工具的聊天"能力，供 {@code Executor} 在自主规划场景下复用，
 * 避免直接依赖具体的 BaseAssistant 类。
 * </p>
 * <p>
 * 实现方（如 BaseAssistant）负责：
 * <ol>
 *   <li>构建当前请求租户对应的 ChatClient</li>
 *   <li>按 serviceIds 从 ApiToolEngine 聚合工具</li>
 *   <li>注入 ToolContext（token/tenantId/userId 等内部参数）</li>
 *   <li>执行流式调用并支持降级</li>
 * </ol>
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
public interface ToolCapable {

    /**
     * 按服务集合聚合工具进行流式聊天
     * <p>
     * Executor 在每一步执行时调用此方法，传入 Planner 决策出的 serviceIds，
     * 实现方按需聚合对应服务的工具供 LLM 选择。
     * </p>
     * <p>
     * 租户/认证信息由实现方从当前请求线程获取（适用于同步请求链路）。
     * </p>
     *
     * @param sessionId  会话ID
     * @param prompt    这一步的用户提示词（通常是步骤描述 + 上下文）
     * @param serviceIds 需要聚合工具的微服务ID集合
     * @return 流式聊天响应
     */
    Flux<ChatResponse> chatWithTools(String sessionId, String prompt, Set<String> serviceIds);

    /**
     * 按服务集合聚合工具进行流式聊天（带预捕获的租户上下文）
     * <p>
     * 用于异步编排场景（如 AgentRuntime 在 boundedElastic 线程执行）：
     * 请求线程先捕获 {@link TenantContext} 快照传入，避免实现方在异步线程
     * 调用 AuthInfoUtils 时拿不到 RequestAttributes。
     * </p>
     *
     * @param sessionId       会话ID
     * @param prompt          提示词
     * @param serviceIds      需要聚合工具的微服务ID集合
     * @param tenantContext   请求线程捕获的租户上下文快照
     * @return 流式聊天响应
     */
    Flux<ChatResponse> chatWithTools(String sessionId, String prompt, Set<String> serviceIds,
                                     TenantContext tenantContext);
}

