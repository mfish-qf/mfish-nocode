package cn.com.mfish.common.ai.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Feign工具自动配置
 * <p>
 * 自动注册FeignToolRegistry和FeignToolScanner，
 * 在Spring上下文中的所有@FeignClient Bean初始化完成后自动扫描并生成Spring AI Tool。
 * <p>
 * 通过META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports注册。
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClient")
public class FeignToolAutoConfiguration {

    @Bean
    public FeignToolRegistry feignToolRegistry() {
        return new FeignToolRegistry();
    }

    @Bean
    public FeignToolScanner feignToolScanner(ApplicationContext applicationContext, FeignToolRegistry feignToolRegistry) {
        return new FeignToolScanner(applicationContext, feignToolRegistry);
    }
}
