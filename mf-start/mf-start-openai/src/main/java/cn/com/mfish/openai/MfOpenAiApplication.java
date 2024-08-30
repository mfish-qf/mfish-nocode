package cn.com.mfish.openai;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @description: 摸鱼聊天机器人
 * @author: mfish
 * @date: 2023/2/8
 */
@AutoCloud
@Slf4j
public class MfOpenAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfOpenAiApplication.class, args);
        log.info("""
                
                \t----------------------------------------------------------
                \t\
                
                \t--------------------摸鱼聊天机器人启动成功-----------------------
                \t""");
    }
}
