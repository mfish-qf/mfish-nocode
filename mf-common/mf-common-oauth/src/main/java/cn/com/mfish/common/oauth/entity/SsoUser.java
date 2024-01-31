package cn.com.mfish.common.oauth.entity;

import cn.com.mfish.common.oauth.api.entity.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: mfish
 * @date: 2020/2/13 17:29
 */
@ApiModel("用户全部信息")
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
public class SsoUser extends UserInfo {
    @ApiModelProperty("密码加密盐")
    private String salt;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("旧密码")
    private String oldPassword;
    @ApiModelProperty("微信openid")
    private String openid;
}
