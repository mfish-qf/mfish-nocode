package cn.com.mfish.common.dblink.entity;

import cn.com.mfish.common.core.enums.DataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: 查询参数
 * @author: mfish
 * @date: 2023/12/26
 */
@Data
@ApiModel("查询参数")
@Accessors(chain = true)
public class QueryParam implements Serializable {
    @ApiModelProperty("参数值")
    private Object value;
    @ApiModelProperty("参数类型")
    private DataType type = DataType.STRING;
}
