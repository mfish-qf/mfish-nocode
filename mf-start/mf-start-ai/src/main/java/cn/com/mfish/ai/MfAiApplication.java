package cn.com.mfish.ai;

import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @description: AI服务（WebFlux版）
 * @date: 2025/08/15
 */
@Slf4j
@SpringBootApplication
@EnableFeignClients(basePackages = "cn.com.mfish")
@MapperScan({"cn.com.mfish.**.mapper"})
public class MfAiApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfAiApplication.class, args);
        Utils.printServerRun(application);
    }
}
