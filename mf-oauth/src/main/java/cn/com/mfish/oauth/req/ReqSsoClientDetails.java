package cn.com.mfish.oauth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 客户端信息
 * @author: mfish
 * @date: 2023-05-12
 * @version: V1.2.1
 */
@Data
@Accessors(chain = true)
@ApiModel("客户端信息请求参数")
public class ReqSsoClientDetails {
    @ApiModelProperty(value = "客户端名称")
    private String clientName;
    @ApiModelProperty(value = "客户端ID")
    private String clientId;
}
