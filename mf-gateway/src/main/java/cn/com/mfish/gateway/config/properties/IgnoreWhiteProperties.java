package cn.com.mfish.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：qiufeng
 * @description：忽略地址白名单配置
 * @date ：2021/11/18 18:00
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.ignore")
@Data
public class IgnoreWhiteProperties {
    private List<String> whites = new ArrayList<>();
}
