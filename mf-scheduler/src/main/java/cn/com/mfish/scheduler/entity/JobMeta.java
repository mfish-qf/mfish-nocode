package cn.com.mfish.scheduler.entity;

import lombok.Data;

import java.util.Map;

/**
 * @description: 任务基础信息
 * @author: mfish
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
     * 是否允许同步
     */
    private boolean allowConcurrent = false;

    /**
     * 描述
     */
    private String description = "暂无描述";

    /**
     * 是否可恢复(执行中应用发生故障，需要重新执行)
     * 此处暂时设置为false由trigger确定处理
     */
    private Boolean recovery = false;

    /**
     * 是否持久化(即使没有Trigger关联时，也不需要删除该JobDetail)
     */
    private Boolean durability = true;

    /**
     * 数据
     */
    private Map<String, Object> dataMap;
}
