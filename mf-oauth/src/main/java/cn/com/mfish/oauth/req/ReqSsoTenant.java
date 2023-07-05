package cn.com.mfish.oauth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.1.0
 */
@Data
@Accessors(chain = true)
@ApiModel("租户信息表请求参数")
public class ReqSsoTenant {
    @ApiModelProperty(value = "租户类型 0 个人 1 企业")
    private Integer tenantType;
    @ApiModelProperty(value = "域名")
    private String domain;
    @ApiModelProperty(value = "租户名称")
    private String name;
}
