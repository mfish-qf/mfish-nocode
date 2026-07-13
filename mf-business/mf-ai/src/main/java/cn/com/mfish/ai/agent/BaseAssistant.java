package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.client.IClientAssistant;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.core.utils.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * @description: 系统中心助手
 * <p>
 * 按租户路由模型：每次请求实时解析租户、从 LlmModelRouter 取该租户的ChatModel
 * （ChatModel按配置签名缓存共享，不随租户增长），并实时构建ChatClient。
 * 不缓存ChatClient，避免为每个租户常驻一份系统提示词+advisor带来的内存膨胀。
 * <p>
 * 线程安全：ChatClient每次请求独立构建、用完即弃，无共享可变状态；
 * 模型在请求线程解析（AuthInfoUtils不支持异步），构建后的引用可安全用于reactive链。
 *
 * @author: mfish
 * @date: 2025/8/22
 */
public abstract class BaseAssistant implements IClientAssistant {
    private final ChatMemory chatMemory;
    private final LlmModelRouter llmModelRouter;

    public BaseAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter) {
        this.llmModelRouter = llmModelRouter;
        this.chatMemory = chatMemory;
    }

    /**
     * 子类提供系统提示词
     */
    protected abstract String getSystemPrompt();

    /**
     * 实时构建当前请求租户对应的ChatClient：从路由取该租户ChatModel，包装系统提示词与advisor。
     * 不缓存——ChatClient构建本身轻量（仅包装ChatModel与提示词字符串，不重建HTTP客户端），
     * 实时构建可避免为每个租户常驻一份提示词内存。必须在请求线程调用（AuthInfoUtils不支持异步）。
     */
    protected ChatClient getChatClient() {
        String tenantId = llmModelRouter.currentTenantId();
        return ChatClient.builder(llmModelRouter.getChatModel(tenantId))
                .defaultSystem(getSystemPrompt())
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 聊天返回id
     *
     * @return 聊天信息
     */
    @Override
    public Flux<ChatResponseVo> chat(AiRequest aiRequest) {
        return chat(aiRequest.getSessionId(), aiRequest.getMessage().getContent())
                .filter(resp -> "STOP".equals(Objects.requireNonNull(resp.getResult()).getMetadata().getFinishReason())
                        || StringUtils.isNotEmpty(resp.getResult().getOutput().getText()))
                .map(resp -> new ChatResponseVo().setId(aiRequest.getId())
                        .setContent(Objects.requireNonNull(resp.getResult()).getOutput().getText())
                        .setFinishReason(resp.getResult().getMetadata().getFinishReason())
                );
    }
}
