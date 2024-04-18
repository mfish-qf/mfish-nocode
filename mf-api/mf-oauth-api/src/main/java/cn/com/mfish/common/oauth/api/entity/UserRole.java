package cn.com.mfish.common.oauth.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: mfish
 * @description: 用户角色
 * @date: 2022/12/1 20:55
 */
@Data
@Schema(description = "用户角色属性")
@Accessors(chain = true)
public class UserRole {
    @Schema(description = "角色ID")
    private String id;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "角色来源 0用户 1组织")
    private int source;
}
