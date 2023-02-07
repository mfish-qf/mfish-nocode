package cn.com.mfish.scheduler.entity;

import lombok.Data;
import org.quartz.Trigger;

import java.util.Date;

/**
 * @description: 触发器基础信息
 * @author: mfish
 * @date: 2023/2/6 17:07
 */
@Data
public class TriggerMeta {
    /**
     * 作业代码
     */
    private String jobCode;

    /**
     * 名称
     */
    private String name;

    /**
     * 组别
     */
    private String group = "default";

    /**
     * 描述
     */
    private String description = "暂无描述";

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 优先级
     */
    private Integer priority = 0;

    /**
     * 日历
     */
    private String calendar;

    /**
     * 遗漏对应的策略
     */
    private Integer misfireInstruction = Trigger.MISFIRE_INSTRUCTION_SMART_POLICY;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 时区ID
     */
    private String timeZone = "Asia/Shanghai";
}
