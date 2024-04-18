package cn.com.mfish.common.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: mfish
 * @description: 微信登录redis中token信息
 * @date: 2021/12/14 9:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "缓存中的微信token")
public class WeChatToken extends AccessToken {
    @Schema(description = "微信openid")
    private String openid;
    @Schema(description = "微信返回session参数")
    private String session_key;
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "用户账号")
    private String account;
    @Schema(description = "refreshToken时效")
    private Long reTokenExpire;
    @Schema(description = "ip地址")
    private String ip;
    @Schema(description = "租户ID")
    private String tenantId;
}
