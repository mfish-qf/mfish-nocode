package cn.com.mfish.scheduler.entity;

import lombok.Data;

import java.util.Date;

/**
 * @description: 触发器日志
 * @author: mfish
 * @date: 2023/2/7 12:10
 */
@Data
public class TriggerLog {
    /**
     * 日志ID
     */
    private String id;

    /**
     * 计划执行时间
     */
    private Date scheduledFireTime;

    /**
     * 实际执行时间
     */
    private Date fireTime;

    /**
     * 实际结束时间
     */
    private Date endTime;

    /**
     * 执行时长
     */
    private Long jobRunTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 结果
     */
    private String result;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 触发器名称
     */
    private String triggerName;

    /**
     * 触发器组别
     */
    private String triggerGroup;

    /**
     * 作业名称
     */
    private String jobName;

    /**
     * 作业组别
     */
    private String jobGroup;

    /**
     * 作业类型
     */
    private String jobClass;

    /**
     * 线程组名称
     */
    private String threadGroupName;

    /**
     * 线程ID
     */
    private String threadId;

    /**
     * 线程名称
     */
    private String threadName;

    /**
     * 线程优先级
     */
    private String threadPriority;

    /**
     * 计划任务ID
     */
    private String scheduledId;

    /**
     * 计划任务名称
     */
    private String scheduledName;

    /**
     * 创建时间
     */
    private Date createDate;
}
