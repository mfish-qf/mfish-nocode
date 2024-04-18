package cn.com.mfish.sys.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author: mfish
 * @description: 表信息
 * @date: 2022/8/29 16:46
 */
@Data
@Schema(description = "表信息")
@Accessors(chain = true)
public class TableInfo implements Serializable {
    @Schema(description = "id类型")
    private String idType;
    @Schema(description = "表名称")
    private String tableName;
    @Schema(description = "表描述信息")
    private String tableComment;
    @Schema(description = "库")
    private String tableSchema;
    @Schema(description = "表类型 0表 1视图")
    private Integer tableType;
    @Schema(description = "列信息")
    private List<FieldInfo> columns;
}
