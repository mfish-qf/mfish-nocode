package cn.com.mfish.nocode;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @description: 摸鱼无代码中心启动
 * @author: mfish
 * @date: 2023/7/18
 */
@Slf4j
@AutoCloud
public class MfNoCodeApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfNoCodeApplication.class, args);
        Utils.printServerRun(application);
    }
}
