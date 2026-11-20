package cn.com.mfish.ai.service;

import cn.com.mfish.ai.api.entity.AiModelConfig;
import cn.com.mfish.ai.api.req.ReqAiModelConfig;
import cn.com.mfish.ai.event.AiModelChangedEvent;
import cn.com.mfish.common.ai.service.AiModelConfigService;
import cn.com.mfish.common.ai.service.LlmModelFactory;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LLM多模型路由服务
 * <p>
 * 按租户维度路由：优先使用各租户在 ai_model_config 表中配置的启用模型；
 * 租户未配置任何模型时，回退到 Spring AI 默认 ChatModel（openaiChatModel，单实例来自 yml、微服务来自 nacos 配置）。
 * 每个租户拥有独立的ChatModel列表，按sortOrder决定fallback优先级。
 * <p>
 * 创建性能优化：通过配置签名缓存已构建的ChatModel，重建时仅构建新增/变更的配置，
 * 未变更的ChatModel直接复用，避免重复创建HTTP客户端等昂贵对象。
 * <p>
 * 线程安全：租户模型表为 volatile 不可变快照，读操作无锁；重建整体替换引用。
 *
 * @author: mfish
 * @date: 2026/07/01
 */
@Service
@Slf4j
public class LlmModelRouter {
    private final ChatModel openaiChatModel;
    private final AiModelConfigService aiModelConfigService;
    /**
     * 租户 → 该租户可用的ChatModel列表（不可变快照）。
     * 通过 volatile 发布，读操作无锁且线程安全；写操作整体替换引用。
     */
    private volatile Map<String, List<ChatModel>> tenantProviders = Map.of();
    /**
     * 配置签名 → 已构建ChatModel 的缓存（仅在 rebuildLock 内访问）。
     * 重建时优先复用未变更配置对应的ChatModel，避免重复构建HTTP客户端等昂贵对象。
     * 被发布到快照的ChatModel对象即使后续从缓存移除，也不影响正在使用它的请求（它们持有对象引用）。
     */
    private final Map<String, ChatModel> modelCache = new HashMap<>();
    /**
     * ChatModel → 脱敏后的 apiKey 快照，与 tenantProviders 一起重建并 volatile 发布。
     * 仅供日志使用，便于确认「本次任务实际调用了哪个模型、用的哪把 key」，绝不存明文。
     */
    private volatile Map<ChatModel, String> maskedKeys = Map.of();
    /**
     * 串行化重建动作。读操作不获取此锁，因此重建期间读取线程仍可使用旧快照，不受阻塞。
     */
    private final ReentrantLock rebuildLock = new ReentrantLock();
    /**
     * 单个请求内模型调用失败后自动切换模型的最大尝试次数。
     * 按租户 provider 列表顺序循环尝试，命中限流(429/TPM)等异常时切换到下一个模型重试。
     */
    private static final int MAX_FALLBACK_ATTEMPTS = 3;
    /**
     * 流式响应不合规的 ChatModel（网关/服务商返回的 SSE chunk 缺 model / tool_calls.index 等必填字段）。
     * 一旦探测到，该模型后续一律走非流式 call()：非流式是单个完整 ChatCompletion 对象，
     * 不经过 ChunkMerger，从根上消除「输出一半就结束」「工具调用丢失」。
     * 用身份集合（模型实例来自 modelCache，长期复用）；模型配置变更会重建实例，自动重新探测。
     */
    private final Set<ChatModel> streamIncompatibleModels = ConcurrentHashMap.newKeySet();

    public LlmModelRouter(ChatModel openAiChatModel, AiModelConfigService aiModelConfigService) {
        this.openaiChatModel = openAiChatModel;
        this.aiModelConfigService = aiModelConfigService;
    }

    @PostConstruct
    public void initProviders() {
        doInitProviders();
    }

