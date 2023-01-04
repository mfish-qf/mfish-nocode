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
    @ApiModelProperty("授权码模式:authorization_code,密码模式:password,刷新token: refresh_token, 隐式模式: implicit: 客户端模式: client_credentials")
    private String authorizedGrantTypes;
    @ApiModelProperty("客户端重定向uri")
    private String webServerRedirectUri;
    @ApiModelProperty("指定用户的权限范围，如果授权的过程需要用户登陆，该字段不生效，implicit和client_credentials需要")
    private String authorities;
    @ApiModelProperty("设置access_token的有效时间(秒),默认(606012,12小时)")
    private Integer accessTokenValidity;
    @ApiModelProperty("设置refresh_token有效期(秒)，默认(606024*30, 30填)")
    private Integer refreshTokenValidity;
    @ApiModelProperty("这是一个预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据")
    private String additionalInformation;
    @ApiModelProperty("默认false,适用于authorization_code模式,设置用户是否自动approval操作,设置true跳过用户确认授权操作页面，直接跳到redirect_uri")
    private boolean autoApprove;
}
