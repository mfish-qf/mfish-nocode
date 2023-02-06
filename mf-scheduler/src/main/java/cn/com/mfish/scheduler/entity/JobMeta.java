package cn.com.mfish.scheduler.entity;

import lombok.Data;

import java.util.Map;

/**
 * @description: 任务基础信息
 * @author: qiufeng
 * @date: 2023/2/6 17:09
 */
@Data
public class JobMeta {
    /**
     * 名称
     */
    private String name;

    /**
     * 组别
     */
    private String group = "default";

    /**
     * 类型
     */
    private String className;

    /**
     * 描述
     */
    private String description = "暂无描述";

    /**
     * 是否可恢复
     */
    private Boolean recovery = false;

    /**
     * 是否持久化
     */
    private Boolean durability = true;

    /**
     * 数据
     */
    private Map<String, Object> dataMap;
}
