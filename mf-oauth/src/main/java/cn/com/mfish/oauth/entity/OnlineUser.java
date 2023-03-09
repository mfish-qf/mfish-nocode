package cn.com.mfish.oauth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 在线用户
 * @author: mfish
 * @date: 2023/3/8 18:20
 */
@ApiModel("在线用户")
@Data
@Accessors(chain = true)
public class OnlineUser {
    @ApiModelProperty("帐号")
    private String account;
    @ApiModelProperty("客户端ID")
    private String clientId;
    @ApiModelProperty("token信息")
    private String token;
    @ApiModelProperty("登录IP")
    private String ip;
    @ApiModelProperty("登录模式 0 浏览器 1微信")
    private int loginMode;
    @ApiModelProperty("登录时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    @ApiModelProperty("过期时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expire;
}
