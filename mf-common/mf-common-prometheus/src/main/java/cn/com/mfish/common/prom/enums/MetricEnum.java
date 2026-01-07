package cn.com.mfish.common.prom.enums;

import lombok.Getter;

import java.util.List;


/**
 * @description: 指标枚举
 * @author: mfish
 * @date: 2025/12/26
 */
@Getter
public enum MetricEnum {
    UNKNOWN("unknown", "未知指标", List.of()),
    MFISH_REQUEST_COUNT("mfish_request_count", "请求总数", List.of("method", "path")),
    MFISH_REQUEST_DURATION("mfish_request_duration", "请求耗时", List.of("method", "path"));
    private final String name;
    private final String description;
    private final List<String> tags;

    MetricEnum(String name, String description, List<String> tags) {
        this.name = name;
        this.description = description;
        this.tags = tags;
    }

    /**
     * 根据指标名称获取枚举
     * @param name 指标名称
     * @return 指标枚举
     */
    public static MetricEnum getMetricEnum(String name) {
        for (MetricEnum metricEnum : values()) {
            if (metricEnum.getName().equals(name)) {
                return metricEnum;
            }
        }
        return UNKNOWN;
    }

}
