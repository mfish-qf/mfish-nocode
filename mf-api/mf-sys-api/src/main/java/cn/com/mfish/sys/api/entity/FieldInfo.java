package cn.com.mfish.sys.api.entity;

import cn.com.mfish.common.core.enums.DataType;
import cn.com.mfish.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: mfish
 * @description: 字段信息
 * @date: 2022/8/29 16:47
 */
@Data
@Schema(description = "字段信息")
@Accessors(chain = true)
public class FieldInfo implements Serializable {
    @Schema(description = "字段名称")
    private String fieldName;
    @Schema(description = "数据库字段名称")
    private String tableFieldName;
    @Schema(description = "是否主键 true是 false否")
    private Boolean isPrimary = false;
    @Schema(description = "JAVA字段类型")
    private String type;
    @Schema(description = "数据库字段类型")
    private String dbType;
    @Schema(description = "列类型 格式varchar(20)")
    private String columnType;
    @Schema(description = "是否允许为空 true允许 false不允许")
    private Boolean nullAble = true;
    @Schema(description = "字段描述")
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
