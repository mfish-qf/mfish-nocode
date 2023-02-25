package cn.com.mfish.scheduler.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.common.TriggerUtils;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import cn.com.mfish.scheduler.mapper.JobSubscribeMapper;
import cn.com.mfish.scheduler.service.JobSubscribeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * @description: 任务订阅表
 * @author: mfish
 * @date: 2023-02-20
 * @version: V1.0.0
 */
@Service
public class JobSubscribeServiceImpl extends ServiceImpl<JobSubscribeMapper, JobSubscribe> implements JobSubscribeService {
    @Override
    public List<JobSubscribe> getSubscribesByJobId(String jobId) {
        return baseMapper.selectList(new LambdaQueryWrapper<JobSubscribe>()
                .eq(JobSubscribe::getJobId, jobId));
    }

    @Override
    public List<JobSubscribe> getSubscribesByJobIds(List<String> jobIds) {
        return baseMapper.getSubscribesByJobIds(jobIds);
    }

    @Override
    public int removeSubscribesByJobId(String jobId) {
        return baseMapper.delete(new LambdaQueryWrapper<JobSubscribe>()
                .eq(JobSubscribe::getJobId, jobId));
    }

    @Override
    public Result<JobSubscribe> insertJobSubscribe(JobSubscribe jobSubscribe) {
        Result<JobSubscribe> result = validateJobSubscribe(jobSubscribe);
        if (!result.isSuccess()) {
            throw new MyRuntimeException(result.getMsg());
        }
        if (baseMapper.insert(jobSubscribe) == 1) {
            return Result.ok(jobSubscribe, "任务订阅表-添加成功!");
        }
        throw new MyRuntimeException("错误:任务订阅表-添加失败!");
    }

    @Override
    public Result<List<JobSubscribe>> insertJobSubscribes(List<JobSubscribe> jobSubscribeList) {
        if (jobSubscribeList == null || jobSubscribeList.isEmpty()) {
            return Result.ok("无新增任务订阅列表");
        }
        for (JobSubscribe jobSubscribe : jobSubscribeList) {
            Result<JobSubscribe> result = validateJobSubscribe(jobSubscribe);
            if (!result.isSuccess()) {
                throw new MyRuntimeException(result.getMsg());
            }
        }
        if (baseMapper.insertJobSubscribes(jobSubscribeList) == jobSubscribeList.size()) {
            return Result.ok(jobSubscribeList, "任务订阅列表-添加成功!");
        }
        throw new MyRuntimeException("错误:任务订阅列表-添加失败!");
    }

    private Result<JobSubscribe> validateJobSubscribe(JobSubscribe jobSubscribe) {
        if (!jobSubscribe.getCron().equals(TriggerUtils.SINGLE_TRIGGER) && !CronExpression.isValidExpression(jobSubscribe.getCron())) {
            return Result.fail(jobSubscribe, MessageFormat.format("错误:cron表达式[{0}]不正确", jobSubscribe.getCron()));
        }
        return Result.ok("任务校验成功");
    }
}
