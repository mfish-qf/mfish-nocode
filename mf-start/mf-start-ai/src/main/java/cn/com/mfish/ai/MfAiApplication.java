package cn.com.mfish.ai;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @description: AI服务
 * @date: 2025/08/15
 */
@Slf4j
@AutoCloud
public class MfAiApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfAiApplication.class, args);
        Utils.printServerRun(application);
    }
}
