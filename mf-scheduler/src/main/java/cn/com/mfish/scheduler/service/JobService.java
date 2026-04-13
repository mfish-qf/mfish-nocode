package cn.com.mfish.scheduler.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.entity.Job;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SchedulerException;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V2.3.1
 */
public interface JobService extends IService<Job> {
    /**
     * 新增定时任务
     *
     * @param job 定时任务对象
     * @return 操作结果
     * @throws SchedulerException 调度器异常
     * @throws ClassNotFoundException 类未找到异常
     */
    Result<Job> insertJob(Job job) throws SchedulerException, ClassNotFoundException;

    /**
     * 修改定时任务
     *
     * @param job 定时任务对象
     * @return 操作结果
     * @throws SchedulerException 调度器异常
     * @throws ClassNotFoundException 类未找到异常
     */
    Result<Job> updateJob(Job job) throws SchedulerException, ClassNotFoundException;

    /**
     * 删除定时任务
     *
     * @param jobId 任务 ID
     * @return 操作结果
     * @throws SchedulerException 调度器异常
     */
    Result<Boolean> deleteJob(String jobId) throws SchedulerException;

    /**
     * 执行定时任务
     *
     * @param job 定时任务对象
     * @return 操作结果
     */
    Result<Boolean> executeJob(Job job);

    /**
     * 设置任务状态
     *
     * @param job 定时任务对象
     * @return 操作结果
     * @throws SchedulerException 调度器异常
     * @throws ClassNotFoundException 类未找到异常
     */
    Result<Boolean> setStatus(Job job) throws SchedulerException, ClassNotFoundException;
}
