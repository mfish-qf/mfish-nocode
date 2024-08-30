package cn.com.mfish;

import cn.com.mfish.common.web.annotation.AutoWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @description: 摸鱼低代码单应用启动类
 * @author: mfish
 * @date: 2024/1/26
 */
@Slf4j
@AutoWeb
public class MfNoCodeStart {
    public static void main(String[] args) {
        SpringApplication.run(MfNoCodeStart.class, args);
        log.info("""
                
                \t----------------------------------------------------------
                \t\
                
                \t--------------------摸鱼低代码平台启动成功-----------------------
                \t""");
    }
}
