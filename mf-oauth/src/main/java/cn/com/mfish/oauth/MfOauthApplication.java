package cn.com.mfish.oauth;

import cn.com.mfish.common.web.annotation.AutoWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @author ：qiufeng
 * @description：统一认证中心
 * @date ：2021/11/15 15:05
 */
@Slf4j
@AutoWeb
public class MfOauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfOauthApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼认证中心启动成功-----------------------\n\t");
    }
}
