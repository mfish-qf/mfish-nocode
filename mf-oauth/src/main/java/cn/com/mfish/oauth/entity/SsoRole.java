package cn.com.mfish.oauth.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.1
 */
@Data
@TableName("sso_role")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sso_role对象", description = "角色信息表")
public class SsoRole extends BaseEntity<String> {
    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "角色ID")
    private String id;
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    @ApiModelProperty(value = "显示顺序")
    private Integer roleSort;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;
    @ApiModelProperty(value = "删除标志（0代表存在 1代表删除）")
    private Integer delFlag;
    @TableField(exist = false)
    @ApiModelProperty("菜单ID列表")
    private List<String> menus = new ArrayList<>();
}
