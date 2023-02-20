package cn.com.mfish.scheduler.common;

import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.entity.JobMeta;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 调度策略通用类
 * @author: mfish
 * @date: 2023/2/6 21:11
 */
@Slf4j
public class SchedulerUtils {

    /**
     * 创建调度任务，同时创建触发器和任务
     *
     * @param scheduler
     * @param job
     */
    public static void createScheduler(Scheduler scheduler, Job job, JobSubscribe jobSubscribe, boolean cover) throws SchedulerException, ClassNotFoundException {
        createScheduler(scheduler, job, Arrays.asList(jobSubscribe), cover);
    }

    /**
     * 批量创建调度策略
     *
     * @param scheduler
     * @param job
     * @param jobSubscribeList
     * @param cover
     * @throws SchedulerException
     * @throws ClassNotFoundException
     */
    public static void createScheduler(Scheduler scheduler, Job job, List<JobSubscribe> jobSubscribeList, boolean cover) throws SchedulerException, ClassNotFoundException {
        if (jobSubscribeList == null || jobSubscribeList.isEmpty()) {
            log.warn(MessageFormat.format("警告:未创建订阅策略!任务:{0}", job.getId()));
            return;
        }
        JobMeta jobMeta = JobUtils.buildJobDetailMeta(job);
        JobUtils.createJob(scheduler, jobMeta, cover);
        for (JobSubscribe subscribe : jobSubscribeList) {
            TriggerUtils.createTrigger(scheduler, TriggerUtils.buildTriggerMeta(job, subscribe), jobMeta, cover);
        }
    }
}
