package cn.com.mfish.common.dblink.entity;

import cn.com.mfish.common.core.enums.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: 查询参数
 * @author: mfish
 * @date: 2023/12/26
 */
@Data
@Schema(description = "查询参数")
@Accessors(chain = true)
public class QueryParam implements Serializable {
    @Schema(description = "参数值")
    private Object value;
    @Schema(description = "参数类型")
    private DataType type = DataType.STRING;
}
