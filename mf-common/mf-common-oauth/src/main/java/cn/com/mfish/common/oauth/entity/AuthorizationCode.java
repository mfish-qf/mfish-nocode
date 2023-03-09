package cn.com.mfish.common.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: mfish
 * @date: 2020/2/13 12:56
 */
@ApiModel("认证Code属性")
@Data
public class AuthorizationCode implements Serializable {
    @ApiModelProperty("认证code")
    private String code;
    @ApiModelProperty("客户端id")
    private String clientId;
    @ApiModelProperty("获取code时传入sessionId")
    private String codeSessionId;
    @ApiModelProperty("申请Scope权限")
    private String scope;
    @ApiModelProperty("回调状态")
    private String state;
    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("帐号")
    private String account;
    @ApiModelProperty("回调地址")
    private String redirectUri;
    @ApiModelProperty("父token，当code为其他账号帮忙获取时记录其token")
    private String parentToken;
}
