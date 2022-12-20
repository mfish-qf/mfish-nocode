package cn.com.mfish.common.oauth.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ：qiufeng
 * @description：用户角色
 * @date ：2022/12/1 20:55
 */
@Data
@ApiModel("用户角色属性")
@Accessors(chain = true)
public class UserRole {
    @ApiModelProperty(value = "角色ID")
    private String id;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
}
