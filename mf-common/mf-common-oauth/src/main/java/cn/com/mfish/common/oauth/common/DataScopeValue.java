package cn.com.mfish.common.oauth.common;

import cn.com.mfish.common.core.scope.DataScopeHandle;
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
    @Schema(name = "字段名称")
    private String fieldName;
    @Schema(name = "字段值")
    private String[] values;
}