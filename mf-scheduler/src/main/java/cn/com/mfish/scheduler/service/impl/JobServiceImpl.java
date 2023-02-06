package cn.com.mfish.scheduler.service.impl;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.common.SchedulerUtils;
import cn.com.mfish.scheduler.common.TriggerUtils;
import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.mapper.JobMapper;
import cn.com.mfish.scheduler.service.JobService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.CronExpression;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V1.0.0
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Resource
    Scheduler scheduler;

    @PostConstruct
    public void initScheduler() throws SchedulerException {
        scheduler.clear();
        List<Job> jobList = baseMapper.selectList(Wrappers.emptyWrapper());
        for (Job job : jobList) {
            SchedulerUtils.createScheduler(scheduler, job);
        }
    }


    @Override
    public Result<Job> insertJob(Job job) {
        Result<Job> result = validateJob(job);
        if (!result.isSuccess()) {
            return result;
        }
        if (baseMapper.insert(job) == 1) {

            return Result.ok(job, "定时调度任务-添加成功!");
        }
        return Result.fail(job, "错误:定时调度任务-添加失败!");
    }

    private Result<Job> validateJob(Job job) {
        if (!job.getCron().equals(TriggerUtils.SINGLE_TRIGGER) && !CronExpression.isValidExpression(job.getCron())) {
            Result.fail(job, "cron表达式不正确");
        }
        return Result.ok("校验成功");
    }

}
