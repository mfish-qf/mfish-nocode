package cn.com.mfish.oauth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：qiufeng
 * @description：修改密码入参
 * @date ：2022/12/21 22:17
 */
@ApiModel("修改密码入参")
@Data
public class ReqChangePwd {
    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("旧密码")
    private String oldPwd;
    @ApiModelProperty("新密码")
    private String newPwd;
}
