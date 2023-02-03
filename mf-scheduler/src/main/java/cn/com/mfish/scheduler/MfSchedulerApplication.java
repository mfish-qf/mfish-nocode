package cn.com.mfish.scheduler;

import cn.com.mfish.common.web.annotation.AutoWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @description: 调度中心启动类
 * @author: mfish
 * @date: 2023/2/3 15:15
 */
@AutoWeb
@Slf4j
public class MfSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfSchedulerApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼调度中心启动成功-----------------------\n\t");
    }
}
