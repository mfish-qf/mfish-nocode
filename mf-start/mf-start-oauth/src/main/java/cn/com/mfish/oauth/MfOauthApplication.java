package cn.com.mfish.oauth;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @author: mfish
 * @description: 统一认证中心
 * @date: 2021/11/15 15:05
 */
@Slf4j
@AutoCloud
public class MfOauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfOauthApplication.class, args);
        log.info("""
                
                \t----------------------------------------------------------
                \t\
                
                \t--------------------摸鱼认证中心启动成功-----------------------
                \t""");
    }
}
