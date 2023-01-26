package cn.com.mfish.monitor;

import cn.com.mfish.common.web.annotation.AutoWeb;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @description: 摸鱼监控中心启动类
 * @author: mfish
 * @date: 2023/1/26 23:11
 */
@AutoWeb
@EnableAdminServer
@Slf4j
public class MfMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfMonitorApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼监控中心启动成功-----------------------\n\t");
    }
}
