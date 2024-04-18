package cn.com.mfish.common.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: mfish
 * @date: 2020/2/13 12:56
 */
@Schema(description = "认证Code属性")
@Data
public class AuthorizationCode implements Serializable {
    @Schema(description = "认证code")
    private String code;
    @Schema(description = "客户端id")
    private String clientId;
    @Schema(description = "获取code时传入sessionId")
    private String codeSessionId;
    @Schema(description = "申请Scope权限")
    private String scope;
    @Schema(description = "回调状态")
    private String state;
    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "帐号")
    private String account;
    @Schema(description = "回调地址")
    private String redirectUri;
    @Schema(description = "父token，当code为其他账号帮忙获取时记录其token")
    private String parentToken;
    @Schema(description = "租户ID")
    private String tenantId;
}
