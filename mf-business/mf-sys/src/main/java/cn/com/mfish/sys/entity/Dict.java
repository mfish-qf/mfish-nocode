package cn.com.mfish.sys.entity;

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
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.0.0
 */
@Data
@TableName("sys_dict")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sys_dict对象", description = "字典")
public class Dict extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("字典名称")
    @ApiModelProperty(value = "字典名称")
	private String dictName;
    @ExcelProperty("字典编码")
    @ApiModelProperty(value = "字典编码")
	private String dictCode;
    @ExcelProperty("状态(0正常 1停用)")
    @ApiModelProperty(value = "状态(0正常 1停用)")
	private Integer status;
    @ExcelProperty("备注")
    @ApiModelProperty(value = "备注")
	private String remark;
}
