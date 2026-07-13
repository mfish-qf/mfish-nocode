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
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LLM多模型路由服务
 * <p>
 * 按租户维度路由：数据库中 tenantId 为空的配置属于全局模型，作为未配置专属模型租户的兜底；
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
     * 串行化重建动作。读操作不获取此锁，因此重建期间读取线程仍可使用旧快照，不受阻塞。
     */
    private final ReentrantLock rebuildLock = new ReentrantLock();

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
                        newMap.computeIfAbsent(config.getTenantId(), k -> new ArrayList<>()).add(chatModel);
                        log.info("[LLM路由] 注册: tenant={}, protocol={}, provider={}, model={}",
                                config.getTenantId(), config.getProtocol(), config.getProvider(), config.getModelName());
                    } catch (Exception e) {
                        log.error("[LLM路由] 注册失败: tenant={}, protocol={}, provider={}, model={}",
                                config.getTenantId(), config.getProtocol(), config.getProvider(), config.getModelName(), e);
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
     * 获取指定租户的ChatModel：租户专属模型 → 全局模型 → 默认模型
     */
    public ChatModel getChatModel(String tenantId) {
        List<ChatModel> list = resolveProviders(tenantId);
        return list.isEmpty() ? openaiChatModel : list.getFirst();
    }

    /**
     * 解析租户可用的provider链：优先租户专属，为空则回退全局
     */
    private List<ChatModel> resolveProviders(String tenantId) {
        Map<String, List<ChatModel>> snapshot = tenantProviders;
        List<ChatModel> list = snapshot.get(tenantId);
        if (list == null || list.isEmpty()) {
            list = snapshot.get(AuthInfoUtils.SUPER_TENANT_ID);
        }
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
                    log.warn("[LLM路由] 提供者{}流式调用失败，尝试fallback", provider, ex);
                    return tryStreamChain(prompt, index + 1, model, providers);
                });
    }

    private Mono<ChatResponse> tryCallChain(Prompt prompt, int index, String model, List<ChatModel> providers) {
        ChatModel provider = getProvider(index, model, providers);
        return Mono.fromCallable(() -> provider.call(prompt))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    log.warn("[LLM路由] 提供者{}同步调用失败，尝试fallback", provider, ex);
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
        log.info("[LLM路由] 调用, provider={}, model={}", provider, provider.getOptions().getModel());
        return provider;
    }

}
