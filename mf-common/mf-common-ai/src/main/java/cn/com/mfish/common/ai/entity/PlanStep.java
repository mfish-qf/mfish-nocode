package cn.com.mfish.common.ai.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 计划步骤
 * <p>
 * 由 Planner 拆解用户需求后生成，Executor 按此步骤逐个执行。
 * 每步指定要调用的微服务集合，Executor 会聚合这些服务的工具供 LLM 使用。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
@Data
@Accessors(chain = true)
public class PlanStep {
    /** 步骤描述：告诉 LLM 这一步要做什么 */
    private String description;
    /** 这一步需要使用的微服务ID集合，如 ["mf-sys","mf-oauth"] */
    private List<String> serviceIds;
    /** 执行状态：PENDING / RUNNING / COMPLETED / FAILED */
    private String status = "PENDING";
    /** 这一步的执行结果摘要（完成后填充） */
    private String result;

    public PlanStep() {
    }

    public PlanStep(String description, List<String> serviceIds) {
        this.description = description;
        this.serviceIds = serviceIds;
    }
}
