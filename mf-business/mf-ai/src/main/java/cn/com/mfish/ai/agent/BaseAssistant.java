package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.client.IClientAssistant;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.ai.feign.FeignToolRegistry;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @description: 助手基类
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
@Slf4j
public abstract class BaseAssistant implements IClientAssistant {
    private final ChatMemory chatMemory;
    private final LlmModelRouter llmModelRouter;
    protected final FeignToolRegistry feignToolRegistry;

    public BaseAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter, FeignToolRegistry feignToolRegistry) {
        this.llmModelRouter = llmModelRouter;
        this.chatMemory = chatMemory;
        this.feignToolRegistry = feignToolRegistry;
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
     * 构建工具上下文：捕获当前请求的认证信息和请求上下文（双栈兼容 Servlet + WebFlux）
     * <p>
     * 供FeignToolCallback在工具执行线程恢复请求上下文，使BearerTokenInterceptor和AuthInfoUtils正常工作。
     *
     * @return 工具上下文Map
     */
    protected Map<String, Object> buildToolContext() {
        String userId = AuthInfoUtils.getCurrentUserId();
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServerWebExchange serverWebExchange = ServletUtils.getExchange();
        Map<String, Object> toolContextMap = new HashMap<>();
        toolContextMap.put("userId", userId != null ? userId : "");
        toolContextMap.put("tenantId", tenantId != null ? tenantId : AuthInfoUtils.SUPER_TENANT_ID);
        toolContextMap.put("origin", RPCConstants.INNER);
        if (requestAttributes != null) {
            toolContextMap.put("requestAttributes", requestAttributes);
        }
        if (serverWebExchange != null) {
            toolContextMap.put("serverWebExchange", serverWebExchange);
        }
        return toolContextMap;
    }

    /**
     * 基于Feign工具的流式聊天模板方法
     * <p>
     * 封装ToolContext构建、ChatClient请求构建、流式降级等公共逻辑，
     * 子类只需提供serviceId即可。
     *
     * @param sessionId 会话id
     * @param prompt    提示词
     * @param serviceId 服务ID，用于从FeignToolRegistry获取对应的ToolCallbackProvider
     * @return 流式聊天响应
     */
    protected Flux<ChatResponse> chatWithTools(String sessionId, String prompt, String serviceId) {
        ToolCallbackProvider toolProvider = feignToolRegistry.getToolCallbackProvider(serviceId);
        Map<String, Object> toolContextMap = buildToolContext();
        ChatClient.ChatClientRequestSpec requestSpec = this.getChatClient().prompt()
                .system("你必须先调用工具，再基于工具结果回答问题")
                .user(prompt)
                .advisors(a -> a.param(CONVERSATION_ID, sessionId))
                .tools(toolProvider)
                .toolContext(toolContextMap);
        return requestSpec
                .stream()
                .chatResponse()
                .onErrorResume(e -> {
                    log.warn("流式调用失败，降级为非流式调用: {}", e.getMessage());
                    return Mono.fromCallable(() -> requestSpec.call().chatResponse())
                            .subscribeOn(Schedulers.boundedElastic())
                            .flux();
                });
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
