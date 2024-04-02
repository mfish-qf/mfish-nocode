package cn.com.mfish.oauth.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 组织用户请求
 * @author: mfish
 * @date: 2024/4/2
 */
@Data
public class ReqOrgUser {
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("昵称--用于显示")
    private String nickname;
}
