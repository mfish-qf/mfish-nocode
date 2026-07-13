package cn.com.mfish.common.ai.entity;

import cn.com.mfish.common.core.constants.ServiceConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: AI路由返回
 * @author: mfish
 * @date: 2025/8/22
 */
@Data
@Schema(description = "AI路由对象")
@Accessors(chain = true)
public class AiRouterVo {
    @Schema(description = "路由路径")
    private String path = "/ai/agent/chat";
    @Schema(description = "路由名称")
    private String name = "摸鱼小助手";
    @Schema(description = "目标微服务ID，如mf-sys、mf-oauth，用于Gateway动态路由")
    private String serviceId = ServiceConstants.MfService.SYS.getValue();
}
