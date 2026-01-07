package cn.com.mfish.common.prom.test;

import cn.com.mfish.common.prom.common.PromClientUtils;
import cn.com.mfish.common.prom.enums.MetricEnum;
import org.springframework.stereotype.Component;

/**
 * @description: 指标持有者
 * @author: mfish
 * @date: 2026/1/6
 */
@Component
public class MetricTest {

    /**
     * 测试用，模拟指标增长
     * 测试时放开PostConstruct注解
     */
//    @PostConstruct
    public void test() {
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                PromClientUtils.setValue(MetricEnum.MFISH_REQUEST_COUNT, i, "GET", "/api/test");
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
