package cn.com.mfish.common.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: mfish
 * @date: 2020/2/17 15:04
 */
@ApiModel("存储的accessToken信息")
@EqualsAndHashCode(callSuper = true)
@Data
public class RedisAccessToken extends AuthorizationCode {
    @ApiModelProperty("获取token时传入sessionId")
    private String tokenSessionId;
    @ApiModelProperty("客户端密钥")
    private String clientSecret;
    @ApiModelProperty("token值")
    private String accessToken;
    @ApiModelProperty("refreshToken值")
    private String refreshToken;
    @ApiModelProperty("获取token的类型")
    private String grantType;
    @ApiModelProperty("token时效")
    private Long expire;
    @ApiModelProperty("refreshToken时效")
    private Long reTokenExpire;
    @ApiModelProperty("ip地址")
    private String ip;
}
