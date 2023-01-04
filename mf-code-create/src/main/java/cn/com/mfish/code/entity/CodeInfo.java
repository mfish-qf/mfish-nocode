package cn.com.mfish.code.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: mfish
 * @description: 代码相关属性
 * @date: 2022/8/25 16:39
 */
@Data
@ApiModel("代码相关属性")
@Accessors(chain = true)
public class CodeInfo implements Serializable {
    @ApiModelProperty("包名")
    private String packageName;
    @ApiModelProperty("实体名称(会自动转化为首字母大写驼峰)")
    private String entityName;
    @ApiModelProperty("表相关信息")
    private TableInfo tableInfo;
    @ApiModelProperty("接口前缀")
    private String apiPrefix;
}
