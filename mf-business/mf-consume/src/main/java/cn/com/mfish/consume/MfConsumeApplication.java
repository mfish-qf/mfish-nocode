package cn.com.mfish.consume;

import cn.com.mfish.common.app.annotation.AutoApp;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @description: 消费端
 * @author: mfish
 * @date: 2023/3/1 16:25
 */
@Slf4j
@AutoApp
@EnableFeignClients(basePackages = "cn.com.mfish")
public class MfConsumeApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfConsumeApplication.class, args);
        Utils.printServerRun(application);
    }
}
