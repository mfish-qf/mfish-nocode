package cn.com.mfish.common.prom.result;

import lombok.Data;

/**
 * @description: prometheus查询响应类
 * @author: mfish
 * @date: 2025/12/9
 */
@Data
public class PromResponse {
    /**
     * 查询状态 （success 或 error）
     */
    private String status;
    /**
     * 查询数据
     */
    private PromData data;
}
