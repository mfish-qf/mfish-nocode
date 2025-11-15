package cn.com.mfish.test;

import cn.com.mfish.common.app.annotation.AutoApp;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @description: 摸鱼测试中心启动
 * @date: 2021/12/3 17:22
 */
@Slf4j
@AutoApp
@EnableFeignClients(basePackages = "cn.com.mfish")
public class MfTestApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfTestApplication.class, args);
        Utils.printServerRun(application);
    }
}
