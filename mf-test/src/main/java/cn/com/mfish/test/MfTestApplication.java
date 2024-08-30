package cn.com.mfish.test;

import cn.com.mfish.common.app.annotation.AutoApp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @author: mfish
 * @description: 摸鱼测试中心启动
 * @date: 2021/12/3 17:22
 */
@Slf4j
@AutoApp
public class MfTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfTestApplication.class, args);
        log.info("""
                
                \t----------------------------------------------------------
                \t\
                
                \t--------------------摸鱼测试中心启动成功-----------------------
                \t""");
    }
}
