package cn.com.mfish.common.cloud.config;

import feign.Contract;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: Feign全局配置，显式声明使用 SpringMvcContract 以支持 @RequestMapping 系列注解
 * @author: mfish
 * @date: 2024/4/18
 */
@Configuration
public class FeignConfig {

    /**
     * 使用 SpringMVC 契约解析 Feign 接口上的 @GetMapping / @PostMapping 等注解
     */
    @Bean
    public Contract feignContract() {
        return new SpringMvcContract();
    }
}
