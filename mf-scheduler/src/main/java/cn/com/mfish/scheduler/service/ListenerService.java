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
        log.setThreadId(Long.toString(Thread.currentThread().getId()));
        log.setThreadName(Thread.currentThread().getName());
        log.setThreadPriority(Long.toString(Thread.currentThread().getPriority()));
        log.setCreateDate(new Date());
        super.save(log);
    }

    /**
     * 准备触发
     *
     * @param context
     * @throws SchedulerException
     */
    public void saveTriggerFired(JobExecutionContext context) throws SchedulerException {
        if (!this.schedulerProperties.isLogFlag()) {
            return;
        }
        save(context, "triggering", null, null);
    }

    /**
     * 判断是否否决
     *
     * @param context
     * @return
     * @throws SchedulerException
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
     * @param context
     * @throws SchedulerException
     */
    public void saveJobToBeExecuted(JobExecutionContext context) throws SchedulerException {
        if (this.schedulerProperties.isLogFlag()) {
            save(context, "toBeExecuted", null, null);
        }
    }

    /**
     * 作业执行被否决
     *
     * @param context
     * @throws SchedulerException
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
     * @param context
     * @param jobException
     * @throws SchedulerException
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
     * @param context
     * @param triggerInstructionCode
     * @throws SchedulerException
     */
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
     * @param context
     * @param status
     * @param result
     * @param exceptionDetail
     * @throws SchedulerException
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
        log.setThreadId(Long.toString(Thread.currentThread().getId()));
        log.setThreadName(Thread.currentThread().getName());
        log.setThreadPriority(Long.toString(Thread.currentThread().getPriority()));
        log.setScheduledId(s.getSchedulerInstanceId());
        log.setScheduledName(s.getSchedulerName());
        log.setCreateDate(new Date());
        delete(context.getFireInstanceId());
        save(log);
    }
}