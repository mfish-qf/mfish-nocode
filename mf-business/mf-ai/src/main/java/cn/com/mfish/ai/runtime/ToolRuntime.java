package cn.com.mfish.ai.runtime;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.agent.TenantContext;
import cn.com.mfish.common.ai.capability.ActionDefinition;
import cn.com.mfish.common.ai.capability.CapabilityEngine;
import cn.com.mfish.common.ai.capability.EngineType;
import cn.com.mfish.common.ai.capability.SkillCapabilityEngine;
import cn.com.mfish.common.ai.engine.ApiToolEngine;
import cn.com.mfish.common.ai.entity.AgentPlan;
import cn.com.mfish.common.ai.entity.FrontendAction;
import cn.com.mfish.common.ai.entity.PlanStep;
import cn.com.mfish.common.ai.frontend.FrontendActionHolder;
import cn.com.mfish.common.ai.frontend.FrontendActionTool;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Runtime boundary for tools.
 *
 * <p>Assistants, Planner and Executor should ask this component for tool-facing
 * decisions instead of directly coupling to ApiToolEngine, Skill internals or
 * FrontendActionHolder.</p>
 */
@Slf4j
@Component
public class ToolRuntime {

    private final LlmModelRouter llmModelRouter;
    private final ApiToolEngine apiToolEngine;
    private final CapabilityEngine capabilityEngine;
    private final ObjectProvider<SkillCapabilityEngine> skillCapabilityEngineProvider;

    public ToolRuntime(LlmModelRouter llmModelRouter,
                       ApiToolEngine apiToolEngine,
                       CapabilityEngine capabilityEngine,
                       ObjectProvider<SkillCapabilityEngine> skillCapabilityEngineProvider) {
        this.llmModelRouter = llmModelRouter;
        this.apiToolEngine = apiToolEngine;
        this.capabilityEngine = capabilityEngine;
        this.skillCapabilityEngineProvider = skillCapabilityEngineProvider;
    }

    public Set<String> getAllServiceIds() {
        return apiToolEngine.getAllServiceIds();
    }

    public ToolCallbackProvider getToolCallbackProvider(Set<String> serviceIds) {
        return apiToolEngine.getToolCallbackProvider(serviceIds);
    }

    /**
     * chat 模式（垂直助手）按主服务收敛工具范围。
     *
     * <p>旧实现先 {@code getExtendedServiceIds()} 把所有非 mf-* 扩展服务（全部 skill-* / MCP / frontend）
     * 注入，再叠加所有 guide 的 requires，等于每个垂直助手都加载全平台工具（实测 290 个）。
     * 现只保留：主服务 + frontend + requires 命中主服务的 guide（及其依赖），
     * 既保证该业务域能读到操作指南、能完成前端联动，又不引入无关服务。</p>
     */
    public Set<String> resolveAssistantServiceIds(String mainServiceId) {
        Set<String> serviceIds = new LinkedHashSet<>();
        if (mainServiceId != null && !mainServiceId.isEmpty()) {
            serviceIds.add(mainServiceId);
        }
        serviceIds.add(FrontendActionTool.FRONTEND_SERVICE_ID);
        SkillCapabilityEngine skillEngine = skillCapabilityEngineProvider.getIfAvailable();
        if (skillEngine != null) {
            for (String skillServiceId : collectAllSkillServiceIds()) {
                String skillCode = skillServiceId.substring("skill-".length());
                List<String> requires = skillEngine.getGuideRequires("skill." + skillCode);
                // 命中的 guide 要；未声明 requires 的纯工具型 skill 也要（每个仅 1 个工具，代价很低）；
                // 声明了 requires 但指向别的主服务的 guide 不要，避免把无关业务服务工具带进来。
                if (requires == null || requires.isEmpty() || requires.contains(mainServiceId)) {
                    serviceIds.add(skillServiceId);
                }
            }
        }
        // 只合并已选中 guide 的 requires（本方法此时仅含相关 guide）
        mergeGuideRequiresInto(serviceIds);
        return serviceIds;
    }

