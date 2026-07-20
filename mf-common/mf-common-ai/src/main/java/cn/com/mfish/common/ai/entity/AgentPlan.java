package cn.com.mfish.common.ai.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Agent 执行计划
 * <p>
 * Planner 调用 LLM 拆解用户需求后生成的结构化计划，
 * 包含若干 {@link PlanStep}，由 AgentRuntime 逐个执行。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
@Data
@Accessors(chain = true)
public class AgentPlan {
    /** 原始用户输入 */
    private String originalPrompt;
    /** 拆解出的步骤列表 */
    private List<PlanStep> steps;
    /** 计划的整体描述（LLM 生成的简要说明） */
    private String summary;

    public AgentPlan() {
    }

    public AgentPlan(String originalPrompt, List<PlanStep> steps, String summary) {
        this.originalPrompt = originalPrompt;
        this.steps = steps;
        this.summary = summary;
    }
}
