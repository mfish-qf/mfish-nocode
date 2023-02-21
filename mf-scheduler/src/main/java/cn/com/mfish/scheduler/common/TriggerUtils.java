package cn.com.mfish.scheduler.common;

import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import cn.com.mfish.scheduler.entity.TriggerMeta;
import cn.com.mfish.scheduler.entity.JobMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;

import java.text.MessageFormat;
import java.util.TimeZone;
import java.util.function.Function;

/**
 * @description: 触发器通用方法
 * @author: mfish
 * @date: 2023/2/6 16:58
 */
@Slf4j
public class TriggerUtils {
    public static final String SINGLE_TRIGGER = "single_trigger";

    /**
     * 获取触发器key
     *
     * @param triggerMeta
     * @return
     */
    private static TriggerKey getTriggerKey(TriggerMeta triggerMeta) {
        return TriggerKey.triggerKey(triggerMeta.getName(), triggerMeta.getGroup());
    }

    /**
     * 创建触发器
     *
     * @param scheduler   调度程序
     * @param triggerMeta 触发器信息
     * @param jobMeta     任务信息
     * @param cover       是否覆盖
     * @throws SchedulerException
     */
    public static void createTrigger(Scheduler scheduler, TriggerMeta triggerMeta, JobMeta jobMeta, boolean cover) throws SchedulerException {
        //获得触发器键
        TriggerKey triggerKey = new TriggerKey(triggerMeta.getName(), triggerMeta.getGroup());
        //判断触发器是否存在
        if (scheduler.checkExists(triggerKey) && !cover) {
            throw new SchedulerException("trigger key: " + triggerKey + " 已存在!");
        }
        // 获得triggerBuilder
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        // 设置triggerBuilder
        triggerBuilder.withIdentity(triggerKey); // 设置触发器对象
        // 设置jobDataMap
        triggerBuilder.usingJobData(JobUtils.buildJobDataMap(jobMeta)); // 设置jobDataMap
        triggerBuilder.withPriority(triggerMeta.getPriority()); // 设置优先级
        if (triggerMeta.getStartTime() == null) { // 设置开始时间
            triggerBuilder.startNow();
        } else {
            triggerBuilder.startAt(triggerMeta.getStartTime());
        }
        triggerBuilder.endAt(triggerMeta.getEndTime()); // 结束时间,可以为空
        JobKey jobKey = JobUtils.getJobKey(jobMeta);
        triggerBuilder.forJob(jobKey); // 针对的作业
        triggerBuilder.withDescription(triggerMeta.getDescription()); // 设置描述
        if (!StringUtils.isEmpty(triggerMeta.getCalendar())) { // 设置日历
            triggerBuilder.modifiedByCalendar(triggerMeta.getCalendar());
        }
        ScheduleBuilder cronTriggerBuilder = getScheduleBuilder(triggerMeta);
        // 将cronTrigger设置到triggerBuilder中
        triggerBuilder.withSchedule(cronTriggerBuilder);
        // 构造Trigger
        Trigger trigger = triggerBuilder.build();
        // 处理
        scheduleJob(scheduler, triggerKey, jobKey, trigger);
    }

