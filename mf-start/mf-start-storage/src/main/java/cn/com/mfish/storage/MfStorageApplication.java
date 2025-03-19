package cn.com.mfish.storage;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @description: 摸鱼文件中心启动类
 * @author: mfish
 * @date: 2023/1/5 16:34
 */
@Slf4j
@AutoCloud
public class MfStorageApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfStorageApplication.class, args);
        Utils.printServerRun(application);
    }
}
