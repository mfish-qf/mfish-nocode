package cn.com.mfish.common.oauth.api.vo;

import cn.com.mfish.common.oauth.api.entity.SsoTenant;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 租户返回对象
 * @author: mfish
 * @date: 2023/6/13 19:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租户对象")
public class TenantVo extends SsoTenant {
    @ExcelProperty("用户名")
    @ApiModelProperty("用户名")
    private String account;
    @ExcelProperty("昵称")
    @ApiModelProperty("昵称")
    private String nickname;
    @ExcelProperty("手机号")
    @ApiModelProperty("手机号")
    private String phone;
    @ExcelProperty("是否管理员")
    @ApiModelProperty("是否管理员 1是 0否")
    private Integer master;
}
