package cn.com.mfish.sys.api.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V1.0.1
 */
@Data
@Accessors(chain = true)
@ApiModel("数据库连接请求参数")
public class ReqDbConnect {
    @ApiModelProperty(value = "连接名")
    private String dbTitle;
    @ApiModelProperty(value = "数据库类型（0 mysql 1 oracle 2 pgsql）")
    private Integer dbType;
    @ApiModelProperty(value = "主机")
    private String host;
    @ApiModelProperty(value = "数据库名")
    private String dbName;
}