    /**
     * 监听数据库模型配置变更事件，重新加载模型。
     * 助手不再缓存ChatClient（每请求实时构建），因此重载后无需通知助手刷新；
     * 下次请求自动取到新的ChatModel。
     */
    @EventListener
    public void onModelChanged(AiModelChangedEvent event) {
        log.info("[LLM路由] 收到模型变更事件，开始重新初始化");
        try {
            doInitProviders();
        } catch (Exception e) {
            log.error("[LLM路由] 重新初始化失败，保持现有模型配置", e);
            return;
        }
        log.info("[LLM路由] 模型重新初始化完成");
    }

    private void doInitProviders() {
        rebuildLock.lock();
        try {
            List<AiModelConfig> configs = aiModelConfigService.queryList(new ReqAiModelConfig().setEnabled(1));
            Map<String, List<ChatModel>> newMap = new HashMap<>();
            Map<ChatModel, String> newMaskedKeys = new HashMap<>();
            Set<String> usedSignatures = new HashSet<>();
            if (configs != null) {
                for (AiModelConfig config : configs) {
                    if (1 != config.getEnabled()) {
                        continue;
                    }
                    String signature = signatureOf(config);
                    if (!usedSignatures.add(signature)) {
                        continue; // 同签名去重
                    }
                    try {
                        // 签名命中缓存则复用已构建的ChatModel，避免重复创建昂贵的HTTP客户端
                        ChatModel chatModel = modelCache.computeIfAbsent(signature, k -> buildModel(config));
                        String maskedKey = maskApiKey(config.getApiKey());
                        newMaskedKeys.put(chatModel, maskedKey);
                        newMap.computeIfAbsent(config.getTenantId(), k -> new ArrayList<>()).add(chatModel);
                        log.info("[LLM路由] 注册: tenant={}, protocol={}, provider={}, model={}, key={}",
                                config.getTenantId(), config.getProtocol(), config.getProvider(),
                                config.getModelName(), maskedKey);
                    } catch (Exception e) {
                        log.error("[LLM路由] 注册失败: tenant={}, protocol={}, provider={}, model={}, key={}",
                                config.getTenantId(), config.getProtocol(), config.getProvider(),
                                config.getModelName(), maskApiKey(config.getApiKey()), e);
                    }
                }
            }
            // 构建不可变快照
            Map<String, List<ChatModel>> snapshot = new HashMap<>();
            for (Map.Entry<String, List<ChatModel>> e : newMap.entrySet()) {
                snapshot.put(e.getKey(), List.copyOf(e.getValue()));
            }
            // 清理不再使用的缓存项，释放已删除配置占用的资源
            modelCache.keySet().retainAll(usedSignatures);
            maskedKeys = Map.copyOf(newMaskedKeys);
            tenantProviders = Map.copyOf(snapshot);
            if (snapshot.isEmpty()) {
                log.warn("[LLM路由] 数据库无启用的模型配置");
            }
        } finally {
            rebuildLock.unlock();
        }
    }

    private ChatModel buildModel(AiModelConfig config) {
        return LlmModelFactory.create(
                config.getProtocol(), config.getApiKey(),
                config.getBaseUrl(), config.getModelName(),
                config.getMaxTokens(), config.getTemperature());
    }

    /**
     * 配置签名：由影响ChatModel构建的字段组成。签名不变则复用已构建的ChatModel。
     * 使用加密后的apiKey（与解密后一一对应）避免明文密钥出现在签名串中。
     */
    private String signatureOf(AiModelConfig config) {
        return config.getProtocol() + "|" + config.getModelName() + "|" + config.getBaseUrl()
                + "|" + config.getApiKey() + "|" + config.getMaxTokens() + "|" + config.getTemperature();
    }

    /**
     * 脱敏 apiKey：只保留前 4 与后 4 位，中间统一打码；短 key 全打码。
     * 仅用于日志，任何情况下都不输出完整密钥。
     */
    private static String maskApiKey(String key) {
        if (key == null || key.isBlank()) {
            return "<none>";
        }
        String v = key.trim();
        if (v.length() <= 8) {
            return "****";
        }
        return v.substring(0, 4) + "****" + v.substring(v.length() - 4);
    }

