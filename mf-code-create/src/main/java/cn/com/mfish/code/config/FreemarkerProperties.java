package cn.com.mfish.code.config;

import freemarker.template.Version;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: mfish
 * @description：freemarker模版keys配置
 * @date: 2022/8/30 16:15
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "code.template")
@Data
public class FreemarkerProperties {
    private final List<String> keys = new ArrayList<>();
    private final String path = "template";
    private final Version version = freemarker.template.Configuration.VERSION_2_3_31;
}
