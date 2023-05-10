package cn.com.mfish.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: mfish
 * @date: 2020/2/16 15:57
 */
@ApiModel("接入客户端信息")
@Data
public class OAuthClient implements Serializable {
    @ApiModelProperty("客户端ID")
    private String clientId;
    @ApiModelProperty("客户端能访问的资源id集合")
    private String resourceIds;
    @ApiModelProperty("客户端密钥")
    private String clientSecret;
    @ApiModelProperty("指定client的权限范围")
    private String scope;
    @ApiModelProperty("认证方式 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token")
    private String grantTypes;
    @ApiModelProperty("客户端重定向url，authorization_code认证回调地址")
    private String redirectUrl;
    @ApiModelProperty("指定用户的权限范围")
    private String authorities;
    @ApiModelProperty("跳过授权页,默认true,适用于authorization_code模式")
    private boolean autoApprove;
}