    /** 安全取模型名（getOptions 可能抛异常或返回空） */
    private static String safeModelName(ChatModel p) {
        if (p == null) {
            return "<null>";
        }
        try {
            Object m = p.getOptions().getModel();
            return m == null ? p.getClass().getSimpleName() : String.valueOf(m);
        } catch (Exception e) {
            return p.getClass().getSimpleName();
        }
    }

    /**
     * 日志用的模型描述：模型名 + 脱敏 key。
     * 绝不打印 ChatModel / ChatOptions 对象本身（其 toString 可能带出完整 apiKey）。
     */
    private String modelDesc(ChatModel p) {
        return safeModelName(p) + " [key=" + maskedKeys.getOrDefault(p, "<unknown>") + "]";
    }

    /**
     * 安全获取当前请求租户ID；无请求上下文（如启动期构造）时返回 null，回退到全局模型。
     * 注意：必须在请求线程调用，不能在reactive异步线程中调用。
     */
    public String currentTenantId() {
        try {
            return AuthInfoUtils.getCurrentTenantId();
        } catch (Exception e) {
            return AuthInfoUtils.SUPER_TENANT_ID;
        }
    }

    /**
     * 获取指定租户的ChatModel。
     * 返回的是包装了整条 fallback 链的 {@link FallbackChatModel}：单次调用会按租户 provider 列表
     * 顺序循环尝试，遇到异常（含 TPM/429 限流）自动切换到下一个模型，最多尝试 {@link #MAX_FALLBACK_ATTEMPTS} 次。
     * 这样所有经本方法构建的 ChatClient（助手/Planner/Skill/Gateway）都自动具备限流切换能力，无需各处单独处理。
     */
    public ChatModel getChatModel(String tenantId) {
        return new FallbackChatModel(tenantId);
    }

    /**
     * 解析租户可用的provider链：优先返回该租户在 ai_model_config 表中配置的启用模型；
     * 若租户未配置任何模型，返回空列表，由上层 {@link FallbackChatModel#providers()} 回退到
     * Spring AI 默认 ChatModel（openaiChatModel，单实例来自 yml、微服务来自 nacos 配置）。
     * 不再使用 tenant_id='1' 的数据库“全局配置”做跨租户兜底，避免租户间模型互相可见。
     */
    private List<ChatModel> resolveProviders(String tenantId) {
        Map<String, List<ChatModel>> snapshot = tenantProviders;
        List<ChatModel> list = snapshot.get(tenantId);
        return list == null ? List.of() : list;
    }

    /**
     * 流式调用，支持fallback链。租户在请求线程解析后传入链路，避免在reactive异步线程中获取租户。
     */
    public Flux<ChatResponse> streamWithFallback(Prompt prompt, String model) {
        List<ChatModel> providers = resolveProviders(currentTenantId());
        return tryStreamChain(prompt, 0, model, providers);
    }

    /**
     * 同步调用，支持fallback链。租户在请求线程解析后传入链路。
     */
    public Mono<ChatResponse> callWithFallback(Prompt prompt, String model) {
        List<ChatModel> providers = resolveProviders(currentTenantId());
        return tryCallChain(prompt, 0, model, providers);
    }

    private Flux<ChatResponse> tryStreamChain(Prompt prompt, int index, String model, List<ChatModel> providers) {
        ChatModel provider = getProvider(index, model, providers);
        return provider.stream(prompt)
                .onErrorResume(ex -> {
                    // 网关/服务商下发的 chunk 格式不合规（缺 model / tool_calls.index 等）时，
                    // 换任何模型都会同样失败，直接把错误交给调用方（如外部 OpenAI 兼容客户端可自行重试），
                    // 不再白跑 fallback 链、也不把截断的流伪装成正常结束。
                    if (isMissingRequiredChunkField(ex)) {
                        log.warn("[LLM路由] 模型 {} 流式 chunk 格式不合规，不切换模型直接返回错误: {}",
                                modelDesc(provider), ex.getMessage());
                        return Flux.error(ex);
                    }
                    log.warn("[LLM路由] 模型 {} 流式调用失败，尝试 fallback", modelDesc(provider), ex);
                    return tryStreamChain(prompt, index + 1, model, providers);
                });
    }

