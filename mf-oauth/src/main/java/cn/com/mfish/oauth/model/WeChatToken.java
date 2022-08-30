package cn.com.mfish.oauth.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ：qiufeng
 * @description：微信登录redis中token信息
 * @date ：2021/12/14 9:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("缓存中的微信token")
public class WeChatToken  extends AccessToken {
    @ApiModelProperty("微信openid")
    private String openid;
    @ApiModelProperty("微信返回session参数")
    private String session_key;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("用户账号")
    private String account;
}
