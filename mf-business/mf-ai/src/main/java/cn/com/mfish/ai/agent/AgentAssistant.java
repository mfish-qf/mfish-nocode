package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.engine.ApiToolEngine;
import cn.com.mfish.common.core.constants.ServiceConstants;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通用智能体助手
 * <p>
 * 自主规划+多轮工具调用场景下的工具调用执行后端：
 * <ul>
 *   <li>不绑定单一 serviceId，可按服务集合聚合工具</li>
 *   <li>系统提示词为通用智能体角色，由 Planner 决策具体步骤</li>
 *   <li>Bean 名为 "agentAssistant"，Executor 通过 @Qualifier 精确注入</li>
 * </ul>
 * </p>
 * <p>
 * 路径标识为 "/agent/executor"，不暴露到前端路由表，
 * 仅在 Executor 按步骤调用时使用。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
@Component
public class AgentAssistant extends BaseAssistant {

    /** 默认聚合所有已注册服务的工具 */
    private static final Set<String> ALL_SERVICES = Arrays.stream(ServiceConstants.MfService.values())
            .map(ServiceConstants.MfService::getValue)
            .collect(Collectors.toUnmodifiableSet());

    public AgentAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter, ApiToolEngine apiToolEngine) {
        super(chatMemory, llmModelRouter, apiToolEngine);
    }

    @Override
    protected String getSystemPrompt() {
        return """
                你是"摸鱼低代码"的智能助手，能够调用工具完成用户请求。
                你会收到一个明确的任务步骤，请调用合适的工具完成这个步骤。
                如果工具返回结果不足以回答，可以继续调用其他工具补充信息。
                请基于工具返回的真实数据回答，不要编造。
                请使用中文回答。
                """;
    }

    /**
     * 默认聊天入口：聚合所有服务工具
     */
    @Override
    public Flux<ChatResponse> chat(String sessionId, String prompt) {
        return chatWithTools(sessionId, prompt, ALL_SERVICES);
    }

    @Override
    public String getPath() {
        return "/agent/executor";
    }
}