    public Set<String> resolveStepServiceIds(Collection<String> serviceIds) {
        Set<String> resolved = serviceIds != null ? new HashSet<>(serviceIds) : new HashSet<>();
        resolved.add(FrontendActionTool.FRONTEND_SERVICE_ID);
        return resolved;
    }

    /**
     * 合并一份计划里所有步骤最终装配好的 serviceIds，供「通用助手单步对话」使用。
     *
     * <p>{@link #enrichPlanServiceIds(AgentPlan)} 已为每一步补齐 guide 依赖（requires），
     * 这里把各步结果求并集；只要最终非空就补上 frontend（指南里常含 frontend.navigate / refresh，
     * 与垂直助手的 resolveAssistantServiceIds 口径一致），让通用助手既能读指南也能执行前端联动。</p>
     *
     * <p>若计划本身为空（如「讲个笑话」这类无需工具的纯对话，Planner 返回 steps: []），
     * 返回空集，调用方应据此退化为纯对话、零工具注入。</p>
     */
    public Set<String> collectPlanServiceIds(cn.com.mfish.common.ai.entity.AgentPlan plan) {
        Set<String> merged = new LinkedHashSet<>();
        if (plan == null || plan.getSteps() == null) {
            return merged;
        }
        for (cn.com.mfish.common.ai.entity.PlanStep step : plan.getSteps()) {
            if (step.getServiceIds() != null) {
                merged.addAll(step.getServiceIds());
            }
        }
        if (!merged.isEmpty()) {
            merged.add(FrontendActionTool.FRONTEND_SERVICE_ID);
        }
        return merged;
    }

    /**
     * 规划后处理：保留 Planner 按问题选出的 step.serviceIds，只补齐「本步选中 guide」声明的依赖服务。
     *
     * <p>旧实现无条件把「所有 skill-* 服务」和「所有 guide 的 requires」合并进每一步，
     * 把 LLM 的筛选结果完全冲掉，导致每步都注入全量工具（实测 290 个 / 22 个 serviceId）。
     * 现改为按步精准装配；仅当 LLM 没给出任何有效 serviceId 时才回退全量并告警，
     * 避免出现「一个工具都没有、步骤无法执行」。</p>
     */
    public void enrichPlanServiceIds(AgentPlan plan) {
        if (plan == null || plan.getSteps() == null || plan.getSteps().isEmpty()) {
            return;
        }
        // 有效 serviceId 同时来自工具注册表与能力注册表（两者可能不完全重合）
        Set<String> knownServiceIds = new HashSet<>(apiToolEngine.getAllServiceIds());
        List<ActionDefinition> actions = capabilityEngine.getAvailableActions();
        if (actions != null) {
            for (ActionDefinition action : actions) {
                if (action.getServiceId() != null) {
                    knownServiceIds.add(action.getServiceId());
                }
            }
        }
        SkillCapabilityEngine skillEngine = skillCapabilityEngineProvider.getIfAvailable();
        Set<String> allSkillServiceIds = collectAllSkillServiceIds();
        for (PlanStep step : plan.getSteps()) {
            Set<String> merged = new LinkedHashSet<>();
            if (step.getServiceIds() != null) {
                for (String serviceId : step.getServiceIds()) {
                    if (serviceId != null && knownServiceIds.contains(serviceId)) {
                        merged.add(serviceId);
                    }
                }
            }
            if (merged.isEmpty()) {
                log.warn("[ToolRuntime] 步骤未给出有效 serviceIds，回退全量工具, step={}, raw={}",
                        step.getDescription(), step.getServiceIds());
                merged.addAll(knownServiceIds);
            }
            if (skillEngine != null) {
                // 反查补 guide：本步用到某业务服务时，自动带上 requires 命中它的操作指南。
                // 防止 LLM 只写了 mf-project 却漏写 skill-project-management，导致读不到指南、
                // 前面调好的周报格式规则失效。只用 mf-* 匹配（frontend 是几乎所有 guide 的
                // 通用依赖，拿它匹配会把所有 guide 又拉回来，等于没收缩）。
                Set<String> businessIds = new HashSet<>();
                for (String id : merged) {
                    if (id.startsWith("mf-")) {
                        businessIds.add(id);
                    }
                }
                if (!businessIds.isEmpty()) {
                    for (String skillServiceId : allSkillServiceIds) {
                        String skillCode = skillServiceId.substring("skill-".length());
                        List<String> requires = skillEngine.getGuideRequires("skill." + skillCode);
                        if (requires != null && requires.stream()
                                .anyMatch(r -> r.startsWith("mf-") && businessIds.contains(r))) {
                            merged.add(skillServiceId);
                        }
                    }
                }
                // 只合并「本步最终选中的」 guide 的 requires
                for (String serviceId : new ArrayList<>(merged)) {
                    if (!serviceId.startsWith("skill-")) {
                        continue;
                    }
                    String skillCode = serviceId.substring("skill-".length());
                    List<String> requires = skillEngine.getGuideRequires("skill." + skillCode);
                    if (requires != null && !requires.isEmpty()) {
                        merged.addAll(requires);
                        log.info("[ToolRuntime] 合并选中 guide 依赖 skill={} requires={}", skillCode, requires);
                    }
                }
            }
            step.setServiceIds(new ArrayList<>(merged));
        }
        log.info("[ToolRuntime] 规划服务集合装配完成 steps={}, 每步 serviceIds 数={}",
                plan.getSteps().size(),
                plan.getSteps().stream()
                        .map(s -> s.getServiceIds() == null ? 0 : s.getServiceIds().size())
                        .toList());
    }

