package cn.com.mfish.common.prom.common;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.http.OkHttpUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.prom.enums.MetricEnum;
import cn.com.mfish.common.prom.req.ReqPromQuery;
import cn.com.mfish.common.prom.req.ReqPromQueryRange;
import cn.com.mfish.common.prom.result.PromResponse;
import com.alibaba.fastjson2.JSON;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description: Prometheus 客户端工具类
 * @author: mfish
 * @date: 2025/12/9
 */
@Slf4j
public class PromClientUtils {

    private static final Map<String, AtomicReference<Double>> VALUE_MAP = new ConcurrentHashMap<>();

    /**
     * 查询时间范围数据
     *
     * @param reqPromQueryRange 查询参数
     * @param host              Prometheus 主机地址
     * @return 查询结果
     */
    public static PromResponse queryRange(ReqPromQueryRange reqPromQueryRange, String host) throws IOException {
        return queryRange(reqPromQueryRange, host, null, null);
    }

    /**
     * 查询时间范围数据
     *
     * @param reqPromQueryRange 查询参数
     * @param host              Prometheus 主机地址
     * @param account           账号
     * @param password          密码
     * @return 查询结果
     * @throws IOException IO 异常
     */
    public static PromResponse queryRange(ReqPromQueryRange reqPromQueryRange, String host, String account, String password) throws IOException {
        return requestVM(host + reqPromQueryRange.toString(), account, password);
    }

    /**
     * 查询最新数据
     *
     * @param reqPromQuery 查询参数
     * @param host         Prometheus 主机地址
     * @return 查询结果
     * @throws IOException IO 异常
     */
    public static PromResponse query(ReqPromQuery reqPromQuery, String host) throws IOException {
        return query(reqPromQuery, host, null, null);
    }


    /**
     * 查询最新数据
     *
     * @param reqPromQuery 查询参数
     * @param host         Prometheus 主机地址
     * @param account      账号
     * @param password     密码
     * @return 查询结果
     * @throws IOException IO 异常
     */
    public static PromResponse query(ReqPromQuery reqPromQuery, String host, String account, String password) throws IOException {
        return requestVM(host + reqPromQuery.toString(), account, password);
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
            log.error("查询 Prometheus 指标失败, url: {}, result: {}", url, result);
            return null;
        }
        return JSON.parseObject(result.getData(), PromResponse.class);
    }

    /**
     * 设置指标值
     *
     * @param metricEnum  指标枚举
     * @param metricValue 指标值
     * @param tagValues 标签值
     */
    public static void setValue(MetricEnum metricEnum, double metricValue, String... tagValues) {
        if (tagValues.length != metricEnum.getTags().size()) {
            throw new MyRuntimeException("标签数量不匹配, 指标: " + metricEnum.getName());
        }
        String key = buildKey(metricEnum, tagValues);
        AtomicReference<Double> valueRef = VALUE_MAP.computeIfAbsent(key, k -> {
            AtomicReference<Double> ref = new AtomicReference<>(0d);
            Gauge.builder(metricEnum.getName(), ref, AtomicReference::get)
                    .description(metricEnum.getDescription())
                    .tags(buildTags(metricEnum, tagValues))
                    .register(SpringBeanFactory.getBean(MeterRegistry.class));
            return ref;
        });
        valueRef.set(metricValue);
    }

    /**
     * 指标值增加 1
     * @param metricEnum 指标枚举
     * @param tagValues 标签值
     */
    public static void increment(MetricEnum metricEnum, String... tagValues) {
        Counter counter = Counter.builder(metricEnum.getName())
                .description(metricEnum.getDescription())
                .tags(buildTags(metricEnum, tagValues))
                .register(SpringBeanFactory.getBean(MeterRegistry.class));
        counter.increment();
    }

    /**
     * 构建指标键
     *
     * @param metricEnum  指标枚举
     * @param tagValues 标签值
     * @return 指标键
     */
    private static String buildKey(MetricEnum metricEnum, String... tagValues) {
        StringBuilder sb = new StringBuilder(metricEnum.getName());
        sb.append("{");
        for (int i = 0; i < tagValues.length; i++) {
            sb.append(metricEnum.getTags().get(i)).append("=\"").append(tagValues[i]).append("\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    /**
     * 构建指标标签
     *
     * @param metricEnum  指标枚举
     * @param tagValues 标签值
     * @return 指标标签
     */
    private static Tags buildTags(MetricEnum metricEnum, String... tagValues) {
        Tags tags = Tags.empty();
        for (int i = 0; i < tagValues.length; i++) {
            tags = tags.and(metricEnum.getTags().get(i), tagValues[i]);
        }
        return tags;
    }

}
