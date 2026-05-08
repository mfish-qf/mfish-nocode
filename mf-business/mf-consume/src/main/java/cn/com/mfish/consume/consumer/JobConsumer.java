package cn.com.mfish.consume.consumer;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.scheduler.api.entity.JobLog;
import cn.com.mfish.common.scheduler.api.remote.RemoteSchedulerService;
import cn.com.mfish.common.scheduler.config.enums.JobStatus;
import cn.com.mfish.common.scheduler.config.utils.InvokeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @description: 任务消费端
 * @author: mfish
 * @date: 2023/3/1 16:34
 */
@Slf4j
@Service
@RocketMQMessageListener(nameServer = "${rocketmq.consumer.nameServer}", topic = "${rocketmq.consumer.topic}", consumerGroup = "${rocketmq.consumer.group}")
public class JobConsumer implements RocketMQListener<JobLog> {
    /** 远程调度服务 */
    @Resource
    RemoteSchedulerService remoteSchedulerService;

    /**
     * 接收并执行RocketMQ中的定时任务消息
     * <p>通过反射调用任务指定的类和方法，执行完成后回调更新任务状态</p>
     *
     * @param jobLog 任务日志对象，包含任务执行的类名、方法名和参数等信息
     */
    @Override
    public void onMessage(JobLog jobLog) {
        JobStatus jobStatus = JobStatus.执行成功;
        String error = null;
        try {
            List<?> params = InvokeUtils.strParams2Obj(jobLog.getParams());
            Object obj = InvokeUtils.invokeMethod(jobLog.getClassName(), jobLog.getMethodName(), params);
            log.info("返回结果:" + obj);
        } catch (InvocationTargetException e) {
            error = e.getTargetException().getMessage();
            log.error("错误:任务执行异常", e);
            jobStatus = JobStatus.执行失败;
        } catch (Exception e) {
            error = e.getMessage();
            log.error("错误:任务执行异常", e);
            jobStatus = JobStatus.执行失败;
        } finally {
            remoteSchedulerService.callBackStatus(RPCConstants.INNER, jobLog.setStatus(jobStatus.getValue()).setRemark(error));
        }

    }
}
