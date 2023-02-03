package cn.com.mfish.scheduler.service.impl;

import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.mapper.JobMapper;
import cn.com.mfish.scheduler.service.JobService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V1.0.0
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

}