    public TenantContext captureTenantContext() {
        String tenantId = llmModelRouter.currentTenantId();
        String userId = AuthInfoUtils.getCurrentUserId();
        String accessToken = AuthInfoUtils.getAccessToken();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServerWebExchange serverWebExchange = ServletUtils.getExchange();
        return new TenantContext(tenantId, userId, accessToken, requestAttributes, serverWebExchange);
    }

    public Map<String, Object> buildToolContext(String sessionId, TenantContext tenantContext) {
        String tenantId;
        String userId;
        String accessToken;
        Object requestAttributes;
        Object serverWebExchange;
        if (tenantContext != null) {
            tenantId = tenantContext.getTenantId();
            userId = tenantContext.getUserId();
            accessToken = tenantContext.getAccessToken();
            requestAttributes = tenantContext.getRequestAttributes();
            serverWebExchange = tenantContext.getServerWebExchange();
        } else {
            tenantId = AuthInfoUtils.getCurrentTenantId();
            userId = AuthInfoUtils.getCurrentUserId();
            accessToken = AuthInfoUtils.getAccessToken();
            requestAttributes = RequestContextHolder.getRequestAttributes();
            serverWebExchange = ServletUtils.getExchange();
        }

        Map<String, Object> context = new HashMap<>();
        context.put(RPCConstants.REQ_USER_ID, userId != null ? userId : "");
        context.put(RPCConstants.REQ_TENANT_ID, tenantId != null ? tenantId : AuthInfoUtils.SUPER_TENANT_ID);
        context.put(RPCConstants.REQ_ORIGIN, RPCConstants.AI);
        if (accessToken != null && !accessToken.isEmpty()) {
            context.put(RPCConstants.REQ_TOKEN, accessToken);
        }
        if (requestAttributes instanceof RequestAttributes ra) {
            context.put(RPCConstants.REQ_REQUEST_ATTRIBUTES, ra);
        }
        if (serverWebExchange instanceof ServerWebExchange swe) {
            context.put(RPCConstants.REQ_SERVER_WEB_EXCHANGE, swe);
        }
        context.put(FrontendActionHolder.CTX_SESSION_ID, sessionId);
        return context;
    }

