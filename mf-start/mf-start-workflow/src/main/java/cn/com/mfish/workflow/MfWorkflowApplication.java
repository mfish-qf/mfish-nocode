package cn.com.mfish.workflow;

import cn.com.mfish.common.cloud.annotation.AutoCloud;
import cn.com.mfish.common.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: mfish
 * @description: 工作流中心启动类
 * @date: 2022/9/2
 */

@Slf4j
@AutoCloud
public class MfWorkflowApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(MfWorkflowApplication.class, args);
        Utils.printServerRun(application);
    }
}
