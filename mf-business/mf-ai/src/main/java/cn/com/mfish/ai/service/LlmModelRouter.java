package cn.com.mfish.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM多模型路由服务
 * <p>
 * 通过ApplicationContext自动发现所有ChatModel Bean，Bean名称自动转提供者名称：
 * - "openAiChatModel"  → "openai"
 * - "ollamaChatModel"  → "ollama"
 * - "zhiPuAiChatModel" → "zhipuai"
 * <p>
 * 模型名→提供者的映射支持两种来源：
 * 1. 内置前缀规则（默认兜底）
 * 2. 数据库配置（动态刷新，优先级更高）
 * <p>
 * fallback顺序：优先使用模型名对应的提供者，失败后按注册顺序依次降级
 *
 * @author: mfish
 * @date: 2026/07/01
 */
@Service
@Slf4j
public class LlmModelRouter {

    /** Bean名称后缀 → 提供者名称的转换规则 */
    private static final String BEAN_SUFFIX = "ChatModel";

    /** 提供者名称 → ChatModel实例 */
    private final Map<String, ChatModel> providers = new LinkedHashMap<>();
    /** fallback顺序（按Bean注册顺序） */
    private final List<String> fallbackOrder;

    /** 模型名前缀 → 提供者名称的映射（内置默认规则） */
    private final Map<String, String> defaultPrefixMap = new LinkedHashMap<>();
    /** 模型名前缀 → 提供者名称的映射（数据库动态配置，优先级高于默认） */
    private final Map<String, String> dynamicPrefixMap = new ConcurrentHashMap<>();

    public LlmModelRouter(ApplicationContext applicationContext) {
        // 自动发现所有ChatModel Bean
        Map<String, ChatModel> beans = applicationContext.getBeansOfType(ChatModel.class);
        for (Map.Entry<String, ChatModel> entry : beans.entrySet()) {
            String providerName = beanNameToProvider(entry.getKey());
            this.providers.put(providerName, entry.getValue());
            log.info("[LLM路由] 注册模型提供者: {} ({})", providerName, entry.getKey());
        }
        this.fallbackOrder = List.copyOf(this.providers.keySet());

        // 内置默认前缀规则
        initDefaultPrefixMap();

        log.info("[LLM路由] 已注册提供者: {}, fallback顺序: {}", providers.keySet(), fallbackOrder);
    }

    /**
     * Bean名称转提供者名称
     * "openAiChatModel"  → "openai"
     * "ollamaChatModel"  → "ollama"
     * "zhiPuAiChatModel" → "zhipuai"
     */
    private String beanNameToProvider(String beanName) {
        if (beanName.endsWith(BEAN_SUFFIX)) {
            beanName = beanName.substring(0, beanName.length() - BEAN_SUFFIX.length());
        }
        // 驼峰转小写，首字母小写
        if (beanName.isEmpty()) {
            return "default";
        }
        // 将驼峰转为下划线再连成小写：openAi → open_ai → openai
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < beanName.length(); i++) {
            char c = beanName.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString().replace("_", "");
    }

    /**
     * 初始化内置默认前缀规则
     * 这些规则在数据库没有配置时作为兜底使用
     */
    private void initDefaultPrefixMap() {
        // Ollama常见模型
        defaultPrefixMap.put("llama", "ollama");
        defaultPrefixMap.put("mistral", "ollama");
        defaultPrefixMap.put("phi", "ollama");
        defaultPrefixMap.put("gemma", "ollama");
        // OpenAI兼容API常见模型
        defaultPrefixMap.put("gpt", "openai");
        defaultPrefixMap.put("qwen", "openai");
        defaultPrefixMap.put("deepseek", "openai");
        defaultPrefixMap.put("o1", "openai");
        defaultPrefixMap.put("o3", "openai");
        defaultPrefixMap.put("chatglm", "zhipuai");

    }

    /**
     * 刷新数据库动态映射配置
     * 由外部定时任务或配置变更事件调用
     *
     * @param prefixMap 模型名前缀 → 提供者名称的映射
     */
    public void refreshDynamicPrefixMap(Map<String, String> prefixMap) {
        dynamicPrefixMap.clear();
        if (prefixMap != null) {
            dynamicPrefixMap.putAll(prefixMap);
        }
        log.info("[LLM路由] 刷新动态映射配置: {}", dynamicPrefixMap);
    }

    /**
     * 根据模型名解析提供者名称
     * 优先级：动态配置 > 内置默认规则 > 首个注册的提供者
     */
    public String resolveProvider(String model) {
        if (model == null || model.isEmpty()) {
            return fallbackOrder.getFirst();
        }
        String lower = model.toLowerCase();

        // 1. 优先查动态配置（数据库）
        for (Map.Entry<String, String> entry : dynamicPrefixMap.entrySet()) {
            if (lower.startsWith(entry.getKey()) && providers.containsKey(entry.getValue())) {
                return entry.getValue();
            }
        }
        // 2. 兜底查内置默认规则
        for (Map.Entry<String, String> entry : defaultPrefixMap.entrySet()) {
            if (lower.startsWith(entry.getKey()) && providers.containsKey(entry.getValue())) {
                return entry.getValue();
            }
        }
        // 3. 直接匹配提供者名称
        if (providers.containsKey(lower)) {
            return lower;
        }
        // 4. 默认返回首个提供者
        return fallbackOrder.getFirst();
    }

    /**
     * 获取所有已注册的提供者名称
     */
    public Set<String> getProviderNames() {
        return Collections.unmodifiableSet(providers.keySet());
    }

    /**
     * 流式调用，支持fallback链
     */
    public Flux<ChatResponse> streamWithFallback(Prompt prompt, String model) {
        String primaryProvider = resolveProvider(model);
        List<String> chain = buildFallbackChain(primaryProvider);
        return tryStreamChain(prompt, chain, 0, model);
    }

    /**
     * 同步调用，支持fallback链
     */
    public Mono<ChatResponse> callWithFallback(Prompt prompt, String model) {
        String primaryProvider = resolveProvider(model);
        List<String> chain = buildFallbackChain(primaryProvider);
        return tryCallChain(prompt, chain, 0);
    }

    private List<String> buildFallbackChain(String primaryProvider) {
        List<String> chain = new ArrayList<>();
        chain.add(primaryProvider);
        for (String provider : fallbackOrder) {
            if (!chain.contains(provider)) {
                chain.add(provider);
            }
        }
        return chain;
    }

    private Flux<ChatResponse> tryStreamChain(Prompt prompt, List<String> chain, int index, String model) {
        if (index >= chain.size()) {
            return Flux.error(new RuntimeException("所有模型均不可用"));
        }
        String provider = chain.get(index);
        log.info("[LLM路由] 流式调用, provider={}, model={}", provider, model);
        return providers.get(provider).stream(prompt)
                .onErrorResume(ex -> {
                    log.warn("[LLM路由] 提供者{}流式调用失败，尝试fallback", provider, ex);
                    return tryStreamChain(prompt, chain, index + 1, model);
                });
    }

    private Mono<ChatResponse> tryCallChain(Prompt prompt, List<String> chain, int index) {
        if (index >= chain.size()) {
            return Mono.error(new RuntimeException("所有模型均不可用"));
        }
        String provider = chain.get(index);
        log.info("[LLM路由] 同步调用, provider={}", provider);
        return Mono.fromCallable(() -> providers.get(provider).call(prompt))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    log.warn("[LLM路由] 提供者{}同步调用失败，尝试fallback", provider, ex);
                    return tryCallChain(prompt, chain, index + 1);
                });
    }
}
