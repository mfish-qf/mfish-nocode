package cn.com.mfish.storage;

import cn.com.mfish.common.web.annotation.AutoWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @description: 摸鱼文件存储服务
 * @author: mfish
 * @date: 2023/1/5 16:34
 */
@Slf4j
@AutoWeb
public class MfStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfStorageApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼文件存储服务启动成功-----------------------\n\t");
    }
}
