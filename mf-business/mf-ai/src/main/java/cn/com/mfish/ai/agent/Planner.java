package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.ai.runtime.ToolRuntime;
import cn.com.mfish.common.ai.agent.TenantContext;
import cn.com.mfish.common.ai.capability.ActionDefinition;
import cn.com.mfish.common.ai.capability.CapabilityEngine;
import cn.com.mfish.common.ai.entity.AgentPlan;
import cn.com.mfish.common.ai.entity.PlanStep;
import cn.com.mfish.common.ai.memory.ConversationMemory;
import cn.com.mfish.common.ai.memory.ConversationMemoryStore;
import cn.com.mfish.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * 任务规划器（已接入 CapabilityEngine + Memory）
 * <p>
 * 重构后职责（第二阶段存量平移）：
 * <ol>
 *   <li>从 {@link ConversationMemoryStore} 读取会话 Memory，获取系统上下文（Vars + Document）</li>
 *   <li>从 {@link CapabilityEngine} 获取所有可用 {@link ActionDefinition}，按服务分组展示给 LLM</li>
 *   <li>将上下文 + 动作列表 + 用户需求打包发给 LLM，生成结构化 {@link AgentPlan}</li>
 * </ol>
 * </p>
 * <p>
 * <b>与旧版差异</b>：
 * <ul>
 *     <li>服务列表来源：旧版从 {@code ServiceConstants.MfService.values()} 硬编码枚举获取；
 *         新版从 {@link CapabilityEngine#getAvailableActions()} 动态获取，自动反映已注册的子引擎</li>
 *     <li>上下文注入：旧版仅传入原始 prompt；新版从 Memory 读取 {@code getSystemContext()}
 *         拼接到 prompt 前，包含租户信息、业务变量和文档内容</li>
 *     <li>动作摘要：新版在规划提示词中展示每个服务的动作数量和示例动作名，
 *         帮助 LLM 更精准地选择 serviceIds</li>
 * </ul>
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
    private final CapabilityEngine capabilityEngine;
    private final ConversationMemoryStore memoryStore;
    private final ToolRuntime toolRuntime;

    public Planner(ChatMemory chatMemory, LlmModelRouter llmModelRouter,
                   CapabilityEngine capabilityEngine, ConversationMemoryStore memoryStore,
                   ToolRuntime toolRuntime) {
        this.chatMemory = chatMemory;
        this.llmModelRouter = llmModelRouter;
        this.capabilityEngine = capabilityEngine;
        this.memoryStore = memoryStore;
        this.toolRuntime = toolRuntime;
    }

    /**
     * 规划：将用户需求拆解为步骤列表
     * <p>
     * 重构后流程：
     * <ol>
     *   <li>从 Memory 读取系统上下文（Vars + Document），拼接到 prompt 前</li>
     *   <li>从 CapabilityEngine 获取所有可用 ActionDefinition，构建规划提示词</li>
     *   <li>调用 LLM 生成结构化 AgentPlan</li>
     *   <li>失败时降级为单步执行，聚合所有已注册服务</li>
     * </ol>
     * </p>
     * <p>
     * 注意：本方法会被异步调度器（boundedElastic）调用，因此 ChatClient 和系统提示词
     * 必须在请求线程预构建并通过闭包传入。{@link TenantContext} 中包含请求线程捕获的 tenantId，
     * 用于路由到该租户的 ChatModel。
     * </p>
     *
     * @param sessionId      会话ID
     * @param prompt         用户原始需求（已由 AgentRuntime 拼接 Document Context）
     * @param tenantContext  请求线程捕获的租户上下文
     * @return 执行计划
     */
    public Mono<AgentPlan> plan(String sessionId, String prompt, TenantContext tenantContext) {
        // 用请求线程捕获的 tenantId 构建 ChatClient（避免异步线程调用 currentTenantId）
        String tenantId = tenantContext != null && tenantContext.getTenantId() != null
                ? tenantContext.getTenantId()
                : llmModelRouter.currentTenantId();
        ChatClient chatClient = getChatClient(tenantId);

        // 从 Memory 读取系统上下文（Vars + Document），拼接到 prompt 前
        String systemContext = resolveSystemContext(sessionId);

        // 从 CapabilityEngine 获取动作列表，构建规划提示词
        String systemPrompt = buildPlannerPrompt();

        // 拼接最终 prompt：系统上下文 + 用户需求
        String finalPrompt = StringUtils.isNotEmpty(systemContext)
                ? systemContext + "\n用户需求：" + prompt
                : prompt;

        return Mono.fromCallable(() -> {
                    var responseEntity = chatClient.prompt()
                            .system(systemPrompt)
                            .user(finalPrompt)
                            .advisors(a -> a.param(CONVERSATION_ID, sessionId))
                            .call()
                            .responseEntity(AgentPlan.class);
                    AgentPlan plan = Objects.requireNonNullElseGet(responseEntity.entity(), () -> fallbackPlan(finalPrompt));
                    plan.setOriginalPrompt(prompt);
                    // 合并 guide 类型 Skill 声明的 requires 到各步骤的 serviceIds
                    toolRuntime.enrichPlanServiceIds(plan);
                    log.info("[Planner] 规划完成, 步骤数={}, summary={}",
                            plan.getSteps() != null ? plan.getSteps().size() : 0, plan.getSummary());
                    return plan;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    log.error("[Planner] 规划失败，降级为单步执行", ex);
                    return Mono.just(fallbackPlan(finalPrompt));
                });
    }

    /**
     * 从 Memory 读取系统上下文（Vars Context + Document Context）
     * <p>
     * AgentRuntime 已在请求线程将 DocumentContext 拼接到 prompt 中，
     * 此处进一步补充 Vars Context（租户信息、业务变量），
     * 使 Planner 能感知用户身份和业务上下文。
     * </p>
     */
    private String resolveSystemContext(String sessionId) {
        try {
            ConversationMemory memory = memoryStore.getOrCreate(sessionId);
            return memory.getSystemContext();
        } catch (Exception e) {
            log.warn("[Planner] 读取 Memory 系统上下文失败 sessionId={}", sessionId, e);
            return "";
        }
    }

    /**
     * 构建规划师系统提示词，包含可用服务列表和动作摘要
     * <p>
     * 重构后从 {@link CapabilityEngine#getAvailableActions()} 动态获取动作列表，
     * 按服务分组展示。相比旧版从 {@code ServiceConstants.MfService.values()} 硬编码枚举获取，
     * 新版自动反映已注册的子引擎状态（如某服务未启动则不出现在列表中）。
     * </p>
     */
    private String buildPlannerPrompt() {
        // 从 CapabilityEngine 获取所有动作，按 serviceId 分组
        List<ActionDefinition> actions = capabilityEngine.getAvailableActions();
        Map<String, List<ActionDefinition>> actionsByService = groupActionsByService(actions);

        // 构建服务列表（仅包含有动作的服务）
        StringBuilder serviceList = new StringBuilder();
        for (Map.Entry<String, List<ActionDefinition>> entry : actionsByService.entrySet()) {
            String serviceId = entry.getKey();
            List<ActionDefinition> serviceActions = entry.getValue();
            serviceList.append(String.format("  - %s: %d 个可用动作", serviceId, serviceActions.size()));

            // 展示前 3 个动作名作为示例，帮助 LLM 理解服务能力
            List<String> sampleNames = serviceActions.stream()
                    .limit(3)
                    .map(ActionDefinition::getName)
                    .toList();
            if (!sampleNames.isEmpty()) {
                serviceList.append("（示例: ").append(String.join(", ", sampleNames)).append("）");
            }
            serviceList.append("\n");
        }

        // 如果没有动作，使用兜底提示
        if (serviceList.isEmpty()) {
            serviceList.append("  （当前无可用服务，请直接回答用户问题）\n");
        }

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
                8. 服务列表中 skill-* 开头的是「操作指南」服务：若本步涉及某个业务域（如需求、版本、排期、周报 → skill-project-management；
                   字典 → skill-dict-guide；权限、账号、组织 → skill-permission-guide；请假 → skill-leave-apply 等），
                   必须把对应的 skill-* 一并写入该步 serviceIds，执行时才能读到该领域的操作指南。
                   选中 guide 后系统会自动补齐它 requires 声明的依赖业务服务，无需再手动列出这些依赖。
                9. 【重要】serviceIds 只写本步真正需要的服务，一般 1-3 个即可；
                   严禁把可用服务列表里的服务全部列上——工具越多越容易选错、越慢、越贵。
                10. 【关键】如果用户的请求是纯闲聊、讲笑话、问候、寒暄、或任何不需要调用平台工具
                   即可直接回答的通用内容（如"讲个笑话""你好""今天天气怎么样""什么是低代码"），
                   请不要规划任何步骤，直接返回空步骤数组 steps: []（不要返回任何 serviceIds）。
                   只有确实需要查询/操作平台数据（如查用户、建需求、走审批、跳转页面）时才写 serviceIds。

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
     * 将动作列表按 serviceId 分组（保持注册顺序）
     */
    private Map<String, List<ActionDefinition>> groupActionsByService(List<ActionDefinition> actions) {
        Map<String, List<ActionDefinition>> grouped = new LinkedHashMap<>();
        if (actions == null || actions.isEmpty()) {
            return grouped;
        }
        for (ActionDefinition action : actions) {
            String serviceId = action.getServiceId() != null ? action.getServiceId() : "unknown";
            grouped.computeIfAbsent(serviceId, k -> new java.util.ArrayList<>()).add(action);
        }
        return grouped;
    }

    /**
     * 规划失败时的兜底：单步执行原始需求，聚合所有已注册服务
     * <p>
     * 重构后从 {@link CapabilityEngine#getAvailableActions()} 提取所有 serviceId，
     * 相比旧版从 {@code ServiceConstants.MfService.values()} 硬编码枚举获取，
     * 新版仅聚合实际有动作的服务，避免向未启动的服务发送请求。
     * </p>
     */
    private AgentPlan fallbackPlan(String prompt) {
        Set<String> allServiceIds = capabilityEngine.getAvailableActions().stream()
                .map(ActionDefinition::getServiceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<String> serviceIdList = List.copyOf(allServiceIds);
        return new AgentPlan()
                .setOriginalPrompt(prompt)
                .setSummary("规划降级：直接执行")
                .setSteps(List.of(
                        new PlanStep(prompt, serviceIdList)
                ));
    }

    /**
     * 按指定租户ID构建 ChatClient
     * <p>
     * 不设置 defaultSystem，因为 {@link #plan} 方法中通过 {@code .system(systemPrompt)}
     * 显式设置规划提示词，会覆盖 defaultSystem。
     * </p>
     *
     * @param tenantId 租户ID
     * @return 该租户对应的 ChatClient
     */
    private ChatClient getChatClient(String tenantId) {
        return ChatClient.builder(llmModelRouter.getChatModel(tenantId))
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
