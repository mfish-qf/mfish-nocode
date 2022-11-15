package cn.com.mfish.oauth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 用户信息
 * @Author: mfish
 * @Date: 2022-11-13
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@ApiModel("用户信息请求参数")
public class ReqSsoUser {
    @ApiModelProperty("组织ID")
    private String orgId;
}
