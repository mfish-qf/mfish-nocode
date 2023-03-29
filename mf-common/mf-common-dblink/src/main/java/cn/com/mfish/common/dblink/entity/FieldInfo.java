package cn.com.mfish.common.dblink.entity;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.dblink.enums.DataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: mfish
 * @description: 字段信息
 * @date: 2022/8/29 16:47
 */
@Data
@ApiModel("字段信息")
@Accessors(chain = true)
public class FieldInfo implements Serializable {
    @ApiModelProperty("字段名称")
    private String fieldName;
    @ApiModelProperty("是否主键 true是 false否")
    private Boolean isPrimary = false;
    @ApiModelProperty("JAVA字段类型")
    private String type;
    @ApiModelProperty("数据库字段类型")
    private String dbType;
    @ApiModelProperty("列类型 格式varchar(20)")
    private String columnType;
    @ApiModelProperty("是否允许为空 true允许 false不允许")
    private Boolean nullAble = true;
    @ApiModelProperty("字段描述")
    private String comment;

    public String getType() {
        return DataType.forType(this.dbType).getValue();
    }

    public String getComment() {
        if (StringUtils.isEmpty(this.comment)) {
            return "";
        }
        return this.comment;
    }
}
