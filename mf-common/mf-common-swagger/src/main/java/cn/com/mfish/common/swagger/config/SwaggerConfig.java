package cn.com.mfish.common.swagger.config;

import cn.com.mfish.common.core.constants.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: mfish
 * @description: swagger配置类
 * @date: 2021/11/15 12:59
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerConfig implements WebMvcConfigurer {
    /**
     * swagger接口设置
     *
     * @param swaggerProperties swagger属性
     * @return
     */
    @Bean
    public OpenAPI openAPIBean(SwaggerProperties swaggerProperties) {
        return new OpenAPI().info(apiInfo(swaggerProperties))
                .externalDocs(new ExternalDocumentation()
                        .description(swaggerProperties.getDescription())
                        .url(swaggerProperties.getTermsOfServiceUrl()))
                .addSecurityItem(new SecurityRequirement().addList(Constants.AUTHENTICATION))
                .components(new Components().addSecuritySchemes(Constants.AUTHENTICATION
                        , new SecurityScheme().name(Constants.AUTHENTICATION)
                                .in(SecurityScheme.In.HEADER).type(SecurityScheme.Type.HTTP).scheme("Bearer")));
    }

    /**
     * 接口信息
     *
     * @param swaggerProperties swagger属性
     * @return
     */
    private Info apiInfo(SwaggerProperties swaggerProperties) {
        return new Info()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .license(new License().name(swaggerProperties.getLicense()).url(swaggerProperties.getLicenseUrl()))
                .contact(swaggerProperties.getContact().getContact())
                .termsOfService(swaggerProperties.getTermsOfServiceUrl())
                .version(swaggerProperties.getVersion());
    }

}
