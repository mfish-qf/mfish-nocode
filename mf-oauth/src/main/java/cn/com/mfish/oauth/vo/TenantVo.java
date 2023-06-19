package cn.com.mfish.oauth.vo;

import cn.com.mfish.oauth.entity.SsoTenant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 租户返回对象
 * @author: mfish
 * @date: 2023/6/13 19:54
 */
@Data
@ApiModel("租户对象")
public class TenantVo extends SsoTenant {
    @ApiModelProperty("用户名")
    private String account;
    @ApiModelProperty("是否管理员 1是 0否")
    private Integer master;
}
