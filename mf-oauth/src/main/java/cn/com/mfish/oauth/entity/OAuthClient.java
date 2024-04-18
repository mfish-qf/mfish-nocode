package cn.com.mfish.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: mfish
 * @date: 2020/2/16 15:57
 */
@Schema(description = "接入客户端信息")
@Data
public class OAuthClient implements Serializable {
    @Schema(description = "客户端ID")
    private String clientId;
    @Schema(description = "客户端能访问的资源id集合")
    private String resourceIds;
    @Schema(description = "客户端密钥")
    private String clientSecret;
    @Schema(description = "指定client的权限范围")
    private String scope;
    @Schema(description = "认证方式 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token")
    private String grantTypes;
    @Schema(description = "客户端重定向url，authorization_code认证回调地址")
    private String redirectUrl;
    @Schema(description = "指定用户的权限范围")
    private String authorities;
    @Schema(description = "跳过授权页,默认true,适用于authorization_code模式")
    private Boolean autoApprove;
}
