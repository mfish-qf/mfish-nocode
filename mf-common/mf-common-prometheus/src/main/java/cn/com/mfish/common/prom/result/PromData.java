package cn.com.mfish.common.prom.result;

import lombok.Data;

import java.util.List;

/**
 * @description: prometheus查询数据类
 * @author: mfish
 * @date: 2025/12/9
 */
@Data
public class PromData {
    /**
     * 查询结果类型
     */
    private String resultType;
    /**
     * 查询结果列表
     */
    private List<PromResult> result;
}
