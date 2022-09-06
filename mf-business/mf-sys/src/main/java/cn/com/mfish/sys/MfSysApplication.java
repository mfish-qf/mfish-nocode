package cn.com.mfish.sys;

import cn.com.mfish.common.core.annotation.AutoFeignClients;
import cn.com.mfish.common.swagger.annotation.AutoSwagger;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ：qiufeng
 * @description：系统管理启动类
 * @date ：2022/9/2 16:03
 */
@SpringBootApplication
@AutoSwagger
@Slf4j
@MapperScan({"cn.com.mfish.**.mapper"})
@AutoFeignClients
public class MfSysApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfSysApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼代码生成中心启动成功-----------------------\n\t");
    }
}
