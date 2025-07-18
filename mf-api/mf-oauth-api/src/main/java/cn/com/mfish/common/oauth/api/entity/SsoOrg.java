package cn.com.mfish.common.oauth.api.entity;

import cn.com.mfish.common.core.entity.BaseTreeEntity;
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
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @version: V2.0.1
 */
@Data
@TableName("sso_org")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "sso_org对象 组织结构表")
public class SsoOrg extends BaseTreeEntity<String> {
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "组织ID")
    private String id;
    @Schema(description = "租户id")
    private String tenantId;
    @Schema(description = "组织固定编码(可为空，不允许重复，用来通过此code识别具体是哪个组织)")
    private String orgFixCode;
    @Schema(description = "组织编码(自动编码父子关系，不需要传值)")
    private String orgCode;
    @Schema(description = "组织级别")
    private Integer orgLevel;
    @Schema(description = "组织名称")
    private String orgName;
    @Schema(description = "排序")
    private Integer orgSort;
    @Schema(description = "负责人")
    private String leader;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
    @Schema(description = "删除标志（0正常 1删除）")
    private Integer delFlag;
    @TableField(exist = false)
    @Schema(description = "角色ID列表")
    private List<String> roleIds;
}
