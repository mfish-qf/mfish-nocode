package cn.com.mfish.common.oauth.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V2.0.1
 */
@Data
@TableName("sso_tenant")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sso_tenant对象 租户信息表")
public class SsoTenant extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("租户类型 0 个人 1 企业")
    @Schema(description = "租户类型 0 个人 1 企业")
    private Integer tenantType;
    @ExcelProperty("租户名称")
    @Schema(description = "租户名称")
    private String name;
    @ExcelProperty("城市")
    @Schema(description = "城市")
    private String city;
    @ExcelProperty("省份")
    @Schema(description = "省份")
    private String province;
    @ExcelProperty("区县")
    @Schema(description = "区县")
    private String county;
    @ExcelProperty("地址")
    @Schema(description = "地址")
    private String address;
    @ExcelProperty("公司规模")
    @Schema(description = "公司规模")
    private String corpSize;
    @Schema(description = "营业年限")
    private String corpYears;
    @ExcelProperty("所属行业")
    @Schema(description = "所属行业")
    private String trade;
    @ExcelProperty("状态 0正常 1注销")
    @Schema(description = "状态 0正常 1注销")
    private Integer status;
    @ExcelProperty("logo")
    @Schema(description = "logo")
    private String logo;
    @ExcelProperty("域名")
    @Schema(description = "域名")
    private String domain;
    @ExcelProperty("删除状态(0-正常,1-已删除)")
    @Schema(description = "删除状态(0-正常,1-已删除)")
    private Integer delFlag;
    @ExcelProperty("用户ID，关联用户为管理员")
    @Schema(description = "用户ID，关联用户为管理员")
    private String userId;
    @ExcelIgnore
    @TableField(exist = false)
    @Schema(description = "角色Id")
    private List<String> roleIds;
}
