package cn.com.mfish.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author qiufeng
 * @date 2020/2/13 17:29
 */
@ApiModel("用户全部信息")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=true)
public class SSOUser extends UserInfo {
    @ApiModelProperty("密码加密盐")
    private String salt;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("旧密码")
    private String oldPassword;
    @ApiModelProperty("微信openid")
    private String openid;
}
