package cn.com.mfish.common.workflow.api.entity;

import lombok.Getter;

/**
 * @description: 任务统计
 * @author: mfish
 * @date: 2025/10/10
 */

public class TaskTotal {

    public TaskTotal(long todoCount, long completedCount, long cancelledCount) {
        this.todoCount = todoCount;
        this.completedCount = completedCount;
        this.cancelledCount = cancelledCount;
    }
    /**
     * 待办任务数量
     */
    @Getter
    private final Long todoCount;
    /**
     * 已办任务数量
     */
    @Getter
    private final Long completedCount;
    /**
     * 已取消任务数量
     */
    @Getter
    private final Long cancelledCount;
    /**
     * 总任务数量
     */
    private Long totalCount;
    /**
     * 获取总任务数量
     *
     * @return 总任务数量
     */
    public Long getTotalCount() {
        return todoCount + completedCount + cancelledCount;
    }
}
