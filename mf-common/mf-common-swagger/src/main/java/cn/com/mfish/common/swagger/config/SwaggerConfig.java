package cn.com.mfish.common.swagger.config;

import cn.com.mfish.common.core.constants.Constants;
import io.swagger.annotations.Api;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: mfish
 * @description: swagger配置类
 * @date: 2021/11/15 12:59
 */
@Configuration
@EnableOpenApi
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerConfig implements WebMvcConfigurer {
    /**
     * swagger接口设置
     *
     * @param swaggerProperties
     * @return
     */
    @Bean
    public Docket docket(SwaggerProperties swaggerProperties) {
        Docket docket = new Docket(DocumentationType.OAS_30)
                .enable(swaggerProperties.getEnabled())
                .apiInfo(apiInfo(swaggerProperties))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .apis(RequestHandlerSelectors.basePackage("com.mfish"))
                .paths(PathSelectors.any())
                .build();
        if (swaggerProperties.getNeedAuth()) {
            docket.securitySchemes(Collections.singletonList(securityScheme()))
                    .securityContexts(securityContexts());
        }
        return docket;
    }

    /***
     *
     * 请求认证模式配置，通过通过Authorization头请求头传递
     * @return
     */
    SecurityScheme securityScheme() {
        return new ApiKey(Constants.AUTHENTICATION, Constants.AUTHENTICATION, Constants.HEADER);
    }

    /**
     * 安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .operationSelector(o -> o.requestMappingPattern().matches("^(?!auth).*$"))
                        .build());
        return securityContexts;
    }

    /**
     * 默认的安全上下文引用
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{
                new AuthorizationScope("global", "accessEverything")};
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference(Constants.AUTHENTICATION, authorizationScopes));
        return securityReferences;
    }

    /**
     * 接口信息
     *
     * @param swaggerProperties
     * @return
     */
    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .license(swaggerProperties.getLicense())
                .licenseUrl(swaggerProperties.getLicenseUrl())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .contact(swaggerProperties.getContact().getContact())
                .version(swaggerProperties.getVersion())
                .build();
    }

    /**
     * swagger-ui 地址
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }

    /**
     * springboot升级后出现与swaggerfox3.0不兼容问题
     * 启动时错误问题，错误信息如下： Failed to start bean 'documentationPluginsBootstrapper'; nested exception is java.lang.NullPointerException问题
     * stackoverflow中提供解决方案：在nacos配置中增加下面配置，并未解决。通过重写过滤null数据
     * mvc:
     * pathmatch:
     * matching-strategy: ant_path_matcher
     * 上面的mvc:patnmatch等属性，也需要在配置中增加，否则会出现no operations defined in spec问题
     * @return
     */
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                    List<RequestMappingInfoHandlerMapping> list;
                    try {
                        Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                        field.setAccessible(true);
                        list = (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                    List<RequestMappingInfoHandlerMapping> filter = list.stream().filter(mapping -> mapping.getPatternParser() == null)
                            .collect(Collectors.toList());
                    list.clear();
                    list.addAll(filter);
                }
                return bean;
            }
        };
    }
}
