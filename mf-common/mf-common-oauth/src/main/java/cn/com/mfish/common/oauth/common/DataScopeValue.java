package cn.com.mfish.common.oauth.common;

import cn.com.mfish.common.oauth.scope.DataScopeHandle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 数据范围值
 * @author: mfish
 * @date: 2024/4/29
 */
@Data
@Accessors(chain = true)
public class DataScopeValue {
    @Schema(name = "数据处理方式")
    private DataScopeHandle dataScopeHandle;
    /**
     * 字段名，用于指定查询条件的目标字段
     */
    @Schema(name = "字段名称")
    private String fieldName;
    /**
     * 值数组，包含一个或多个字段值，用于构建查询条件
     */
    @Schema(name = "字段值")
    private String[] values;
    /**
     * 排除条件数组，包含一个或多个排除条件，用于构建查询条件
     * 排除条件的格式应为一个字符串，例如"id <> 1"，用于排除特定的值
     * 如果排除值为变量采用#{XXX}格式，例如id <> #{XXX}
     */
    @Schema(name = "排除条件")
    private String[] excludes;
}