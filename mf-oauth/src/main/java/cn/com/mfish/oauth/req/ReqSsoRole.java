package cn.com.mfish.oauth.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @version: V2.1.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "角色信息表请求参数")
public class ReqSsoRole {
    @Schema(description = "租户ID")
    private String tenantId;
    @Schema(description = "组织ID")
    private String orgIds;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
}
