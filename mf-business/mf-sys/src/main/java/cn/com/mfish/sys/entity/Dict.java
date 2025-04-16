package cn.com.mfish.sys.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @version: V2.0.0
 */
@Data
@TableName("sys_dict")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sys_dict对象 字典")
public class Dict extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("字典名称")
    @Schema(description = "字典名称")
    private String dictName;
    @ExcelProperty("字典编码")
    @Schema(description = "字典编码")
    private String dictCode;
    @ExcelProperty("状态(0正常 1停用)")
    @Schema(description = "状态(0正常 1停用)")
    private Integer status;
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;
}
