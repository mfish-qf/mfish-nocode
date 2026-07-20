package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.agent.TenantContext;
import cn.com.mfish.common.ai.entity.AgentPlan;
import cn.com.mfish.common.ai.entity.PlanStep;
import cn.com.mfish.common.core.constants.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * 任务规划器
 * <p>
 * 调用 LLM 将用户原始需求拆解为结构化的 {@link AgentPlan}：
 * <ol>
 *   <li>分析用户意图</li>
 *   <li>拆解为多个可执行的步骤</li>
 *   <li>每步指定需要调用的微服务集合</li>
 * </ol>
 * </p>
 * <p>
 * 采用结构化输出（responseEntity），LLM 直接返回 JSON 反序列化为 AgentPlan。
 * 必须在请求线程解析租户并构建 ChatClient（AuthInfoUtils 不支持异步）。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
@Slf4j
@Component
public class Planner {

    private final ChatMemory chatMemory;
    private final LlmModelRouter llmModelRouter;

    public Planner(ChatMemory chatMemory, LlmModelRouter llmModelRouter) {
        this.chatMemory = chatMemory;
        this.llmModelRouter = llmModelRouter;
    }

    /**
     * 规划：将用户需求拆解为步骤列表
     * <p>
     * 注意：本方法会被异步调度器（boundedElastic）调用，因此 ChatClient 必须在请求线程
     * 预构建并通过闭包传入。{@link TenantContext} 中包含请求线程捕获的 tenantId，
     * 用于路由到该租户的 ChatModel。
     * </p>
     *
     * @param sessionId      会话ID
     * @param prompt         用户原始需求
     * @param tenantContext  请求线程捕获的租户上下文
     * @return 执行计划
     */
    public Mono<AgentPlan> plan(String sessionId, String prompt, TenantContext tenantContext) {
        // 用请求线程捕获的 tenantId 构建 ChatClient（避免异步线程调用 currentTenantId）
        String tenantId = tenantContext != null && tenantContext.getTenantId() != null
                ? tenantContext.getTenantId()
                : llmModelRouter.currentTenantId();
        ChatClient chatClient = getChatClient(tenantId);
        String systemPrompt = buildPlannerPrompt();

        return Mono.fromCallable(() -> {
                    var responseEntity = chatClient.prompt()
                            .system(systemPrompt)
                            .user(prompt)
                            .advisors(a -> a.param(CONVERSATION_ID, sessionId))
                            .call()
                            .responseEntity(AgentPlan.class);
                    AgentPlan plan = Objects.requireNonNullElseGet(responseEntity.entity(), () -> fallbackPlan(prompt));
                    plan.setOriginalPrompt(prompt);
                    log.info("[Planner] 规划完成, 步骤数={}, summary={}",
                            plan.getSteps() != null ? plan.getSteps().size() : 0, plan.getSummary());
                    return plan;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    log.error("[Planner] 规划失败，降级为单步执行", ex);
                    return Mono.just(fallbackPlan(prompt));
                });
    }

    /**
     * 构建规划师系统提示词，包含可用服务列表
     */
    private String buildPlannerPrompt() {
        String serviceList = Arrays.stream(ServiceConstants.MfService.values())
                .map(s -> String.format("  - %s: %s", s.getValue(), s.getGatewayPrefix()))
                .collect(Collectors.joining("\n"));

        return """
                你是"摸鱼低代码"平台的任务规划师。
                用户会提出一个需求，你需要将其拆解为多个可执行的步骤。

                # 可用的微服务列表
                """ + serviceList + """

                # 输出要求
                返回 JSON 格式，结构如下：
                ```json
                {
                  "summary": "整体计划简述",
                  "steps": [
                    {
                      "description": "这一步要做什么，描述清楚让执行器能理解",
                      "serviceIds": ["mf-sys"]
                    }
                  ]
                }
                ```

                # 规划规则
                1. 每个步骤必须是可独立执行的任务，描述要具体明确
                2. serviceIds 必须从上面的可用服务列表中选择
                3. 如果需求简单，可以只规划 1 个步骤
                4. 复杂需求拆解为 2-5 个步骤，不要过度拆解
                5. 步骤之间应该有逻辑顺序（先查询后修改、先依赖后主流程）
                6. serviceIds 是数组，表示这一步可能需要多个服务的工具配合
                7. 不要编造不存在的 serviceId

                # 示例
                用户："查询系统中的菜单和角色信息，并整理成报告"
                返回：
                ```json
                {
                  "summary": "分两步查询认证中心的菜单和角色信息",
                  "steps": [
                    {
                      "description": "查询系统中所有菜单的树形结构信息",
                      "serviceIds": ["mf-oauth"]
                    },
                    {
                      "description": "查询系统中所有角色的信息",
                      "serviceIds": ["mf-oauth"]
                    }
                  ]
                }
                ```

                请以 JSON 格式返回，不要包含其他内容。
                """;
    }

    /**
     * 规划失败时的兜底：单步执行原始需求，聚合所有服务工具
     */
    private AgentPlan fallbackPlan(String prompt) {
        List<String> allServices = Arrays.stream(ServiceConstants.MfService.values())
                .map(ServiceConstants.MfService::getValue)
                .collect(Collectors.toList());
        return new AgentPlan()
                .setOriginalPrompt(prompt)
                .setSummary("规划降级：直接执行")
                .setSteps(List.of(
                        new PlanStep(prompt, allServices)
                ));
    }

    /**
     * 按指定租户ID构建 ChatClient
     *
     * @param tenantId 租户ID
     * @return 该租户对应的 ChatClient
     */
    private ChatClient getChatClient(String tenantId) {
        return ChatClient.builder(llmModelRouter.getChatModel(tenantId))
                .defaultSystem(buildPlannerPrompt())
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
