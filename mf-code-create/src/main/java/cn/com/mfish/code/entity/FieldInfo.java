package cn.com.mfish.code.entity;

import cn.com.mfish.common.dblink.enums.DataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Locale;

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
    @ApiModelProperty("字段类型")
    private String type = "String";
    @ApiModelProperty("数据库字段类型")
    private String dbType = "VARCHAR";
    @ApiModelProperty("是否允许为空 true允许 false不允许")
    private Boolean nullAble = true;
    @ApiModelProperty("字段描述")
    private String comment = "";

    public FieldInfo setDbType(String dbType) {
        this.dbType = dbType.toUpperCase(Locale.ROOT);
        this.type = DataType.forType(this.dbType).getValue();
        return this;
    }
}
