package cn.com.mfish.scheduler.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.entity.Job;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SchedulerException;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V1.0.0
 */
public interface JobService extends IService<Job> {
    Result<Job> insertJob(Job job) throws SchedulerException, ClassNotFoundException;
}
