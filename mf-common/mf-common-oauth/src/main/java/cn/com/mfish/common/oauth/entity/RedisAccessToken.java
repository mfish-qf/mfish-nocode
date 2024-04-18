package cn.com.mfish.common.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: mfish
 * @date: 2020/2/17 15:04
 */
@Schema(description = "存储的accessToken信息")
@EqualsAndHashCode(callSuper = true)
@Data
public class RedisAccessToken extends AuthorizationCode {
    @Schema(description = "获取token时传入sessionId")
    private String tokenSessionId;
    @Schema(description = "客户端密钥")
    private String clientSecret;
    @Schema(description = "token值")
    private String accessToken;
    @Schema(description = "refreshToken值")
    private String refreshToken;
    @Schema(description = "获取token的类型")
    private String grantType;
    @Schema(description = "token时效")
    private Long expire;
    @Schema(description = "refreshToken时效")
    private Long reTokenExpire;
    @Schema(description = "ip地址")
    private String ip;
}
