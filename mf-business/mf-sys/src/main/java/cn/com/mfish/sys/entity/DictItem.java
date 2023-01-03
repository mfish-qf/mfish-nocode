package cn.com.mfish.sys.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 字典项
 * @Author: mfish
 * @Date: 2023-01-03
 * @Version: V1.0.0
 */
@Data
@TableName("sys_dict_item")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sys_dict_item对象", description = "字典项")
public class DictItem extends BaseEntity<String> {
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty(value = "字典编码")
	private String dictCode;
    @ApiModelProperty(value = "字典标签")
	private String dictLabel;
    @ApiModelProperty(value = "字典键值")
	private String dictValue;
    @ApiModelProperty(value = "字典排序")
	private Integer dictSort;
    @ApiModelProperty(value = "状态(0正常 1停用)")
	private Integer status;
    @ApiModelProperty(value = "备注")
	private String remark;
}
