package cn.com.mfish.oauth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.2.0
 */
@Data
@Accessors(chain = true)
@ApiModel("角色信息表请求参数")
public class ReqSsoRole {
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
    @ApiModelProperty(value = "组织ID")
    private String orgIds;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;
}