    /**
     * 获取调度策略
     *
     * @param triggerMeta
     * @return
     */
    private static ScheduleBuilder getScheduleBuilder(TriggerMeta triggerMeta) {
        if (SINGLE_TRIGGER.equals(triggerMeta.getCron())) {
            return SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0);
        }
        // cron表达式
        CronScheduleBuilder cronTriggerBuilder = CronScheduleBuilder.cronSchedule(triggerMeta.getCron());
        // 时区
        cronTriggerBuilder.inTimeZone(TimeZone.getTimeZone(triggerMeta.getTimeZone()));
        // 设置过期触发策略 默认采用智能策略 SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY
        // Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY 表明对于过期的定时任务将不执行任何过期策略
        switch (triggerMeta.getMisfireInstruction()) {
            case CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
                //立即触发一次
                cronTriggerBuilder.withMisfireHandlingInstructionFireAndProceed();
                break;
            case CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING:
                // 忽略掉不管
                cronTriggerBuilder.withMisfireHandlingInstructionDoNothing();
                break;
            default:
                break;
        }
        return cronTriggerBuilder;
    }

    /**
     * 为job添加触发器
     *
     * @param scheduler
     * @param triggerKey
     * @param jobKey
     * @param trigger
     * @throws SchedulerException
     */
    private static void scheduleJob(Scheduler scheduler, TriggerKey triggerKey, JobKey jobKey, Trigger trigger) throws SchedulerException {
        // 判断trigger是否存在
        if (scheduler.checkExists(triggerKey)) {
            // 获得当前触发器
            Trigger t = scheduler.getTrigger(triggerKey);
            // 判断触发器关联
            if (jobKey.getName().equals(t.getJobKey().getName()) && jobKey.getGroup().equals(t.getJobKey().getGroup())) {
                // 更新触发器
                scheduler.rescheduleJob(triggerKey, trigger);
            } else {
                String error = "更新触发器失败!";
                throw new SchedulerException(error);
            }
        } else {
            // 创建触发器
            scheduler.scheduleJob(trigger);
        }
    }

    /**
     * 暂停触发器
     *
     * @param scheduler
     * @param triggerMeta
     * @return
     * @throws SchedulerException
     */
    public static boolean pause(Scheduler scheduler, TriggerMeta triggerMeta) throws SchedulerException {
        return operate(scheduler, triggerMeta, (triggerKey) -> {
            try {
                scheduler.pauseTrigger(triggerKey);
                return true;
            } catch (SchedulerException e) {
                return false;
            }
        });
    }

    /**
     * 恢复触发器
     *
     * @param scheduler
     * @param triggerMeta
     * @return
     * @throws SchedulerException
     */
    public static boolean resume(Scheduler scheduler, TriggerMeta triggerMeta) throws SchedulerException {
        return operate(scheduler, triggerMeta, (triggerKey) -> {
            try {
                scheduler.resumeTrigger(triggerKey);
                return true;
            } catch (SchedulerException e) {
                return false;
            }
        });
    }

    /**
     * 移除触发器
     *
     * @param scheduler
     * @param triggerMeta
     * @return
     * @throws SchedulerException
     */
    public static boolean remove(Scheduler scheduler, TriggerMeta triggerMeta) throws SchedulerException {
        return operate(scheduler, triggerMeta, (triggerKey) -> {
            try {
                return scheduler.unscheduleJob(triggerKey);
            } catch (SchedulerException e) {
                return false;
            }
        });
    }

    /**
     * 移除触发器
     *
     * @param scheduler
     * @param triggerMeta
     * @throws SchedulerException
     */
    private static boolean operate(Scheduler scheduler, TriggerMeta triggerMeta, Function<TriggerKey, Boolean> function) throws SchedulerException {
        // 获得triggerKey
        TriggerKey triggerKey = getTriggerKey(triggerMeta);
        if (scheduler.checkExists(triggerKey)) {
            return function.apply(triggerKey);
        }
        log.warn("triggerKey:" + triggerKey + "不存在");
        return false;
    }

    /**
     * 构建触发器元数据
     *
     * @param job
     * @return
     */
    public static TriggerMeta buildTriggerMeta(Job job, JobSubscribe jobSubscribe) {
        TriggerMeta triggerMeta = new TriggerMeta();
        triggerMeta.setName(MessageFormat.format("{0}:[{1}]", job.getJobName(), jobSubscribe.getId()));
        triggerMeta.setGroup(job.getJobGroup());
        Integer priority = job.getPriority();
        triggerMeta.setPriority(priority == null ? 0 : priority);
        String timeZone = job.getTimeZone();
        triggerMeta.setTimeZone(StringUtils.isEmpty(timeZone) ? "Asia/Shanghai" : timeZone);
        triggerMeta.setMisfireInstruction(job.getMisfireHandler());

        triggerMeta.setCron(jobSubscribe.getCron());
        triggerMeta.setStartTime(jobSubscribe.getStartTime());
        triggerMeta.setEndTime(jobSubscribe.getEndTime());
        return triggerMeta;
    }
}
