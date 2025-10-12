package cn.com.mfish.common.workflow.api.entity;


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

}
