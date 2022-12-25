package cn.com.mfish.gateway;

import cn.com.mfish.common.core.annotation.AutoFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author qiufeng
 * @date 2021/8/11 11:44
 */
@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@AutoFeignClients
public class MfGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(cn.com.mfish.gateway.MfGatewayApplication.class, args);
        log.info("\n\t----------------------------------------------------------\n\t" +
                "\n\t--------------------摸鱼网关服务启动成功-----------------------\n\t");
    }
}
