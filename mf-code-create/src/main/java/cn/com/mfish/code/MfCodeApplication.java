package cn.com.mfish.code;

import cn.com.mfish.common.web.annotation.AutoWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * @author ：qiufeng
 * @description：代码生成入口类
 * @date ：2022/8/18 16:42
 */
@Slf4j
@AutoWeb
public class MfCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfCodeApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼代码生成中心启动成功-----------------------\n\t");
    }
}
