package cn.com.mfish.scheduler.invoke;

import cn.com.mfish.common.scheduler.api.entity.JobLog;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: 消息队列任务
 * @author: mfish
 * @date: 2023/2/13 16:47
 */
@Slf4j
public class MQInvoke implements BaseInvoke {

    /**
     * 消息推送调度
     *
     * @param jobLog 任务日志
     * @param params 请求参数
     * @return 返回结果
     */
    @Override
    public <T> Object run(JobLog jobLog, List<T> params) {
        //todo 需要使用消息调度时打开，同时打开pom.xml中的rocketmq-spring-boot-starter
//        RocketMQTemplate rocketMQTemplate = SpringBeanFactory.getBean(RocketMQTemplate.class);
//        ProductProperties productProperties = SpringBeanFactory.getBean(ProductProperties.class);
//        return rocketMQTemplate.syncSend(productProperties.getTopic(), jobLog);
        return null;
    }
}
