package cn.com.mfish.scheduler.invoke;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.scheduler.config.properties.ProductProperties;
import cn.com.mfish.common.scheduler.api.entity.JobLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

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
     * @param jobLog
     * @param params
     * @return
     */
    @Override
    public <T> Object run(JobLog jobLog, List<T> params) {
        RocketMQTemplate rocketMQTemplate = SpringBeanFactory.getBean(RocketMQTemplate.class);
        ProductProperties productProperties = SpringBeanFactory.getBean(ProductProperties.class);
        return rocketMQTemplate.syncSend(productProperties.getTopic(), jobLog);
    }
}
