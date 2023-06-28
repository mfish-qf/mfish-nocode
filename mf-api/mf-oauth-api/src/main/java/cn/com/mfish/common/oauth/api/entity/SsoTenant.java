package cn.com.mfish.common.oauth.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.0.1
 */
@Data
@TableName("sso_tenant")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sso_tenant对象", description = "租户信息表")
public class SsoTenant extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("租户类型 0 个人 1 企业")
    @ApiModelProperty(value = "租户类型 0 个人 1 企业")
    private Integer tenantType;
    @ExcelProperty("租户名称")
    @ApiModelProperty(value = "租户名称")
	private String name;
    @ExcelProperty("城市")
    @ApiModelProperty(value = "城市")
	private String city;
    @ExcelProperty("省份")
    @ApiModelProperty(value = "省份")
	private String province;
    @ExcelProperty("区县")
    @ApiModelProperty(value = "区县")
	private String county;
    @ExcelProperty("地址")
    @ApiModelProperty(value = "地址")
	private String address;
    @ExcelProperty("公司规模")
    @ApiModelProperty(value = "公司规模")
	private String corpSize;
    @ApiModelProperty(value = "营业年限")
    private String corpYears;
    @ExcelProperty("所属行业")
    @ApiModelProperty(value = "所属行业")
	private String trade;
    @ExcelProperty("状态 0正常 1注销")
    @ApiModelProperty(value = "状态 0正常 1注销")
	private Integer status;
    @ExcelProperty("logo")
    @ApiModelProperty(value = "logo")
	private String logo;
    @ExcelProperty("域名")
    @ApiModelProperty(value = "域名")
	private String domain;
    @ExcelProperty("删除状态(0-正常,1-已删除)")
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	private Integer delFlag;
    @ExcelProperty("用户ID，关联用户为管理员")
    @ApiModelProperty(value = "用户ID，关联用户为管理员")
	private String userId;
}
