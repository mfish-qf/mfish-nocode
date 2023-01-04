package cn.com.mfish.test;

import cn.com.mfish.common.core.annotation.AutoFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: mfish
 * @description: 摸鱼测试中心启动
 * @date: 2021/12/3 17:22
 */
@SpringBootApplication
@Slf4j
@AutoFeignClients
public class MfTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfTestApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼测试中心启动成功-----------------------\n\t");
    }
}
