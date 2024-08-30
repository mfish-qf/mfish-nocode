package cn.com.mfish.scheduler.service;

import cn.com.mfish.scheduler.listener.ListenerEvent;
import cn.com.mfish.scheduler.config.properties.SchedulerProperties;
import cn.com.mfish.scheduler.entity.TriggerLog;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @description: 监听服务
 * @author: mfish
 * @date: 2023/2/7 12:05
 */
@Service
public class ListenerService extends ListenerEvent<TriggerLog> {
    @Resource
    private SchedulerProperties schedulerProperties;

    /**
     * 保存未正常触发的记录
     *
     * @param trigger 触发器
     */
    public void saveTriggerMisfired(Trigger trigger) {
        TriggerLog log = new TriggerLog();
        log.setId(UUID.randomUUID().toString());
        log.setStatus("misfired");
        log.setTriggerName(trigger.getKey().getName());
        log.setTriggerGroup(trigger.getKey().getGroup());
        log.setJobName(trigger.getJobKey().getName());
        log.setJobGroup(trigger.getJobKey().getGroup());
        log.setThreadGroupName(Thread.currentThread().getThreadGroup().getName());
        log.setThreadId(Long.toString(Thread.currentThread().threadId()));
        log.setThreadName(Thread.currentThread().getName());
        log.setThreadPriority(Long.toString(Thread.currentThread().getPriority()));
        log.setCreateDate(new Date());
        super.save(log);
    }

    /**
     * 准备触发
     *
     * @param context 任务执行的上下文环境，包含任务执行所需的相关信息
     * @throws SchedulerException 如果保存日志操作失败，可能会抛出调度异常
     */
    public void saveTriggerFired(JobExecutionContext context) throws SchedulerException {
        if (!this.schedulerProperties.isLogFlag()) {
            return;
        }
        save(context, "triggering", null, null);
    }

    /**
     * 判断是否否决
     * 本方法用于处理任务执行的否决逻辑，当任务执行被否决时，根据配置决定是否记录日志
     *
     * @param context 任务执行的上下文环境，包含任务执行所需的相关信息
     * @return 固定返回false，表示否决当前的任务执行
     * @throws SchedulerException 如果保存日志操作失败，可能会抛出调度异常
     */
    public boolean saveVetoJobExecution(JobExecutionContext context) throws SchedulerException {
        if (this.schedulerProperties.isLogFlag()) {
            save(context, "vetoed(false)", null, null);
        }
        return false;
    }

    /**
     * 准备执行作业
     *
     * @param context 任务执行的上下文环境，包含任务执行所需的相关信息
     * @throws SchedulerException 如果保存日志操作失败，可能会抛出调度异常
     */
    public void saveJobToBeExecuted(JobExecutionContext context) throws SchedulerException {
        if (this.schedulerProperties.isLogFlag()) {
            save(context, "toBeExecuted", null, null);
        }
    }

    /**
     * 作业执行被否决
     *
     * @param context 任务执行的上下文环境，包含任务执行所需的相关信息
     * @throws SchedulerException 如果保存日志操作失败，可能会抛出调度异常
     */
    public void saveJobExecutionVetoed(JobExecutionContext context) throws SchedulerException {
        if (this.schedulerProperties.isLogFlag()) {
            // 写入新的记录
            save(context, "executionVetoed", null, null);
        }
    }

    /**
     * 作业执行完毕
     *
     * @param context 任务执行的上下文环境，包含任务执行所需的相关信息
     * @throws SchedulerException 如果保存日志操作失败，可能会抛出调度异常
     */
    public void saveJobWasExecuted(JobExecutionContext context, JobExecutionException jobException)
            throws SchedulerException {
        if (!this.schedulerProperties.isLogFlag()) {
            return;
        }
        // 状态和异常信息
        String status = "executed";
        String exceptionDetail = null;
        // 如果作业异常,则放入信息到result中
        if (null != jobException) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("jobException", jobException);
            context.setResult(result);
            // 获得异常信息
            exceptionDetail = ExceptionUtils.getStackTrace(jobException);
        }
        save(context, status, null, exceptionDetail);
    }

    /**
     * 触发完成
     *
     * @param context 上下文对象，包含执行相关的信息
     * @param triggerInstructionCode 触发器的执行指令代码
     * @throws SchedulerException 如果保存过程中发生异常，则抛出调度异常
     */
    @SuppressWarnings("unchecked")
    public void saveTriggerComplete(JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) throws SchedulerException {
        if (!this.schedulerProperties.isLogFlag()) {
            return;
        }
        // 状态和异常信息
        String status = "complete";
        String exceptionDetail = null;
        // 获得result
        Object o = context.getResult();
        if (o instanceof Map) {
            Map<String, Object> result = (Map<String, Object>) o;
            status = result.get("status").toString();
            JobExecutionException jobException = (JobExecutionException) result.get("jobException");
            // 获得异常信息
            exceptionDetail = ExceptionUtils.getStackTrace(jobException);
        }
        save(context, status, triggerInstructionCode.toString(), exceptionDetail);

    }

    /**
     * 构造调度日志
     *
     * @param context         调度执行上下文，包含调度任务和触发器的相关信息
     * @param status          调度任务的状态
     * @param result          调度任务的结果
     * @param exceptionDetail 异常详情，如果调度过程中出现异常，则记录异常信息
     * @throws SchedulerException 如果保存日志过程中发生错误，则抛出调度器异常
     */
    private void save(JobExecutionContext context, String status, String result, String exceptionDetail) throws SchedulerException {
        // 获得计划任务实例
        Scheduler s = context.getScheduler();
        //构造
        TriggerLog log = new TriggerLog();
        log.setId(context.getFireInstanceId());
        log.setScheduledFireTime(context.getScheduledFireTime());
        log.setFireTime(context.getFireTime());
        log.setEndTime(new Date());
        log.setJobRunTime(context.getJobRunTime());
        log.setStatus(status);
        log.setResult(result);
        log.setErrorMsg(exceptionDetail);
        log.setTriggerName(context.getTrigger().getKey().getName());
        log.setTriggerGroup(context.getTrigger().getKey().getGroup());
        log.setJobName(context.getJobDetail().getKey().getName());
        log.setJobGroup(context.getJobDetail().getKey().getGroup());
        log.setJobClass(context.getJobDetail().getJobClass().getName());
        log.setThreadGroupName(Thread.currentThread().getThreadGroup().getName());
        log.setThreadId(Long.toString(Thread.currentThread().threadId()));
        log.setThreadName(Thread.currentThread().getName());
        log.setThreadPriority(Long.toString(Thread.currentThread().getPriority()));
        log.setScheduledId(s.getSchedulerInstanceId());
        log.setScheduledName(s.getSchedulerName());
        log.setCreateDate(new Date());
        delete(context.getFireInstanceId());
        save(log);
    }
}