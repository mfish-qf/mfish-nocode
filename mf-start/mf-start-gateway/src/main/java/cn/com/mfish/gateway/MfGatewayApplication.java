package cn.com.mfish.gateway;

import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @date: 2021/8/11 11:44
 */
@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients
public class MfGatewayApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(cn.com.mfish.gateway.MfGatewayApplication.class, args);
        Utils.printServerRun(application);
    }
}
