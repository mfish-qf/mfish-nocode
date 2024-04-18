package cn.com.mfish.common.oauth.req;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 组织用户请求
 * @author: mfish
 * @date: 2024/4/2
 */
@Data
@Accessors(chain = true)
public class ReqOrgUser {
    @Schema(description = "账号")
    private String account;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "昵称--用于显示")
    private String nickname;
}
