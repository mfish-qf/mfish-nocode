package cn.com.mfish.common.cloud.config;

import feign.Contract;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 定义FeignContract为springMvc方式
 * @author: mfish
 * @date: 2024/4/18
 */
@Configuration
public class FeignConfig {
    @Bean
    public Contract feignContract() {
//        return new feign.Contract.Default();
        return new SpringMvcContract();
    }
}
