package cn.com.mfish.prometheus;

import cn.com.mfish.common.prom.common.PromClientUtils;
import cn.com.mfish.common.prom.enums.StepUnit;
import cn.com.mfish.common.prom.req.PromDuration;
import cn.com.mfish.common.prom.req.ReqPromQuery;
import cn.com.mfish.common.prom.req.ReqPromQueryRange;
import cn.com.mfish.common.prom.result.PromResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
            PromResponse result = PromClientUtils.queryRange((ReqPromQueryRange) new ReqPromQueryRange().setStart(start).setEnd(new Date()).setStep(new PromDuration(1L, StepUnit.m)).setQuery(promql), HOST);
            System.out.println(result);

            PromResponse result2 = PromClientUtils.query((ReqPromQuery) new ReqPromQuery().setQuery(promql).setLookBackDelta(new PromDuration(1L, StepUnit.h)), HOST);
            System.out.println(result2);
        } catch (IOException e) {
            log.error("查询victor metrics失败", e);
        }
    }

    @Test
    public void getReqPromQuery() {
        ReqPromQueryRange reqPromQuery = new ReqPromQueryRange();
        reqPromQuery.setQuery("model_call_count{model_service_id=\"****\"}");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            reqPromQuery.setStart(sdf.parse("2025-12-31 00:00:00"));
            reqPromQuery.setEnd(sdf.parse("2025-12-31 23:59:59"));
        } catch (Exception e) {
            log.error("解析时间失败", e);
        }
        reqPromQuery.setStep(new PromDuration(5L, StepUnit.m));
        System.out.println(reqPromQuery);
    }
}
