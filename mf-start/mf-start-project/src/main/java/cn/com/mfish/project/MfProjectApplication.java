package cn.com.mfish.project;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @description: 项目业务中心启动类
 * @date: 2026/10/15
 */

@Slf4j
@AutoCloud
public class MfProjectApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfProjectApplication.class, args);
        Utils.printServerRun(application);
    }
}
