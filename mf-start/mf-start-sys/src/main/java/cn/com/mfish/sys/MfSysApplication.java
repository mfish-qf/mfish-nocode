package cn.com.mfish.sys;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @description: 系统业务中心启动类
 * @date: 2022/9/2
 */

@Slf4j
@AutoCloud
public class MfSysApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfSysApplication.class, args);
        Utils.printServerRun(application);
    }
}
