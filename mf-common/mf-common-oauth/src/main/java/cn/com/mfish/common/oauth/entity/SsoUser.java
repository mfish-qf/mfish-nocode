package cn.com.mfish.common.oauth.entity;

import cn.com.mfish.common.oauth.api.entity.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: mfish
 * @date: 2020/2/13 17:29
 */
@Schema(description = "用户全部信息")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SsoUser extends UserInfo {
    @Schema(description = "密码加密盐")
    private String salt;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "旧密码")
    private String oldPassword;
    @Schema(description = "微信openid")
    private String openid;
}
