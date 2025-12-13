package cn.com.mfish.common.prom.common;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.http.OkHttpUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.prom.enums.StepUnit;
import cn.com.mfish.common.prom.result.PromResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

/**
 * @description: Prometheus 客户端工具类
 * @author: mfish
 * @date: 2025/12/9
 */
@Slf4j
public class PromClientUtils {
    /**
     * 查询时间范围数据
     *
     * @param host     Prometheus 主机地址
     * @param promql   查询语句
     * @param start    开始时间
     * @param end      结束时间
     * @param step     时间步长
     * @param stepUnit 时间步长单位
     * @return 查询结果
     * @throws IOException IO 异常
     */
    public static PromResponse queryRange(String host, String promql, Date start, Date end, Integer step, StepUnit stepUnit) throws IOException {
        return queryRange(host, promql, start, end, step, stepUnit, null, null);
    }

    /**
     * 查询时间范围数据
     *
     * @param host     Prometheus 主机地址
     * @param promql   查询语句
     * @param start    开始时间
     * @param end      结束时间
     * @param step     时间步长
     * @param stepUnit 时间步长单位
     * @param account  账号
     * @param password 密码
     * @return 查询结果
     * @throws IOException IO 异常
     */
    public static PromResponse queryRange(String host, String promql, Date start, Date end, Integer step, StepUnit stepUnit, String account, String password) throws IOException {
        log.info("查询时间范围数据, promql: {}, start: {}, end: {}, step: {}, stepUnit: {}", promql, start, end, step, stepUnit);
        String queryParam = URLEncoder.encode(promql, StandardCharsets.UTF_8);
        String url = String.format("%s/api/v1/query_range?query=%s&start=%d&end=%d&step=%s",
                host, queryParam, start.toInstant().getEpochSecond(), end.toInstant().getEpochSecond(), step + stepUnit.getValue());
        return requestVM(url, account, password);
    }

    /**
     * 查询最新数据
     *
     * @param host   Prometheus 主机地址
     * @param promql 查询语句
     * @return 查询结果
     * @throws IOException IO 异常
     */
    public static PromResponse query(String host, String promql) throws IOException {
        return query(host, promql, null, null);
    }


    /**
     * 查询最新数据
     *
     * @param host     Prometheus 主机地址
     * @param promql   查询语句
     * @param account  账号
     * @param password 密码
     * @return 查询结果
     * @throws IOException IO 异常
     */
    public static PromResponse query(String host, String promql, String account, String password) throws IOException {
        log.info("查询最新数据, promql: {}", promql);
        String queryParam = URLEncoder.encode(promql, StandardCharsets.UTF_8);
        String url = String.format("%s/api/v1/query?query=%s",
                host, queryParam);
        return requestVM(url, account, password);
    }

    /**
     * 解析查询结果
     *
     * @param url 查询 url
     * @return 解析后的查询结果
     * @throws IOException IO 异常
     */
    private static PromResponse requestVM(String url, String account, String password) throws IOException {
        Result<String> result;
        if (StringUtils.isNotEmpty(account) && StringUtils.isNotEmpty(password)) {
            String auth = String.format("%s:%s", account, password);
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", authHeader);
            result = OkHttpUtils.get(url, headers);
        } else {
            result = OkHttpUtils.get(url);
        }
        if (!result.isSuccess()) {
            log.error("查询victor metrics失败, url: {}, result: {}", url, result);
            return null;
        }
        return JSON.parseObject(result.getData(), PromResponse.class);
    }
}
