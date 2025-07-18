package cn.com.mfish.sys.api.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V2.0.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "数据库连接请求参数")
public class ReqDbConnect {
    @Schema(description = "连接名")
    private String dbTitle;
    @Schema(description = "数据库类型（0 mysql 1 oracle 2 pgsql）")
    private Integer dbType;
    @Schema(description = "主机")
    private String host;
    @Schema(description = "数据库名")
    private String dbName;
}
