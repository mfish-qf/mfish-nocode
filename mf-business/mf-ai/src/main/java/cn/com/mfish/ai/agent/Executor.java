package cn.com.mfish.ai.agent;

import cn.com.mfish.common.ai.agent.EventBus;
import cn.com.mfish.common.ai.agent.TenantContext;
import cn.com.mfish.common.ai.agent.ToolCapable;
import cn.com.mfish.common.ai.entity.AgentPlan;
import cn.com.mfish.common.ai.entity.EventType;
import cn.com.mfish.common.ai.entity.PlanStep;
import cn.com.mfish.ai.runtime.ToolRuntime;
import cn.com.mfish.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 步骤执行器
 * <p>
 * 按 {@link PlanStep} 逐个执行，每步：
 * <ol>
 *   <li>从步骤描述构建提示词</li>
 *   <li>通过 {@link ToolCapable} 调用 LLM+工具（Spring AI 内部处理工具循环）</li>
 *   <li>流式 token 推送 {@link EventType#TOKEN_STREAM} 事件</li>
 *   <li>收集完整结果，推送 {@link EventType#STEP_COMPLETED} 事件</li>
 * </ol>
 * </p>
 * <p>
 * 每步的返回结果会作为下一步的上下文，形成"多轮调用"的链式上下文。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
@Slf4j
@Component
public class Executor {

    private final ToolCapable toolCapable;
    private final ToolRuntime toolRuntime;

    public Executor(@Qualifier("agentAssistant") ToolCapable toolCapable, ToolRuntime toolRuntime) {
        this.toolCapable = toolCapable;
        this.toolRuntime = toolRuntime;
    }

    /**
     * 执行单个步骤
     *
     * @param step           要执行的步骤
     * @param stepIndex      步骤索引（从 0 开始）
     * @param sessionId      会话ID
     * @param plan           整体计划（用于构建上下文）
     * @param eventBus       事件总线
     * @param tenantContext  请求线程捕获的租户上下文快照
     * @return 步骤执行结果文本（用于后续步骤上下文）
     */
    public Mono<String> execute(PlanStep step, int stepIndex, String sessionId,
                                AgentPlan plan, EventBus eventBus, TenantContext tenantContext) {
        // 标记步骤开始
        step.setStatus("RUNNING");
        eventBus.emit(EventType.STEP_STARTED, stepIndex, step.getDescription());
        // 清理上一步可能残留的前端操作指令和 emitter
        toolRuntime.resetFrontendActions(sessionId);

        // 注册 emitter：工具调用时通过 EventBus 实时下发 FRONTEND_ACTION 事件
        // 使 navigate/refresh 等操作在 LLM 调用工具时立即下发，与 token 流按实际执行顺序交织
        toolRuntime.registerFrontendEmitter(sessionId, fa -> {
            String json = com.alibaba.fastjson2.JSON.toJSONString(fa);
            eventBus.emit(cn.com.mfish.common.ai.entity.EventType.FRONTEND_ACTION, stepIndex, json);
            log.info("[Executor] 步骤{} 实时下发前端操作: {} {}", stepIndex, fa.getAction(), fa.getTarget());
        });

        // 构建提示词：包含原始需求和当前步骤描述
        String prompt = buildStepPrompt(step, stepIndex, plan);

        StringBuilder resultBuilder = new StringBuilder();
        // 记录上游是否给过正式结束信号：工具调用会产生多轮 LLM 流，中间轮也可能带 finishReason，
        // 仅用于诊断（区分"上游正常结束"与"上游中途断流"），不再据此提前结束订阅。
        AtomicBoolean stopReceived = new AtomicBoolean(false);

        // serviceIds 可能为 null，做兜底
        Set<String> serviceIdSet = toolRuntime.resolveStepServiceIds(step.getServiceIds());

        // 传入租户上下文，避免异步线程拿不到 RequestAttributes
        return toolCapable.chatWithTools(sessionId, prompt, serviceIdSet, tenantContext)
                .doOnNext(chatResponse -> {
                    String token = extractText(chatResponse);
                    if (StringUtils.isNotEmpty(token)) {
                        resultBuilder.append(token);
                        eventBus.emit(EventType.TOKEN_STREAM, stepIndex, token);
                    }
                    if (isFinished(chatResponse)) {
                        stopReceived.set(true);
                    }
                })
                // ★ 等整个流式响应自然结束再汇总。旧实现 filter(isFinished).next() 在第一个
                // finish 信号到达时立刻 cancel 上游：多轮工具调用场景下，这会让最终答案生成到
                // 一半被掐断，而步骤仍被标成 [完成]，正是"回复不完整"的根因。
                .then(Mono.fromSupplier(resultBuilder::toString))
                .map(result -> {
                    if (!stopReceived.get()) {
                        // 未收到 STOP 即结束 = 上游流被中途关闭，留日志便于继续定位代理/网关侧断流
                        log.warn("[Executor] 步骤{} 流式响应未收到 STOP 即结束, 已收集 {} 字符, 结果可能被上游截断",
                                stepIndex, result.length());
                    }
                    step.setStatus("COMPLETED").setResult(result);
                    String summary = result.length() > 200 ? result.substring(0, 200) + "..." : result;
                    eventBus.emit(EventType.STEP_COMPLETED, stepIndex, summary);
                    // 下发延迟的 refresh 操作（文本之后）
                    emitDeferredFrontendActions(eventBus, stepIndex, sessionId);
                    return result;
                })
                .onErrorResume(ex -> {
                    log.error("[Executor] 步骤{}执行失败", stepIndex, ex);
                    step.setStatus("FAILED").setResult(ex.getMessage());
                    eventBus.emit(EventType.ERROR, stepIndex, "步骤" + (stepIndex + 1) + "执行失败: " + ex.getMessage());
                    // 保留已收到的文本，避免一次异常把整段回复丢掉
                    return Mono.just(resultBuilder.toString());
                })
                .doFinally(signal -> {
                    // 注销 emitter，防止内存泄漏
                    toolRuntime.unregisterFrontendEmitter(sessionId);
                    toolRuntime.clearFrontendActions(sessionId);
                });
    }

    /**
     * 构建步骤提示词
     * <p>
     * 把原始需求和当前步骤描述组合，让 LLM 理解这一步要做什么。
     * 强化指导：必须实际调用工具，不得仅描述步骤或声称"无法调用"。
     * </p>
     */
    private String buildStepPrompt(PlanStep step, int stepIndex, AgentPlan plan) {
        // 注入当前日期，供 LLM 解析"明天"、"后天"等相对日期
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate tomorrow = today.plusDays(1);
        java.time.LocalDate dayAfter = today.plusDays(2);
        return "原始需求：" + plan.getOriginalPrompt() + "\n\n" +
                "当前是第 " + (stepIndex + 1) + " 步，共 " + plan.getSteps().size() + " 步。\n" +
                "这一步的任务：" + step.getDescription() + "\n\n" +
                "【当前真实日期】今天=" + today + "，明天=" + tomorrow + "，后天=" + dayAfter
                + "（必须使用这些日期，严禁使用训练数据中的日期；用户提到相对日期时转换为具体日期，格式 yyyy-MM-dd HH:mm:ss）\n\n" +
                """
                # 执行要求（必须严格遵守）
                1. **必须实际调用工具**：你已被注入了完成本步骤所需的全部工具，工具列表已在上方列出。必须从中选择匹配的工具并实际调用，不得仅描述"建议调用"或"需要调用"而不执行。
                2. **工具名格式**：工具名采用 `controllerName.operationId` 格式（如 `demoLeaveApply.add`、`demoLeaveApply.submit`）。请从上方工具列表中找到名称完全匹配的工具调用，不要虚构或猜测工具名。
                3. 【强制】**guide 类型 Skill 优先调用**：如果工具列表中包含 `skill.` 开头的工具（如 `skill.leave-apply`），
                   必须首先调用它获取操作指南。指南返回的是操作流程，不是最终答案！
                   获取指南后，必须立即按指南中的步骤顺序逐个调用对应工具（包括 frontend.navigate、demoLeaveApply.add、
                   demoLeaveApply.submit、frontend.refresh 等），严禁仅返回指南内容而不执行后续工具调用。
                4. 【强制】**严格串行执行**：每次只调用一个工具，等待该工具返回结果后，再调用下一个工具！
                   严禁一次性发起多个工具调用！前一步的返回值（如单据 ID）是后一步的入参。
                   例如：调用 frontend.navigate → 等待返回 → 调用 demoLeaveApply.add → 等待返回取 ID →
                   调用 demoLeaveApply.submit → 等待返回 → 调用 frontend.refresh → 等待返回 → 汇总反馈。
                5. **禁止虚假执行**：严禁出现"我无法直接调用"、"请您手动执行"、"请在后台系统中操作"等表述。工具已就绪，你完全可以调用。
                6. **结果反馈**：所有工具调用完成后，基于工具返回的真实数据给出这一步的结论。如果工具返回失败，说明失败原因。
                7. 【强制】**前端操作必须执行**：当 skill 指南中包含 `frontend.navigate`（路由跳转）或
                   `frontend.refresh`（页面刷新）步骤时，必须实际调用对应的 frontend 工具。这些操作触发前端
                   页面跳转和数据刷新，是完整业务流程的一部分，不得跳过或仅描述而不调用。
                """;
    }

    /**
     * 从 ChatResponse 提取文本。
     * usage-only / 工具调用等中间 chunk 可能没有 output，直接取会 NPE 打断整个流、导致结果被截断，
     * 这里统一返回空串跳过。
     */
    private String extractText(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getResult() == null
                || chatResponse.getResult().getOutput() == null) {
            return "";
        }
        String text = chatResponse.getResult().getOutput().getText();
        return text != null ? text : "";
    }

    /**
     * 判断是否收到正式结束信号（finish_reason=STOP），仅用于诊断日志。
     * 不再用它提前终止订阅——工具调用的中间轮也可能带 STOP。
     */
    private boolean isFinished(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getResult() == null
                || chatResponse.getResult().getMetadata() == null) {
            return false;
        }
        return "STOP".equals(chatResponse.getResult().getMetadata().getFinishReason());
    }

    /**
     * 下发延迟的前端操作（refresh），在文本流完成后调用
     * <p>
     * FrontendActionHolder 将 refresh 操作存入延迟通道（list），不走 emitter 实时下发。
     * 此方法在 STEP_COMPLETED 之后调用 drain 取出 refresh 操作，通过 EventBus 下发，
     * 实现"文本反馈 → refresh → 步骤结束"的顺序。
     * </p>
     *
     * @param eventBus   事件总线
     * @param stepIndex  步骤索引
     * @param sessionId  会话ID
     */
    private void emitDeferredFrontendActions(EventBus eventBus, int stepIndex, String sessionId) {
        List<cn.com.mfish.common.ai.entity.FrontendAction> deferred =
                toolRuntime.drainFrontendActions(sessionId);
        for (cn.com.mfish.common.ai.entity.FrontendAction fa : deferred) {
            String json = com.alibaba.fastjson2.JSON.toJSONString(fa);
            log.info("[Executor] 步骤{} 延迟下发前端操作: {} {}", stepIndex, fa.getAction(), fa.getTarget());
            eventBus.emit(cn.com.mfish.common.ai.entity.EventType.FRONTEND_ACTION, stepIndex, json);
        }
    }
}
