package cn.com.mfish.oauth.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author: mfish
 * @description: 修改密码入参
 * @date: 2022/12/21 22:17
 */
@Schema(description = "修改密码入参")
@Data
public class ReqChangePwd {
    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "旧密码")
    private String oldPwd;
    @Schema(description = "新密码")
    private String newPwd;
}
