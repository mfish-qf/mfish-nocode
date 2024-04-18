package cn.com.mfish.common.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description: 简单用户信息
 * @author: mfish
 * @date: 2024/4/9
 */
@Data
@Schema(description = "简单用户信息")
public class SimpleUserInfo {
    @Schema(description = "唯一id")
    private String id;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "昵称--用于显示")
    private String nickname;
    @Schema(description = "头像")
    private String headImgUrl;
}
