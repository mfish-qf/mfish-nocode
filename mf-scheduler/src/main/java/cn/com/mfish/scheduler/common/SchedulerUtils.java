package cn.com.mfish.scheduler.common;

import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.entity.JobMeta;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * @description: 调度策略通用类
 * @author: mfish
 * @date: 2023/2/6 21:11
 */
public class SchedulerUtils {

    /**
     * 创建调度任务，同时创建触发器和任务
     *
     * @param scheduler
     * @param job
     */
    public static void createScheduler(Scheduler scheduler, Job job, boolean cover) throws SchedulerException, ClassNotFoundException {
        JobMeta jobMeta = JobUtils.buildJobDetailMeta(job);
        JobUtils.createJob(scheduler, jobMeta, cover);
        TriggerUtils.createTrigger(scheduler, TriggerUtils.buildTriggerMeta(job), jobMeta, cover);
    }
}
