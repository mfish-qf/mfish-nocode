package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.FileParseService;
import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.ai.runtime.ToolRuntime;
import cn.com.mfish.common.ai.client.IClientAssistant;
import cn.com.mfish.common.ai.engine.ApiToolEngine;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.ai.agent.ToolCapable;
import cn.com.mfish.common.ai.memory.ConversationMemory;
import cn.com.mfish.common.ai.memory.ConversationMemoryStore;
import cn.com.mfish.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
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

    /**
     * 会话记忆存储：三驾马车 Memory 模块的入口
     * <p>
     * 用于按 sessionId 获取 {@link ConversationMemory}，将文件解析结果、租户上下文、
     * 业务变量等统一收纳到 Memory，供后续 Planner 拼接上下文。
     * </p>
     * <p>
     * 字段注入避免修改所有子类构造函数。
     * </p>
     */
    @Autowired
    protected ConversationMemoryStore memoryStore;

    @Autowired
    protected ToolRuntime toolRuntime;

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
     * 无工具纯对话流式聊天（零工具注入）。
     * <p>
     * 用于「用户问题无需任何平台工具即可回答」的场景（如讲笑话、问候、通用知识问答），
     * 避免把几百个业务工具塞进上下文。双流降级逻辑与 {@link #chatWithTools} 一致。
     * </p>
     *
     * @param sessionId       会话id
     * @param prompt         提示词
     * @param tenantContext  请求线程捕获的租户上下文快照，null 表示从当前线程获取
     * @return 流式聊天响应
     */
    protected Flux<ChatResponse> chatNoTools(String sessionId, String prompt,
                                             cn.com.mfish.common.ai.agent.TenantContext tenantContext) {
        String tenantId = tenantContext != null && tenantContext.getTenantId() != null
                ? tenantContext.getTenantId()
                : llmModelRouter.currentTenantId();
        ChatClient chatClient = getChatClient(tenantId);
        if (log.isInfoEnabled()) {
            log.info("[BaseAssistant] chatNoTools sessionId={} (纯对话，无工具)", sessionId);
        }
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(CONVERSATION_ID, sessionId))
                .stream()
                .chatResponse()
                .onErrorResume(e -> {
                    log.warn("无工具流式调用失败，降级为非流式调用: {}", e.getMessage());
                    return Mono.fromCallable(() -> chatClient.prompt()
                                    .user(prompt)
                                    .advisors(a -> a.param(CONVERSATION_ID, sessionId))
                                    .call()
                                    .chatResponse())
                            .subscribeOn(Schedulers.boundedElastic())
                            .flux();
                });
    }

    /**
     * 带扩展工具的流式聊天模板方法（按主服务收敛后的工具集）
     * <p>
     * 在垂直助手中使用：{@code ToolRuntime.resolveAssistantServiceIds(mainServiceId)} 只注入
     * 「主服务 + frontend + requires 命中该主服务的 guide（及其依赖）」，
     * 不再聚合全平台的 MCP / Skill / 其它业务服务工具（旧实现每助手约 290 个）。
     * </p>
     * <p>
     * guide 类型 Skill 在 frontmatter 通过 requires 声明依赖的业务服务（如 mf-demo）。
     * 选中相关 guide 后合并其 requires，使 LLM 读取指南后仍能调用指南引用的业务工具完成实际操作，
     * 与 agent 模式 Planner + enrichPlanServiceIds 的按步装配口径一致。
     * </p>
     *
     * @param sessionId      会话id
     * @param prompt         提示词
     * @param mainServiceId  主服务ID（垂直助手所属的微服务ID，如 SYS_SERVICE）
     * @return 流式聊天响应
     */
    protected Flux<ChatResponse> chatWithToolsAndExtensions(String sessionId, String prompt, String mainServiceId) {
        return chatWithTools(sessionId, prompt, toolRuntime.resolveAssistantServiceIds(mainServiceId));
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
        ToolCallbackProvider toolProvider = toolRuntime.getToolCallbackProvider(serviceIds);
        // 诊断日志：输出当前步骤注入的工具数量和名称，便于排查 LLM "无法调用工具" 问题
        org.springframework.ai.tool.ToolCallback[] diagnosticCallbacks = toolProvider.getToolCallbacks();
        if (log.isInfoEnabled()) {
            StringBuilder toolNames = new StringBuilder();
            for (int i = 0; i < diagnosticCallbacks.length; i++) {
                if (i > 0) toolNames.append(", ");
                toolNames.append(diagnosticCallbacks[i].getToolDefinition().name());
            }
            log.info("[BaseAssistant] chatWithTools sessionId={} serviceIds={} toolCount={} tools=[{}]",
                    sessionId, serviceIds, diagnosticCallbacks.length, toolNames);
        }
        Map<String, Object> toolContextMap = toolRuntime.buildToolContext(sessionId, tenantContext);
        // 按租户上下文构建 ChatClient，避免在异步线程调用 currentTenantId()
        String tenantId = tenantContext != null && tenantContext.getTenantId() != null
                ? tenantContext.getTenantId()
                : llmModelRouter.currentTenantId();
        ChatClient chatClient = getChatClient(tenantId);
        // 构建工具使用提示词，减少LLM幻觉
        String toolHint = toolRuntime.buildToolUsageHint(toolProvider);
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
     * 聊天返回id
     * <p>
     * 重构后流程（接入 Memory 模块）：
     * <ol>
     *   <li>获取（或创建）sessionId 对应的 {@link ConversationMemory}</li>
     *   <li>若请求携带 fileIds：通过 FileParseService 解析为 DocumentChunk 列表，
     *       注入到 Memory 的 Document Context（替代旧的字符串拼接）</li>
     *   <li>从 Memory 读取 Document Context 拼接文本，注入到用户 prompt 前</li>
     *   <li>交由子类 chat(sessionId, prompt) 处理（保持向后兼容）</li>
     * </ol>
     * <p>
     * 关键约束：文件解析必须在请求线程执行（Feign BearerTokenInterceptor 依赖
     * RequestContextHolder），Memory 写入也在请求线程完成（保证一致性）。
     *
     * @return 聊天信息
     */
    @Override
    public Flux<ChatResponseVo> chat(AiRequest aiRequest) {
        String sessionId = aiRequest.getSessionId();
        String prompt = aiRequest.getMessage().getContent();
        ConversationMemory memory = memoryStore.getOrCreate(sessionId);

        // 文件解析 + 注入 Memory 的 Document Context
        List<String> fileIds = aiRequest.getFileIds();
        if (fileIds != null && !fileIds.isEmpty()) {
            List<cn.com.mfish.common.ai.memory.DocumentChunk> chunks = fileParseService.loadAsChunks(fileIds);
            if (!chunks.isEmpty()) {
                memory.addDocumentChunks(chunks);
            }
        }

        // 从 Memory 读取文档上下文，拼接到 prompt 前
        // 当前使用 getDocumentContext() 全量注入；当文档较多、token 预算紧张时，
        // 未来可切换为 memory.searchDocumentContext(prompt, 5) 走 RAG 检索（向量库版 Memory 覆写此方法）。
        String documentContext = memory.getDocumentContext();
        if (StringUtils.isNotEmpty(documentContext)) {
            prompt = "以下是用户上传的文件内容，请基于文件内容进行分析：\n\n"
                    + documentContext
                    + "\n用户问题：" + prompt;
        }

        // 在用户 prompt 前注入当前日期，防止 LLM 使用训练数据中的过期日期
        // LLM 对 user message 的关注度高于 system message，在此注入比仅靠系统提示词更可靠
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate tomorrow = today.plusDays(1);
        java.time.LocalDate dayAfter = today.plusDays(2);
        prompt = "【当前真实日期】今天=" + today + "，明天=" + tomorrow + "，后天=" + dayAfter
                + "。用户提到相对日期时必须使用这些日期，严禁使用你训练数据中的日期。\n\n用户输入：" + prompt;

        final String finalPrompt = prompt;
        final String messageId = aiRequest.getId();
        // 清理上次请求可能残留的前端操作指令和 emitter
        toolRuntime.resetFrontendActions(sessionId);

        // 使用 Flux.create + emitter 回调实现 FRONTEND_ACTION 分通道下发：
        // - navigate/click/fill/openModal：实时下发（工具调用时立即通过 emitter 发射）
        // - refresh：延迟下发（存入 list，文本流完成后 drain 发射）
        // 最终顺序：navigate → 业务工具 → 文本 → refresh → STOP
        // 单一 Flux 源，不触发额外的 async dispatch，避免 Spring Security 异常
        return Flux.<ChatResponseVo>create(sink -> {
            // 注册 emitter：非 refresh 的操作实时发射到 sink
            toolRuntime.registerFrontendEmitter(sessionId, fa -> {
                String json = com.alibaba.fastjson2.JSON.toJSONString(fa);
                log.info("[BaseAssistant] 实时下发前端操作: {} {}", fa.getAction(), fa.getTarget());
                sink.next(new ChatResponseVo()
                        .setId(messageId)
                        .setType(cn.com.mfish.common.ai.entity.EventType.FRONTEND_ACTION)
                        .setContent(json));
            });

            chat(sessionId, finalPrompt)
                    // 只保留有文本内容的响应（STOP 空响应在最后统一发射）。
                    // usage-only / 工具调用等中间 chunk 可能没有 output 或 metadata，
                    // 直接取会 NPE 触发 sink.error，把整段回复掐断，这里逐层判空跳过。
                    .filter(resp -> resp != null && resp.getResult() != null
                            && resp.getResult().getOutput() != null
                            && StringUtils.isNotEmpty(resp.getResult().getOutput().getText()))
                    .map(resp -> new ChatResponseVo().setId(messageId)
                            .setContent(resp.getResult().getOutput().getText())
                            .setFinishReason(resp.getResult().getMetadata() != null
                                    ? resp.getResult().getMetadata().getFinishReason() : null))
                    .subscribe(
                            sink::next,
                            sink::error,
                            () -> {
                                // 主流完成：注销 emitter
                                toolRuntime.unregisterFrontendEmitter(sessionId);
                                // 下发延迟的 refresh 操作（文本之后、STOP 之前）
                                List<cn.com.mfish.common.ai.entity.FrontendAction> deferredActions =
                                        toolRuntime.drainFrontendActions(sessionId);
                                for (cn.com.mfish.common.ai.entity.FrontendAction fa : deferredActions) {
                                    String json = com.alibaba.fastjson2.JSON.toJSONString(fa);
                                    log.info("[BaseAssistant] 延迟下发前端操作: {} {}", fa.getAction(), fa.getTarget());
                                    sink.next(new ChatResponseVo()
                                            .setId(messageId)
                                            .setType(cn.com.mfish.common.ai.entity.EventType.FRONTEND_ACTION)
                                            .setContent(json));
                                }
                                // 发射 STOP 信号
                                sink.next(new ChatResponseVo().setId(messageId).setFinishReason("STOP"));
                                sink.complete();
                            }
                    );
        }, reactor.core.publisher.FluxSink.OverflowStrategy.BUFFER)
                .doFinally(signal -> {
                    toolRuntime.unregisterFrontendEmitter(sessionId);
                    toolRuntime.clearFrontendActions(sessionId);
                    log.info("[BaseAssistant] 请求结束 sessionId={} signal={}", sessionId, signal);
                });
    }

}

