package cn.com.mfish.demo;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @description: 其他web业务服务参考类
 * @date: 2022/12/16 10:01
 */
@Slf4j
@AutoCloud
public class MfDemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfDemoApplication.class, args);
        Utils.printServerRun(application);
    }
}
