package cn.com.mfish.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description: 用户组织
 * @author: mfish
 * @date: 2023/7/4
 */
@Data
@Schema(description = "用户组织")
public class UserOrg {
    @Schema(description = "组织ID")
    private String orgId;
    @Schema(description = "用户ID")
    private String userId;
}
