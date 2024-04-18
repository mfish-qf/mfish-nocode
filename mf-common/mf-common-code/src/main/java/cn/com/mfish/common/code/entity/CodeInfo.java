package cn.com.mfish.common.code.entity;

import cn.com.mfish.sys.api.entity.TableInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author: mfish
 * @description: 代码相关属性
 * @date: 2022/8/25 16:39
 */
@Data
@Schema(description = "代码相关属性")
@Accessors(chain = true)
public class CodeInfo implements Serializable {
    @Schema(description = "包名")
    private String packageName;
    @Schema(description = "实体名称(会自动转化为首字母大写驼峰)")
    private String entityName;
    @Schema(description = "表相关信息")
    private TableInfo tableInfo;
    @Schema(description = "接口前缀")
    private String apiPrefix;
    @Schema(description = "查询列表")
    private List<SearchInfo> searchList;
}
