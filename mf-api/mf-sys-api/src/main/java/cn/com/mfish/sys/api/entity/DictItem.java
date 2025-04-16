package cn.com.mfish.sys.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 字典项
 * @Author: mfish
 * @date: 2023-01-03
 * @version: V2.0.0
 */
@Data
@TableName("sys_dict_item")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sys_dict_item对象 字典项")
public class DictItem<T> extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @Schema(description = "字典ID")
    private String dictId;
    @Schema(description = "字典编码")
    private String dictCode;
    @Schema(description = "字典标签")
    private String dictLabel;
    @Schema(description = "字典键值")
    private T dictValue;
    @Schema(description = "值类型(0 字符 1数字 2布尔)")
    private Integer valueType;
    @Schema(description = "字典排序")
    private Integer dictSort;
    @Schema(description = "字典显示颜色")
    private String color;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "状态(0正常 1停用)")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
}
