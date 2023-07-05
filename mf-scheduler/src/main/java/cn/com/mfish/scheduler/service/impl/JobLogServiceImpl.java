package cn.com.mfish.scheduler.service.impl;

import cn.com.mfish.common.scheduler.api.entity.JobLog;
import cn.com.mfish.scheduler.mapper.JobLogMapper;
import cn.com.mfish.scheduler.service.JobLogService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @description: 任务日志
 * @author: mfish
 * @date: 2023-02-14
 * @version: V1.1.0
 */
@Service
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLog> implements JobLogService {

}
