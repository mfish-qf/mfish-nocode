package cn.com.mfish.oauth;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @description: 统一认证中心
 * @date: 2021/11/15 15:05
 */
@Slf4j
@AutoCloud
public class MfOauthApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application= SpringApplication.run(MfOauthApplication.class, args);
        Utils.printServerRun(application);
    }
}
