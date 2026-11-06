package cn.com.mfish.ai.service.impl;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.capability.SkillChatClientProvider;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * {@link SkillChatClientProvider} 业务层实现
 * <p>
 * 委托 {@link LlmModelRouter#getChatModel(String)} 按租户路由获取 {@link ChatModel}，
 * 供 {@code SkillCapabilityEngine} 执行提示词级 Skill 时构建 ChatClient。
 * </p>
 * <p>
 * 跨模块解耦：mf-common-ai 仅依赖此接口，不直接依赖 mf-ai 模块的 LlmModelRouter。
 * </p>
 * <p>
 * <b>租户解析</b>：Skill 在 LLM 工具调用线程中执行，{@code ExecutionContext} 可能未携带租户上下文，
 * 此时按以下优先级解析租户：
 * <ol>
 *   <li>传入的 tenantId 非空 → 直接使用</li>
 *   <li>请求线程上下文（{@link AuthInfoUtils#getCurrentTenantId()}）能获取 → 使用之</li>
 *   <li>都失败 → 回退到 {@link AuthInfoUtils#SUPER_TENANT_ID}（超级租户，最终兜底到 Spring AI 默认模型）</li>
 * </ol>
 * 避免 null 传入 {@code LlmModelRouter.resolveProviders} 导致
 * {@code Map.of().get(null)} 抛 NPE。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/22
 * @version: V2.4.1
 */
@Slf4j
@Service
public class SkillChatClientProviderImpl implements SkillChatClientProvider {

    private final LlmModelRouter llmModelRouter;

    public SkillChatClientProviderImpl(LlmModelRouter llmModelRouter) {
        this.llmModelRouter = llmModelRouter;
    }

    @Override
    public ChatModel getChatModel(String tenantId) {
        // 入参为空时尝试从请求线程解析，最终兜底为超级租户（无专属模型时回落到 yml/nacos 默认模型）
        // 避免 null 传入 LlmModelRouter.resolveProviders 触发 Map.of().get(null) NPE
        String resolvedTenantId = resolveTenantId(tenantId);
        return llmModelRouter.getChatModel(resolvedTenantId);
    }

    /**
     * 解析租户ID：入参优先 → 请求上下文 → 超级租户兜底
     */
    private String resolveTenantId(String tenantId) {
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId;
        }
        try {
            // Skill 在 boundedElastic 线程执行，若请求上下文已传播至此可获取真实租户
            String current = AuthInfoUtils.getCurrentTenantId();
            if (current != null && !current.isEmpty()) {
                return current;
            }
        } catch (Exception e) {
            // 无请求上下文（如异步线程），静默回退
            log.debug("[SkillChatClientProvider] 无请求上下文，回退到全局模型");
        }
        return AuthInfoUtils.SUPER_TENANT_ID;
    }
}
