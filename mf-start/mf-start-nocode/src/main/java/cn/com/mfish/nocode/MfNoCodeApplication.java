package cn.com.mfish.nocode;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @description: 摸鱼无代码中心启动
 * @author: mfish
 * @date: 2023/7/18
 */
@Slf4j
@AutoCloud
public class MfNoCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfNoCodeApplication.class, args);
        log.info("""
                \t----------------------------------------------------------
                \t\
                
                \t--------------------摸鱼无代码中心服务启动成功-----------------------
                \t""");
    }
}
