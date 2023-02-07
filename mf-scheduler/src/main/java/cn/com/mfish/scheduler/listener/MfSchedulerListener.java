package cn.com.mfish.scheduler.listener;

import cn.com.mfish.common.core.utils.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;

import java.text.MessageFormat;

/**
 * @description: 调度监听
 * @author: mfish
 * @date: 2023/2/7 14:34
 */
public class MfSchedulerListener extends ListenerEvent<String> implements SchedulerListener {
    private static final String JOB = "作业";
    private static final String COLON = ":";

    @Override
    public void jobScheduled(Trigger trigger) {
        save(MessageFormat.format("[jobScheduled]{0}{1}{2}{3}被触发器{4}{5}{6}触发了"
                , JOB, trigger.getJobKey().getName(), COLON, trigger.getJobKey().getGroup()
                , trigger.getKey().getName(), COLON, trigger.getKey().getGroup()));
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        save(MessageFormat.format("[jobUnscheduled]{0}{1}{2}被移除了"
                , triggerKey.getName(), COLON, triggerKey.getGroup()));
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        save(MessageFormat.format("[triggerFinalized]{0}{1}{2}{3},触发器{4}{5}{6}已经执行完成,后续将不会继续触发"
                , JOB, trigger.getJobKey().getName(), COLON, trigger.getJobKey().getGroup()
                , trigger.getKey().getName(), COLON, trigger.getKey().getGroup()));
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        save(MessageFormat.format("[triggerPaused]{0}{1}{2}被暂停了"
                , triggerKey.getName(), COLON, triggerKey.getGroup()));
    }

    @Override
    public void triggersPaused(String s) {
        String logMsg;
        if (StringUtils.isEmpty(s)) {
            logMsg = "[triggersPaused]触发器组全部被暂停了";
        } else {
            logMsg = MessageFormat.format("[triggersPaused]触发器组{0}被暂停了", s);
        }
        save(logMsg);
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        save(MessageFormat.format("[triggerResumed]{0}{1}{2}被恢复了"
                , triggerKey.getName(), COLON, triggerKey.getGroup()));
    }

    @Override
    public void triggersResumed(String triggerGroup) {
        String logMsg;
        if (StringUtils.isEmpty(triggerGroup)) {
            logMsg = "[triggersResumed]触发器组全部被恢复了";
        } else {
            logMsg = MessageFormat.format("[triggersResumed]触发器组{0}被恢复了", triggerGroup);
        }
        save(logMsg);

    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        save(MessageFormat.format("[jobAdded]{0}{1}{2}{3}被添加了"
                , JOB, jobDetail.getKey().getName(), COLON, jobDetail.getKey().getGroup()));
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        save(MessageFormat.format("[jobDeleted]{0}{1}{2}{3}被删除了", JOB, jobKey.getName(), COLON, jobKey.getGroup()));
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        save(MessageFormat.format("[jobPaused]{0}{1}{2}{3}被暂停了", JOB, jobKey.getName(), COLON, jobKey.getGroup()));
    }

    @Override
    public void jobsPaused(String jobGroup) {
        String logMsg;
        if (StringUtils.isEmpty(jobGroup)) {
            logMsg = "[jobsPaused]作业全部被暂停了";
        } else {
            logMsg = MessageFormat.format("[jobsPaused]作业组{0}被暂停了", jobGroup);
        }
        save(logMsg);
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        save(MessageFormat.format("[jobResumed]{0}{1}{2}{3}被恢复了", JOB, jobKey.getName(), COLON, jobKey.getGroup()));
    }

    @Override
    public void jobsResumed(String jobGroup) {
        String logMsg;
        if (StringUtils.isEmpty(jobGroup)) {
            logMsg = "[jobsResumed]作业全部被恢复了";
        } else {
            logMsg = MessageFormat.format("[jobsResumed]作业组{0}被恢复了", jobGroup);
        }
        save(logMsg);
    }

    @Override
    public void schedulerError(String s, SchedulerException e) {
        save(MessageFormat.format("[schedulerError]计划任务出错:{0}\n{1}", s, ExceptionUtils.getStackTrace(e)));
    }

    @Override
    public void schedulerInStandbyMode() {
        save("[schedulerInStandbyMode]计划任务为待机状态");
    }

    @Override
    public void schedulerStarted() {
        save("[schedulerStarted]计划任务已经启动");
    }

    @Override
    public void schedulerStarting() {
        save("[schedulerStarting]计划任务正在启动中");
    }

    @Override
    public void schedulerShutdown() {
        save("[schedulerShutdown]计划任务已关闭");
    }

    @Override
    public void schedulerShuttingdown() {
        save("[schedulerShuttingdown]计划任务正在关闭中");
    }

    @Override
    public void schedulingDataCleared() {
        save("[schedulingDataCleared]计划任务数据被清除");
    }
}