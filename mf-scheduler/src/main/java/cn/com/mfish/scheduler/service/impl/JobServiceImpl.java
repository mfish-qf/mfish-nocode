package cn.com.mfish.scheduler.service.impl;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.common.SchedulerUtils;
import cn.com.mfish.scheduler.common.TriggerUtils;
import cn.com.mfish.scheduler.config.properties.SchedulerProperties;
import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.mapper.JobMapper;
import cn.com.mfish.scheduler.service.JobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V1.0.0
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {
    SchedulerFactoryBean mfSchedulerFactoryBean;

    public JobServiceImpl(@Autowired @Qualifier("mfSchedulerFactoryBean") SchedulerFactoryBean mfSchedulerFactoryBean) {
        this.mfSchedulerFactoryBean = mfSchedulerFactoryBean;
    }

    @Resource
    SchedulerProperties schedulerProperties;

    @Override
    @Transactional
    public Result<Job> insertJob(Job job) throws SchedulerException, ClassNotFoundException {
        Result<Job> result = validateJob(job);
        if (!result.isSuccess()) {
            return result;
        }
        if (baseMapper.insert(job) == 1) {
            SchedulerUtils.createScheduler(mfSchedulerFactoryBean.getScheduler(), job, schedulerProperties.isCover());
            return Result.ok(job, "定时调度任务-添加成功!");
        }
        return Result.fail(job, "错误:定时调度任务-添加失败!");
    }

    private Result<Job> validateJob(Job job) {
        if (!job.getCron().equals(TriggerUtils.SINGLE_TRIGGER) && !CronExpression.isValidExpression(job.getCron())) {
            return Result.fail(job, "cron表达式不正确");
        }
        return Result.ok("校验成功");
    }

}
