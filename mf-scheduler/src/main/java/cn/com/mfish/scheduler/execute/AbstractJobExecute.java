package cn.com.mfish.scheduler.execute;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.scheduler.api.entity.JobLog;
import cn.com.mfish.common.scheduler.config.enums.JobStatus;
import cn.com.mfish.common.scheduler.config.utils.InvokeUtils;
import cn.com.mfish.scheduler.common.JobUtils;
import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import cn.com.mfish.scheduler.enums.JobType;
import cn.com.mfish.scheduler.invoke.BaseInvoke;
import cn.com.mfish.scheduler.service.JobLogService;
import cn.com.mfish.scheduler.service.JobSubscribeService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.TriggerKey;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: 抽象job执行
 * @author: mfish
 * @date: 2023/2/7 10:41
 */
@Slf4j
public abstract class AbstractJobExecute extends QuartzJobBean {
    private static ThreadLocal<JobLog> threadLocal = new ThreadLocal<>();
    private static final String OPERATOR = "scheduler_robot";

    /**
     * 执行job
     * 参数支持普通对象数组类型 例如:["****",11] 和 invokeParams对象数组类型
     * 例如：[{"type":"java.lang.String","value":"inner"},{"type":"cn.com.mfish.sys.api.entity.SysLog","value":{"title":"aaaa"}}]
     *
     * @param context
     */
    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobMap = context.getMergedJobDataMap();
        TriggerKey key = context.getTrigger().getKey();
        Job job = JSON.parseObject(jobMap.get(JobUtils.JOB_DATA_MAP).toString(), Job.class);
        //triggerName是订阅ID
        execute(job, key.getName());
    }

    /**
     * 执行任务
     *
     * @param job         任务
     * @param subscribeId 手动执行订阅ID传空字符串
     */
    public void execute(Job job, String subscribeId) {
        List<?> list = InvokeUtils.strParams2Obj(job.getParams());
        beginExecute(job, subscribeId);
        boolean success = false;
        String error = null;
        try {
            execute(JobType.getJob(job.getJobType()), threadLocal.get(), list);
            success = true;
        } catch (Exception ex) {
            log.error(MessageFormat.format("任务:{0}-执行状态:异常-任务ID:{1}-订阅ID:{2}", job.getJobName(), job.getId(), subscribeId), ex);
            success = false;
            error = ex.getMessage();
        } finally {
            executeCallBack(success, error);
        }
    }

    /**
     * 任务开始执行
     *
     * @param job
     * @return
     */
    private static void beginExecute(Job job, String subscribeId) {
        JobLog jobLog = new JobLog().setId(Utils.uuid32())
                .setJobId(job.getId())
                .setSubscribeId(subscribeId)
                .setJobGroup(job.getJobGroup())
                .setJobName(job.getJobName())
                .setJobType(job.getJobType())
                .setClassName(job.getClassName())
                .setMethodName(job.getMethodName())
                .setParams(job.getParams())
                .setStatus(JobStatus.开始.getValue())
                .setLogType(job.getLogType());
        jobLog.setCreateBy(OPERATOR).setCreateTime(new Date());
        log.info(MessageFormat.format("任务:{0}-执行状态:{1}-任务ID:{2}-订阅ID:{3}", jobLog.getJobName(), jobLog.getStatus(), jobLog.getId(), jobLog.getSubscribeId()));
        threadLocal.set(jobLog);
        if (1 == jobLog.getLogType()) {
            return;
        }
        JobSubscribeService jobSubscribeService = SpringBeanFactory.getBean(JobSubscribeService.class);
        JobSubscribe jobSubscribe = jobSubscribeService.getById(subscribeId);
        if (jobSubscribe != null) {
            jobLog.setCron(jobSubscribe.getCron());
            jobLog.setStartTime(jobSubscribe.getStartTime());
            jobLog.setEndTime(jobSubscribe.getEndTime());
        }
        JobLogService jobLogService = SpringBeanFactory.getBean(JobLogService.class);
        if (jobLogService.save(jobLog)) {
            log.info(MessageFormat.format("任务:{0}调度成功,任务ID:{1}", job.getJobName(), job.getId()));
            return;
        }
        log.error(MessageFormat.format("任务:{0}调度失败,任务ID:{1}", job.getJobName(), job.getId()));
    }

    /**
     * 任务回调
     *
     * @param success 任务是否成功
     * @param remark  错误信息
     */
    private static void executeCallBack(boolean success, String remark) {
        JobLog jobLog = threadLocal.get();
        threadLocal.remove();
        if (jobLog == null) {
            log.error("错误:未获取到任务日志");
            return;
        }
        JobStatus jobStatus = success ? JobStatus.调度成功 : JobStatus.调度失败;
        log.info(MessageFormat.format("任务:{0}-执行状态:{1}-任务ID:{2}-订阅ID:{3}", jobLog.getJobName(), jobStatus, jobLog.getId(), jobLog.getSubscribeId()));
        if (1 == jobLog.getLogType()) {
            return;
        }
        JobLogService jobLogService = SpringBeanFactory.getBean(JobLogService.class);
        //数据库设置长度1000，截取999位
        if (remark != null && remark.length() >= 1000) {
            remark = remark.substring(0, 999);
        }
        jobLog.setStatus(jobStatus.getValue()).setCostTime(new Date().getTime() - jobLog.getCreateTime().getTime()).setRemark(remark);
        jobLog.setUpdateBy(OPERATOR);
        jobLogService.updateById(jobLog);
    }

    protected abstract <T> void execute(BaseInvoke baseJob, JobLog jobLog, List<T> params);
}
