package cn.com.mfish.sys.entity;

import cn.com.mfish.common.core.entity.BaseTreeEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 树行分类字典
 * @author: mfish
 * @date: 2024-03-12
 * @version: V1.3.0
 */
@Data
@TableName("sys_dict_category")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sys_dict_category对象 树行分类字典")
public class DictCategory extends BaseTreeEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("父分类id")
    @Schema(description = "父分类id")
    private String parentId;
    @ExcelProperty("分类编码")
    @Schema(description = "分类编码")
    private String categoryCode;
    @ExcelProperty("分类名称")
    @Schema(description = "分类名称")
    private String categoryName;
    @ExcelProperty("分类树编码（系统自动编码）")
    @Schema(description = "分类树编码（系统自动编码）")
    private String treeCode;
    @ExcelProperty("分类树层级（自动生成）")
    @Schema(description = "分类树层级（自动生成）")
    private Short treeLevel;
    @ExcelProperty("排序")
    @Schema(description = "排序")
    private Integer sort;
}