    private Mono<ChatResponse> tryCallChain(Prompt prompt, int index, String model, List<ChatModel> providers) {
        ChatModel provider = getProvider(index, model, providers);
        return Mono.fromCallable(() -> provider.call(prompt))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    log.warn("[LLM路由] 模型 {} 同步调用失败，尝试 fallback", modelDesc(provider), ex);
                    return tryCallChain(prompt, index + 1, model, providers);
                });
    }

    private ChatModel getProvider(int index, String model, List<ChatModel> providers) {
        if (index >= providers.size()) {
            throw new MyRuntimeException("所有模型均不可用");
        }
        ChatModel provider;
        if (model == null || model.isEmpty()) {
            provider = providers.get(index);
        } else {
            provider = providers.stream().filter(p -> Objects.equals(p.getOptions().getModel(), model)).findFirst().orElseGet(() -> providers.get(index));
        }
        log.info("[LLM路由] 调用模型 {}", modelDesc(provider));
        return provider;
    }

    /**
     * 包装多个 ChatModel 的路由模型：单次调用按租户 provider 列表顺序(循环)尝试，
     * 遇到任何异常（含 TPM/429 限流、网络错误）自动切换到下一个模型，最多尝试 MAX_FALLBACK_ATTEMPTS 次。
     * 把 fallback 收敛到 ChatModel 这一层后，所有经 LlmModelRouter.getChatModel 构建的 ChatClient 自动受益，
     * 无需在 BaseAssistant / Planner / Skill / Gateway 各处分别写重试逻辑。
     */
    private class FallbackChatModel implements ChatModel {
        private final String tenantId;

        FallbackChatModel(String tenantId) {
            this.tenantId = tenantId;
        }

        /** 解析该租户的可用模型链；空时回退到注入的默认 openaiChatModel，再无则抛明确异常 */
        private List<ChatModel> providers() {
            List<ChatModel> list = resolveProviders(tenantId);
            if (!list.isEmpty()) {
                return list;
            }
            if (openaiChatModel != null) {
                return List.of(openaiChatModel);
            }
            throw new MyRuntimeException("未配置任何可用模型(租户=" + tenantId + ")");
        }

        @Override
        public ChatOptions getOptions() {
            try {
                List<ChatModel> list = providers();
                return list.isEmpty() ? null : list.get(0).getOptions();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public ChatResponse call(Prompt prompt) {
            List<ChatModel> providers = providers();
            Throwable last = null;
            for (int attempt = 0; attempt < MAX_FALLBACK_ATTEMPTS; attempt++) {
                ChatModel p = providers.get(attempt % providers.size());
                try {
                    log.info("[LLM路由] 调用模型 {} mode=call attempt={}/{}",
                            modelDesc(p), attempt + 1, MAX_FALLBACK_ATTEMPTS);
                    return p.call(prompt);
                } catch (Throwable ex) {
                    last = ex;
                    log.warn("[LLM路由] 模型 {} 调用失败(第{}/{}次)，自动切换下一个模型重试",
                            modelDesc(p), attempt + 1, MAX_FALLBACK_ATTEMPTS, ex);
                }
            }
            throw new MyRuntimeException("模型调用全部失败，已尝试" + MAX_FALLBACK_ATTEMPTS + "次", last);
        }

        @Override
        public Flux<ChatResponse> stream(Prompt prompt) {
            List<ChatModel> providers = providers();
            return tryStream(prompt, 0, providers);
        }

        private Flux<ChatResponse> tryStream(Prompt prompt, int attempt, List<ChatModel> providers) {
            if (attempt >= MAX_FALLBACK_ATTEMPTS) {
                return Flux.error(new MyRuntimeException("模型流式调用全部失败，已尝试" + MAX_FALLBACK_ATTEMPTS + "次"));
            }
            ChatModel p = providers.get(attempt % providers.size());
            // 已被探测为「流式 chunk 不合规」的模型：直接走非流式 call()。
            // 非流式返回的是单个完整 ChatCompletion 对象，不经过 ChunkMerger，
            // 从根上杜绝缺 model / index 导致的半途抛错、答案截断、工具调用丢失。
            // Spring AI 的 ToolCallingAdvisor 基于聚合响应递归处理工具调用，
            // 因此 stream() 返回单个 ChatResponse 时，工具循环依然完整可用。
            if (streamIncompatibleModels.contains(p)) {
                log.info("[LLM路由] 模型 {} 流式不合规(已探测)，改用非流式调用", modelDesc(p));
                return callAsFlux(p, prompt, attempt, providers);
            }
            log.info("[LLM路由] 调用模型 {} mode=stream attempt={}/{}",
                    modelDesc(p), attempt + 1, MAX_FALLBACK_ATTEMPTS);
            return p.stream(prompt)
                    .onErrorResume(ex -> {
                        // OpenAI 兼容网关/服务商的 SSE chunk 缺必填字段（model / tool_calls.index 等）时，
                        // Spring AI 2.0.1 的 ChunkMerger 强读 JsonField.getRequired 抛
                        // OpenAIInvalidDataException("xxx is not set")。
                        // 【关键】绝不能 Flux.empty() 「优雅收尾」：bufferUntil 缓存的一整批 chunk
                        // 要等 mergeChunks / chunkToChatCompletion 才解析，缺字段时整批内容（含
                        // tool_calls）全丢；且「只有尾部 usage/DONE chunk 缺字段」是错的假设——
                        // 普通文本 chunk 也逐个走 chunk.model()，中间任意一个缺字段就半途抛错，
                        // 表现正是「输出一半就结束」。
                        // 首次探测即标记该模型，并就地改用非流式完整重跑；换模型重试无效（同一网关不合规）。
                        if (isMissingRequiredChunkField(ex)) {
                            if (streamIncompatibleModels.add(p)) {
                                log.warn("[LLM路由] 模型 {} 流式 chunk 格式不合规({})，已标记，后续请求改用非流式调用",
                                        modelDesc(p), ex.getMessage());
                            }
                            return callAsFlux(p, prompt, attempt, providers);
                        }
                        log.warn("[LLM路由] 模型 {} 流式调用失败(第{}/{}次)，自动切换下一个模型重试",
                                modelDesc(p), attempt + 1, MAX_FALLBACK_ATTEMPTS, ex);
                        return tryStream(prompt, attempt + 1, providers);
                    });
        }

        /**
         * 把同步 call() 包装成单元素 Flux，用于绕开不合规的流式 chunk 解析。
         * 工具调用由上层 ToolCallingAdvisor 基于聚合响应递归执行，不依赖这里是否流式。
         */
        private Flux<ChatResponse> callAsFlux(ChatModel p, Prompt prompt, int attempt, List<ChatModel> providers) {
            return Mono.fromCallable(() -> p.call(prompt))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flux()
                    .onErrorResume(ex -> {
                        log.warn("[LLM路由] 模型 {} 非流式调用失败，切换下一个模型重试", modelDesc(p), ex);
                        return tryStream(prompt, attempt + 1, providers);
                    });
        }

    }

    /**
     * 判断是否为「OpenAI 兼容网关/服务商流式 chunk 缺必填字段」这类响应格式异常。
     * 典型如 OpenAIInvalidDataException("model is not set") / ("index is not set") 等——
     * 均由 ChunkMerger 强制读取 JsonField.getRequired 引发。只要消息含 "is not set"
     * 即判定为「网关/服务商不合规、缺字段」，统一降级处理，不浪费模型切换重试。
     * 用类名字符串匹配，避免在本模块引入官方 openai-java SDK 的编译期依赖。
     */
    private static boolean isMissingRequiredChunkField(Throwable ex) {
        for (Throwable cur = ex; cur != null; cur = cur.getCause()) {
            if ("com.openai.errors.OpenAIInvalidDataException".equals(cur.getClass().getName())
                    && String.valueOf(cur.getMessage()).contains("is not set")) {
                return true;
            }
        }
        return false;
    }

}
