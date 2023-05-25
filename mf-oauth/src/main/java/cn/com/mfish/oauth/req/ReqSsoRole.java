package cn.com.mfish.oauth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.1
 */
@Data
@Accessors(chain = true)
@ApiModel("角色信息表请求参数")
public class ReqSsoRole {
    @ApiModelProperty(value = "客户端ID")
    private String clientId;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;
}
