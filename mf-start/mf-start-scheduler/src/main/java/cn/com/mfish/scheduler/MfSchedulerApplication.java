package cn.com.mfish.scheduler;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @description: 调度中心启动类
 * @author: mfish
 * @date: 2023/2/3 15:15
 */
@AutoCloud
@Slf4j
@EnableScheduling
public class MfSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfSchedulerApplication.class, args);
        log.info("""
                
                \t----------------------------------------------------------
                \t\
                
                \t--------------------摸鱼调度中心启动成功-----------------------
                \t""");
    }
}
