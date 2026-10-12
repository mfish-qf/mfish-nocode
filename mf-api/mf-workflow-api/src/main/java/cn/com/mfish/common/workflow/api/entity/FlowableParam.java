package cn.com.mfish.common.workflow.api.entity;


import cn.com.mfish.common.workflow.api.enums.FlowKey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: 工作流参数
 * @author: mfish
 * @date: 2025/9/16
 */
@Data
@Accessors(chain = true)
@Schema(description = "工作流参数")
public class FlowableParam<T> implements Serializable {
    //固定为改造前自动生成的值：默认 serialVersionUID 由类结构哈希而来（含静态方法），新增 of() 会改变哈希，导致库中在途流程变量反序列化失败
    private static final long serialVersionUID = 8899980303321350951L;

    @Schema(description = "流程定义key 来自FlowKey枚举", requiredMode = Schema.RequiredMode.REQUIRED)
    private String key;

    @Schema(description = "业务id", requiredMode = Schema.RequiredMode.REQUIRED)
    private T id;

    @Schema(description = "发起人账号")
    private String startAccount;

    @Schema(description = "回调接口前缀 微服务架构时候需要指定回调接口的前缀", requiredMode = Schema.RequiredMode.REQUIRED)
    private String prefix;

    @Schema(description = "回调接口 微服务采用Feign接口进行回调，需要Feign接口的全路径，例如：cn.com.mfish.common.nocode.api.remote.RemoteNocodeService", requiredMode = Schema.RequiredMode.REQUIRED)
    private String callback;

    @Schema(description = "其他参数")
    private Map<String, Object> param;

    /**
     * 按流程定义枚举创建工作流参数，流程key、回调前缀、回调接口均取自枚举配置
     *
     * @param flowKey 流程定义枚举
     * @param id      业务id
     * @param <T>     业务id类型
     * @return 工作流参数
     */
    public static <T> FlowableParam<T> of(FlowKey flowKey, T id) {
        return new FlowableParam<T>()
                .setKey(flowKey.toString())
                .setId(id)
                .setPrefix(flowKey.getPrefix())
                .setCallback(flowKey.getCallback());
    }

}
