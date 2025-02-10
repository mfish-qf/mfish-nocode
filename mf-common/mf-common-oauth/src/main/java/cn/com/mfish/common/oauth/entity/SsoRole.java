package cn.com.mfish.common.oauth.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.3.2
 */
@Data
@TableName("sso_role")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sso_role对象 角色信息表")
public class SsoRole extends BaseEntity<String> {
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "角色ID")
    private String id;
    @Schema(description = "租户ID")
    private String tenantId;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色编码")
    private String roleCode;
    @Schema(description = "显示顺序")
    private Integer roleSort;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
    @Schema(description = "删除标志（0代表存在 1代表删除）")
    private Integer delFlag;
    @TableField(exist = false)
    @Schema(description = "菜单ID列表")
    private List<String> menus = new ArrayList<>();
}
