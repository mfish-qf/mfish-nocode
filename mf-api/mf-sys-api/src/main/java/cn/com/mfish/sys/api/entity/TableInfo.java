package cn.com.mfish.sys.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("表信息")
@Accessors(chain = true)
public class TableInfo implements Serializable {
    @ApiModelProperty("id类型")
    private String idType;
    @ApiModelProperty("表名称")
    private String tableName;
    @ApiModelProperty("表描述信息")
    private String tableComment;
    @ApiModelProperty("库")
    private String tableSchema;
    @ApiModelProperty("列信息")
    private List<FieldInfo> columns;
}
