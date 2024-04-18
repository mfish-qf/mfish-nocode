package cn.com.mfish.common.swagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author: mfish
 * @description: swagger基本属性
 * @date: 2021/11/22 17:22
 */
@Configuration
@ConfigurationProperties(prefix = "swagger")
@Data
@RefreshScope
public class SwaggerProperties {
    /**
     * 是否开启swagger
     */
    private Boolean enabled = true;
    private String title = "摸鱼框架";
    private String description = "摸鱼框架接口文档";
    private String termsOfServiceUrl = "";
    private String license = "";
    private String licenseUrl = "";
    private String version = "版本号:V1.3.0";
    private MyContact contact;
    //是否需要头部验证 默认：true(需要)
    private Boolean needAuth = true;
}
