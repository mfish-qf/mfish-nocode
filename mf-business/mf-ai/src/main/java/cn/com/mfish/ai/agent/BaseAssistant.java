package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.FileParseService;
import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.client.IClientAssistant;
import cn.com.mfish.common.ai.engine.ApiToolEngine;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.ai.agent.ToolCapable;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
public abstract class BaseAssistant implements IClientAssistant, ToolCapable {
    private final ChatMemory chatMemory;
    private final LlmModelRouter llmModelRouter;
    protected final ApiToolEngine apiToolEngine;

    /**
     * 文件解析服务：用于根据AiRequest.fileIds获取文件内容并注入提示词
     * 采用字段注入避免修改所有子类构造函数
     */
    @Autowired
    protected FileParseService fileParseService;

    public BaseAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter, ApiToolEngine apiToolEngine) {
        this.llmModelRouter = llmModelRouter;
        this.chatMemory = chatMemory;
        this.apiToolEngine = apiToolEngine;
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
        return getChatClient(tenantId);
    }

    /**
     * 按指定租户ID构建 ChatClient
     * <p>
     * 用于异步编排场景：请求线程捕获租户ID后传入，避免在异步线程调用 currentTenantId() 失败。
     * </p>
     *
     * @param tenantId 租户ID
     * @return 该租户对应的 ChatClient
     */
    protected ChatClient getChatClient(String tenantId) {
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
        // 在请求线程捕获 token，供 HttpToolCallback/FeignToolCallback 在工具执行线程复用
        // token 不带 "Bearer " 前缀，由各 ToolCallback 在注入 header 时按需添加
        String accessToken = AuthInfoUtils.getAccessToken();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServerWebExchange serverWebExchange = ServletUtils.getExchange();
        return buildToolContextFromSnapshot(tenantId, userId, accessToken,
                requestAttributes, serverWebExchange);
    }

    /**
     * 从预捕获的租户上下文快照构建工具上下文
     * <p>
     * 用于异步编排场景：请求线程先捕获 {@link cn.com.mfish.common.ai.agent.TenantContext}，
     * 异步线程调用此方法构建 ToolContext，避免直接调用 AuthInfoUtils。
     * </p>
     *
     * @param ctx 租户上下文快照
     * @return 工具上下文Map
     */
    protected Map<String, Object> buildToolContext(cn.com.mfish.common.ai.agent.TenantContext ctx) {
        String tenantId = ctx != null ? ctx.getTenantId() : null;
        String userId = ctx != null ? ctx.getUserId() : null;
        String accessToken = ctx != null ? ctx.getAccessToken() : null;
        Object reqAttr = ctx != null ? ctx.getRequestAttributes() : null;
        Object exchange = ctx != null ? ctx.getServerWebExchange() : null;
        return buildToolContextFromSnapshot(tenantId, userId, accessToken, reqAttr, exchange);
    }

    /**
     * 从快照字段组装工具上下文 Map
     */
    private Map<String, Object> buildToolContextFromSnapshot(String tenantId, String userId, String accessToken,
                                                             Object requestAttributes, Object serverWebExchange) {
        Map<String, Object> toolContextMap = new HashMap<>();
        toolContextMap.put(RPCConstants.REQ_USER_ID, userId != null ? userId : "");
        toolContextMap.put(RPCConstants.REQ_TENANT_ID, tenantId != null ? tenantId : AuthInfoUtils.SUPER_TENANT_ID);
        toolContextMap.put(RPCConstants.REQ_ORIGIN, RPCConstants.AI);
        if (accessToken != null && !accessToken.isEmpty()) {
            toolContextMap.put(RPCConstants.REQ_TOKEN, accessToken);
        }
        if (requestAttributes instanceof RequestAttributes ra) {
            toolContextMap.put(RPCConstants.REQ_REQUEST_ATTRIBUTES, ra);
        }
        if (serverWebExchange instanceof ServerWebExchange swe) {
            toolContextMap.put(RPCConstants.REQ_SERVER_WEB_EXCHANGE, swe);
        }
        return toolContextMap;
    }

    /**
     * 基于工具的流式聊天模板方法（单服务重载）
     * <p>
     * 子类常用入口：传入单个 serviceId，内部委托给 {@link #chatWithTools(String, String, Set)}。
     * </p>
     *
     * @param sessionId 会话id
     * @param prompt    提示词
     * @param serviceId 服务ID
     * @return 流式聊天响应
     */
    protected Flux<ChatResponse> chatWithTools(String sessionId, String prompt, String serviceId) {
        return chatWithTools(sessionId, prompt, Collections.singleton(serviceId));
    }

    /**
     * 基于工具的流式聊天模板方法（多服务聚合重载）
     * <p>
     * 自主规划场景下，Executor 按计划步骤指定的服务集合调用此方法。
     * 内部从 ApiToolEngine 聚合对应服务的工具，供 LLM 选择。
     * </p>
     * <p>
     * 本重载从当前请求线程获取租户信息，适用于同步请求链路。
     * 异步编排场景请使用 {@link #chatWithTools(String, String, Set, cn.com.mfish.common.ai.agent.TenantContext)}。
     * </p>
     *
     * @param sessionId  会话id
     * @param prompt    提示词
     * @param serviceIds 需要聚合工具的微服务ID集合
     * @return 流式聊天响应
     */
    @Override
    public Flux<ChatResponse> chatWithTools(String sessionId, String prompt, Set<String> serviceIds) {
        return chatWithTools(sessionId, prompt, serviceIds, null);
    }

    /**
     * 基于工具的流式聊天模板方法（带租户上下文重载）
     * <p>
     * 异步编排场景下专用：请求线程先捕获 {@link cn.com.mfish.common.ai.agent.TenantContext} 快照，
     * 异步线程用此快照构建 ChatClient 和 ToolContext，避免调用 AuthInfoUtils 时拿不到 RequestAttributes。
     * </p>
     * <p>
     * 当 {@code tenantContext} 为 null 时，回退到从当前线程获取租户信息（兼容同步请求链路）。
     * </p>
     * <p>
     * 系统提示词中注入工具使用规则，减少 LLM 幻觉工具名的概率：
     * <ol>
     *     <li>只能使用提供的工具，不能虚构工具名</li>
     *     <li>先分析用户意图，再选择最合适的工具</li>
     *     <li>工具调用失败时根据错误信息调整参数或选择其他工具</li>
     * </ol>
     * </p>
     *
     * @param sessionId       会话id
     * @param prompt          提示词
     * @param serviceIds      需要聚合工具的微服务ID集合
     * @param tenantContext   请求线程捕获的租户上下文快照，null 表示从当前线程获取
     * @return 流式聊天响应
     */
    @Override
    public Flux<ChatResponse> chatWithTools(String sessionId, String prompt, Set<String> serviceIds,
                                            cn.com.mfish.common.ai.agent.TenantContext tenantContext) {
        ToolCallbackProvider toolProvider = apiToolEngine.getToolCallbackProvider(serviceIds);
        Map<String, Object> toolContextMap = tenantContext != null
                ? buildToolContext(tenantContext)
                : buildToolContext();
        // 按租户上下文构建 ChatClient，避免在异步线程调用 currentTenantId()
        String tenantId = tenantContext != null && tenantContext.getTenantId() != null
                ? tenantContext.getTenantId()
                : llmModelRouter.currentTenantId();
        ChatClient chatClient = getChatClient(tenantId);
        // 构建工具使用提示词，减少LLM幻觉
        String toolHint = buildToolUsageHint(toolProvider);
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .system(getSystemPrompt() + "\n\n" + toolHint)
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
     * 构建工具使用提示词，帮助LLM理解可用工具，减少幻觉
     */
    private String buildToolUsageHint(ToolCallbackProvider toolProvider) {
        org.springframework.ai.tool.ToolCallback[] callbacks = toolProvider.getToolCallbacks();
        if (callbacks == null || callbacks.length == 0) {
            return "## 工具使用规则\n当前没有可用的工具，请直接根据你的知识回答用户问题。";
        }
        StringBuilder sb = new StringBuilder("## 工具使用规则\n");
        sb.append("你只能使用以下工具，不能虚构任何工具名：\n");
        for (org.springframework.ai.tool.ToolCallback tc : callbacks) {
            String name = tc.getToolDefinition().name();
            String desc = tc.getToolDefinition().description();
            sb.append("- ").append(name);
            if (desc != null && !desc.isEmpty()) {
                sb.append(": ").append(desc);
            }
            sb.append("\n");
        }
        sb.append("\n调用规则：\n");
        sb.append("1. 先分析用户意图，从上述工具列表中选择最合适的一个\n");
        sb.append("2. 只能使用上述列表中的工具名，不要构造新的工具名\n");
        sb.append("3. 如果没有合适的工具，直接用你的知识回答\n");
        sb.append("4. 工具调用失败时，根据返回的错误信息调整参数或选择其他工具\n");
        return sb.toString();
    }

    /**
     * 聊天返回id
     * <p>
     * 若请求携带fileIds，会先通过FileParseService从文件服务获取文件内容，
     * 将其拼接到用户提示词前，再交由子类chat(sessionId, prompt)处理。
     *
     * @return 聊天信息
     */
    @Override
    public Flux<ChatResponseVo> chat(AiRequest aiRequest) {
        String prompt = aiRequest.getMessage().getContent();
        List<String> fileIds = aiRequest.getFileIds();
        if (fileIds != null && !fileIds.isEmpty()) {
            String fileContents = fileParseService.loadFileContents(fileIds);
            if (StringUtils.isNotEmpty(fileContents)) {
                prompt = "以下是用户上传的文件内容，请基于文件内容进行分析：\n\n"
                        + fileContents
                        + "\n用户问题：" + prompt;
            }
        }
        final String finalPrompt = prompt;
        return chat(aiRequest.getSessionId(), finalPrompt)
                .filter(resp -> "STOP".equals(Objects.requireNonNull(resp.getResult()).getMetadata().getFinishReason())
                        || StringUtils.isNotEmpty(resp.getResult().getOutput().getText()))
                .map(resp -> new ChatResponseVo().setId(aiRequest.getId())
                        .setContent(Objects.requireNonNull(resp.getResult()).getOutput().getText())
                        .setFinishReason(resp.getResult().getMetadata().getFinishReason())
                );
    }
}
