package cn.com.mfish.code;

import cn.com.mfish.common.swagger.annotation.AutoSwagger;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ：qiufeng
 * @description：代码生成入口类
 * @date ：2022/8/18 16:42
 */
@SpringBootApplication
@AutoSwagger
@Slf4j
@MapperScan({"cn.com.mfish.code.mapper"})
public class MfCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfCodeApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼代码生成中心启动成功-----------------------\n\t");
    }
}
