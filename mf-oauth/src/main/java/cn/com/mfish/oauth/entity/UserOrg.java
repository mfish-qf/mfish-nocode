package cn.com.mfish.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 用户组织
 * @author: mfish
 * @date: 2023/7/4
 */
@Data
@ApiModel("用户组织")
public class UserOrg {
    @ApiModelProperty("组织ID")
    private String orgId;
    @ApiModelProperty("用户ID")
    private String userId;
}
