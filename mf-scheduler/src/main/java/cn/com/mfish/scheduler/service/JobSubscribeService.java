package cn.com.mfish.scheduler.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * @description: 任务订阅表
 * @author: mfish
 * @date: 2023-02-20
 * @version: V2.3.1
 */
public interface JobSubscribeService extends IService<JobSubscribe> {

    /**
     * 根据任务 ID 获取订阅列表
     *
     * @param jobId 任务 ID
     * @return 任务订阅列表
     */
    List<JobSubscribe> getSubscribesByJobId(String jobId);

    /**
     * 根据任务 ID 列表获取订阅列表
     *
     * @param jobIds 任务 ID 列表
     * @return 任务订阅列表
     */
    List<JobSubscribe> getSubscribesByJobIds(List<String> jobIds);

    /**
     * 根据任务 ID 删除订阅
     *
     * @param jobId 任务 ID
     * @return 删除的订阅数量
     */
    int removeSubscribesByJobId(String jobId);

    /**
     * 新增任务订阅
     *
     * @param jobSubscribe 任务订阅对象
     * @return 操作结果
     */
    Result<JobSubscribe> insertJobSubscribe(JobSubscribe jobSubscribe);

    /**
     * 批量新增任务订阅
     *
     * @param jobSubscribeList 任务订阅对象列表
     * @return 操作结果
     */
    Result<List<JobSubscribe>> insertJobSubscribes(List<JobSubscribe> jobSubscribeList);

    /**
     * 设置任务订阅状态
     *
     * @param jobSubscribe 任务订阅对象
     * @return 操作结果
     * @throws SchedulerException 调度器异常
     */
    Result<Boolean> setStatus(JobSubscribe jobSubscribe) throws SchedulerException;
}
