package cn.com.mfish.common.oauth.api.vo;

import cn.com.mfish.common.oauth.api.entity.SsoTenant;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 租户返回对象
 * @author: mfish
 * @date: 2023/6/13 19:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "租户对象")
public class TenantVo extends SsoTenant {
    @ExcelProperty("用户名")
    @Schema(description = "用户名")
    private String account;
    @ExcelProperty("昵称")
    @Schema(description = "昵称")
    private String nickname;
    @ExcelProperty("手机号")
    @Schema(description = "手机号")
    private String phone;
    @ExcelProperty("是否管理员")
    @Schema(description = "是否管理员 1是 0否")
    private Integer master;
}
