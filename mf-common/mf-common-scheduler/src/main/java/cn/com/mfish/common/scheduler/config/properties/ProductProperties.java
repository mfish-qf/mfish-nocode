package cn.com.mfish.common.scheduler.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description: rocketMq属性配置
 * @author: mfish
 * @date: 2023/3/1 11:44
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq.producer")
@RefreshScope
public class ProductProperties {
    /**
     * 分组
     */
    private String group;
    /**
     * 主题
     */
    private String topic;
    /**
     * 消息超时时间
     */
    private Long sendMessageTimeout;
}
