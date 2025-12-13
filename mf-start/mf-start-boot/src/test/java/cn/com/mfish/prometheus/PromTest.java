package cn.com.mfish.prometheus;

import cn.com.mfish.common.prom.common.PromClientUtils;
import cn.com.mfish.common.prom.enums.StepUnit;
import cn.com.mfish.common.prom.result.PromResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

/**
 * @description: Prometheus 测试类
 * @author: mfish
 * @date: 2025/12/9
 */
@Slf4j
@SpringBootTest
@ComponentScan(basePackages = "cn.com.mfish")
@RunWith(SpringRunner.class)
public class PromTest {
    private final static String HOST = "http://localhost:9090";

    @Test
    public void testQueryRange() throws IOException {
        String promql = "node_cpu_seconds_total{mode=\"idle\",cpu=\"0\"}"; // 简单查询示例
        long sixHoursInMillis = 6L * 60 * 60 * 1000;
        Date start = new Date(System.currentTimeMillis() - sixHoursInMillis);
        try {
            PromResponse result = PromClientUtils.queryRange(HOST, promql, start, new Date(), 1, StepUnit.m);
            System.out.println(result);

            PromResponse result2 = PromClientUtils.query(HOST, promql);
            System.out.println(result2);
        } catch (IOException e) {
            log.error("查询victor metrics失败", e);
        }
    }
}
