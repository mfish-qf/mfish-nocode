package cn.com.mfish.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description: openai配置
 * @author: mfish
 * @date: 2023/2/9 14:46
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "openai.chat")
@Data
public class OpenAiConfig {
    private String token = "***";
    private String url = "https://api.openai.com/v1/completions";
}
