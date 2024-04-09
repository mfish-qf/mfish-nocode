package cn.com.mfish.common.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 简单用户信息
 * @author: mfish
 * @date: 2024/4/9
 */
@Data
@ApiModel("简单用户信息")
public class SimpleUserInfo {
    @ApiModelProperty("唯一id")
    private String id;
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("昵称--用于显示")
    private String nickname;
    @ApiModelProperty("头像")
    private String headImgUrl;
}
