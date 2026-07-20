package cn.com.mfish.common.ai.entity;

/**
 * Agent 事件类型枚举
 * <p>
 * 定义自主规划+多轮工具调用过程中各阶段的事件类型，
 * 前端可按 type 分类展示思考过程、工具调用、最终答案等。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
public enum EventType {
    /** 计划生成完成，携带 AgentPlan */
    PLAN_CREATED,
    /** 单个步骤开始执行，携带步骤索引和描述 */
    STEP_STARTED,
    /** 工具被调用，携带工具名和参数 */
    TOOL_CALLED,
    /** 工具执行完成，携带结果摘要 */
    TOOL_RESULT,
    /** 流式 token 输出，携带文本片段 */
    TOKEN_STREAM,
    /** 单个步骤完成，携带该步结果摘要 */
    STEP_COMPLETED,
    /** 整个计划执行完成，携带最终汇总 */
    PLAN_COMPLETED,
    /** 执行过程发生错误，携带错误信息 */
    ERROR
}