    public String buildToolUsageHint(ToolCallbackProvider toolProvider) {
        ToolCallback[] callbacks = toolProvider.getToolCallbacks();
        if (callbacks.length == 0) {
            return "## 工具使用规则\n当前没有可用工具，请直接根据你的知识回答用户问题。";
        }
        StringBuilder sb = new StringBuilder("## 工具使用规则\n");
        sb.append("你只能使用以下工具，不能虚构任何工具名：\n");
        boolean hasSkill = false;
        boolean hasFrontend = false;
        for (ToolCallback callback : callbacks) {
            String name = callback.getToolDefinition().name();
            String desc = callback.getToolDefinition().description();
            if (name.startsWith("skill.")) {
                hasSkill = true;
            }
            if (name.startsWith("frontend.")) {
                hasFrontend = true;
            }
            sb.append("- ").append(name);
            if (desc != null && !desc.isEmpty()) {
                sb.append(": ").append(desc);
            }
            sb.append("\n");
        }
        sb.append("\n调用规则：\n");
        sb.append("1. 先分析用户意图，从工具列表中选择最合适的工具。\n");
        sb.append("2. 只能使用上方列出的工具名，不要构造新工具名。\n");
        sb.append("3. 没有合适工具时，直接回答用户。\n");
        sb.append("4. 工具调用失败时，根据错误信息调整参数或选择其他工具。\n");
        if (hasSkill && hasFrontend) {
            sb.append("5. 【强制】如果工具列表中包含 skill.* 开头的工具，且用户需求属于该 skill 对应的领域（如请假、代码生成），\n");
            sb.append("   必须优先调用 skill 工具获取操作指南。获取指南后，必须立即按指南中的步骤顺序逐个调用对应工具\n");
            sb.append("   （包括 frontend.navigate 路由跳转、frontend.refresh 页面刷新等前端操作工具），\n");
            sb.append("   严禁仅返回指南内容而不执行，严禁跳过任何前端操作步骤。\n");
        }
        return sb.toString();
    }

    public void resetFrontendActions(String sessionId) {
        FrontendActionHolder.clear(sessionId);
        FrontendActionHolder.unregisterEmitter(sessionId);
    }

    public void registerFrontendEmitter(String sessionId, Consumer<FrontendAction> emitter) {
        FrontendActionHolder.registerEmitter(sessionId, emitter);
    }

    public void unregisterFrontendEmitter(String sessionId) {
        FrontendActionHolder.unregisterEmitter(sessionId);
    }

    public void clearFrontendActions(String sessionId) {
        FrontendActionHolder.clear(sessionId);
    }

    public List<FrontendAction> drainFrontendActions(String sessionId) {
        return FrontendActionHolder.drain(sessionId);
    }

    private void mergeGuideRequiresInto(Set<String> serviceIds) {
        SkillCapabilityEngine skillEngine = skillCapabilityEngineProvider.getIfAvailable();
        if (skillEngine == null || serviceIds == null || serviceIds.isEmpty()) {
            return;
        }
        Set<String> skillServiceIds = new HashSet<>();
        for (String serviceId : serviceIds) {
            if (serviceId != null && serviceId.startsWith("skill-")) {
                skillServiceIds.add(serviceId);
            }
        }
        for (String skillServiceId : skillServiceIds) {
            String skillCode = skillServiceId.substring("skill-".length());
            List<String> requires = skillEngine.getGuideRequires("skill." + skillCode);
            if (requires != null && !requires.isEmpty()) {
                serviceIds.addAll(requires);
                log.info("[ToolRuntime] merge assistant guide requires skill={} requires={} serviceIds={}",
                        skillCode, requires, serviceIds);
            }
        }
    }

    private Set<String> collectAllSkillServiceIds() {
        Set<String> skillServiceIds = new LinkedHashSet<>();
        try {
            List<ActionDefinition> actions = capabilityEngine.getAvailableActions(EngineType.SKILL);
            if (actions != null) {
                for (ActionDefinition action : actions) {
                    if (action.getServiceId() != null && action.getServiceId().startsWith("skill-")) {
                        skillServiceIds.add(action.getServiceId());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[ToolRuntime] collect skill serviceIds failed", e);
        }
        return skillServiceIds;
    }
}
