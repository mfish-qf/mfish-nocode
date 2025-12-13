package cn.com.mfish.common.prom.result;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: prometheus查询结果类
 * @author: mfish
 * @date: 2025/12/9
 */
@Data
public class PromResult {

    /**
     * 指标的标签（如 job, instance, method 等）
     */
    private Map<String, String> metric;

    /**
     * 指标的数值（如果是 instant 查询）
     * 格式: [时间戳, "数值字符串"]
     * 时间戳: 秒级精度的浮点数
     * 数值字符串: 指标的实际数值，可能是浮点数或整数
     * 注意：如果是 range 查询，value 会是 null，而 values 会包含多个 [时间戳, "数值字符串"] 数组
     */
    private List<Object> value;

    /**
     * 如果是 range 查询，返回的是 values (复数)
     */
    private List<List<Object>> values;
}
