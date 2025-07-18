package cn.com.mfish.common.oauth.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 用户信息
 * @Author: mfish
 * @date: 2022-11-13
 * @version: V2.0.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户信息请求参数")
public class ReqSsoUser {
    @Schema(description = "租户ID")
    private String tenantId;
    @Schema(description = "组织ID")
    private String orgId;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "昵称--用于显示")
    private String nickname;
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
}
