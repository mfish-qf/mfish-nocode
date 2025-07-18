package cn.com.mfish.scheduler.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.scheduler.config.utils.InvokeUtils;
import cn.com.mfish.scheduler.common.SchedulerUtils;
import cn.com.mfish.scheduler.config.properties.SchedulerProperties;
import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import cn.com.mfish.scheduler.execute.GeneralJobExecute;
import cn.com.mfish.scheduler.mapper.JobMapper;
import cn.com.mfish.scheduler.service.JobService;
import cn.com.mfish.scheduler.service.JobSubscribeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V2.0.1
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {
    @Resource
    SchedulerProperties schedulerProperties;
    @Resource
    JobSubscribeService jobSubscribeService;
    SchedulerFactoryBean mfSchedulerFactoryBean;

    public JobServiceImpl(@Autowired @Qualifier("mfSchedulerFactoryBean") SchedulerFactoryBean mfSchedulerFactoryBean) {
        this.mfSchedulerFactoryBean = mfSchedulerFactoryBean;
    }

    /**
     * 定时同步调度策略(测试时放开使用)
     *
     */
//    @Scheduled(cron = "0 0 1 * * ? *")
//    @PostConstruct
    public void initJob() throws SchedulerException {
        Scheduler scheduler = mfSchedulerFactoryBean.getScheduler();
        scheduler.clear();
        List<Job> list = baseMapper.selectList(Wrappers.emptyWrapper());
        for (Job job : list) {
            List<JobSubscribe> jobSubscribeList = jobSubscribeService.list(new LambdaQueryWrapper<JobSubscribe>()
                    .like(job.getId() != null, JobSubscribe::getJobId, job.getId()));
            SchedulerUtils.createScheduler(scheduler, job, jobSubscribeList, true);
        }
    }

    @Override
    @Transactional
    public Result<Job> insertJob(Job job) throws SchedulerException {
        Result<Job> result = verifyJob(job);
        if (!result.isSuccess()) {
            return result;
        }
        if (baseMapper.insert(job) == 1) {
            return updateTrigger(job);
        }
        return Result.fail(job, "错误:定时调度任务-添加失败!");
    }

    @Override
    @Transactional
    public Result<Job> updateJob(Job job) throws SchedulerException {
        Result<Job> result = verifyJob(job);
        if (!result.isSuccess()) {
            return result;
        }
        if (baseMapper.updateById(job) == 1) {
            return updateTrigger(job);
        }
        return Result.fail(job, "错误:定时调度任务-更新失败!");
    }

    /**
     * 校验任务
     *
     * @param job 任务
     * @return 返回结果
     */
    private Result<Job> verifyJob(Job job) {
        try {
            InvokeUtils.strParams2Obj(job.getParams());
        } catch (Exception ex) {
            return Result.fail(job, "错误:任务参数不正确");
        }
        return Result.ok("任务校验成功");
    }

    @Override
    @Transactional
    public Result<Boolean> deleteJob(String jobId) throws SchedulerException {
        Job job = baseMapper.selectById(jobId);
        if (baseMapper.deleteById(jobId) == 1) {
            deleteTrigger(job);
            return Result.ok(true, "定时调度任务-删除成功!");
        }
        return Result.fail(false, "错误:定时调度任务-删除失败!");
    }

    @Override
    public Result<Boolean> executeJob(Job job) {
        new GeneralJobExecute().execute(job, "");
        return Result.ok(true, "执行完成");
    }

    /**
     * 更新调度策略
     * <p>
     * 本方法首先删除现有的触发器，然后插入新的触发器以更新调度策略
     * 这种方式确保了调度策略的及时更新和一致性
     *
     * @param job 作业对象，包含作业和触发器的详细信息
     * @return 返回作业的执行结果
     * @throws SchedulerException 如果调度操作失败，抛出调度异常
     */
    private Result<Job> updateTrigger(Job job) throws SchedulerException {
        deleteTrigger(job);
        return insertTrigger(job);
    }

    /**
     * 插入触发策略
     *
     * @param job 作业对象，包含作业和触发器的详细信息
     * @return 返回作业的执行结果
     * @throws SchedulerException 如果调度操作失败，抛出调度异常
     */
    public Result<Job> insertTrigger(Job job) throws SchedulerException {
        List<JobSubscribe> list = job.getSubscribes();
        if (list != null && !list.isEmpty()) {
            for (JobSubscribe jobSubscribe : list) {
                jobSubscribe.setJobId(job.getId());
            }
        }
        Result<List<JobSubscribe>> result = jobSubscribeService.insertJobSubscribes(list);
        //任务状态为启用时，创建调度策略
        if (result.isSuccess()) {
            SchedulerUtils.createScheduler(mfSchedulerFactoryBean.getScheduler(), job, list, schedulerProperties.isCover());
            return Result.ok(job, "定时调度任务-创建成功!");
        }
        throw new MyRuntimeException("错误:新增策略失败");
    }

    /**
     * 删除触发策略
     *
     * @param job 作业对象，包含作业和触发器的详细信息
     * @throws SchedulerException 如果调度操作失败，抛出调度异常
     */
    private void deleteTrigger(Job job) throws SchedulerException {
        List<JobSubscribe> subscribes = jobSubscribeService.getSubscribesByJobId(job.getId());
        if (subscribes != null && !subscribes.isEmpty()) {
            if (jobSubscribeService.removeSubscribesByJobId(job.getId()) <= 0) {
                throw new MyRuntimeException("错误:移除旧策略失败");
            }
            SchedulerUtils.removeTrigger(mfSchedulerFactoryBean.getScheduler(), job, subscribes);
        }
    }

    @Override
    public Result<Boolean> setStatus(Job job) throws SchedulerException {
        if (baseMapper.updateById(new Job().setId(job.getId()).setStatus(job.getStatus())) == 1) {
            Job newJob = baseMapper.selectById(job.getId());
            List<JobSubscribe> subscribes = jobSubscribeService.getSubscribesByJobId(job.getId());
            if (newJob == null) {
                throw new MyRuntimeException("错误:修改状态失败-未获取到任务");
            }
            if (job.getStatus() == 1) {
                SchedulerUtils.pause(mfSchedulerFactoryBean.getScheduler(), newJob, subscribes);
            } else {
                SchedulerUtils.resume(mfSchedulerFactoryBean.getScheduler(), newJob, subscribes);
            }
            return Result.ok(true, "定时调度任务-设置状态成功!");
        }
        return Result.fail(false, "错误:定时调度任务-设置状态失败!");
    }
}
