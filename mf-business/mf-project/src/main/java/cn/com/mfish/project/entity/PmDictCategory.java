package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 树形业务字典与功能层级分类表
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@TableName("pm_dict_category")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_dict_category对象 树形业务字典与功能层级分类")
public class PmDictCategory extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_ID)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "父分类id")
    private String parentId;
    @Schema(description = "分类编码")
    private String categoryCode;
    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "分类树编码(系统自动编码,如 001、001001)")
    private String treeCode;
    @Schema(description = "分类树层级(自动生成,1为一级分类,2为二级分类)")
    private Integer treeLevel;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "删除标志(0正常 1已删除)")
    @TableLogic
    private Integer delFlag;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "子节点")
    @TableField(exist = false)
    private List<PmDictCategory> children;
}
