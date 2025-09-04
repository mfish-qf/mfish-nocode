package cn.com.mfish.sys.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V2.1.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "代码构建请求参数")
public class ReqCodeBuild {
    private String tableName;
    @Schema(description = "接口路径前缀 例如:/oauth2/user接口前缀为oauth2(不传会使用packageName，最底层包名 例如:cn.com.mfish.sys包会使用sys)")
    private String apiPrefix;
    @Schema(description = "实体类名(不传会使用表名驼峰化)")
    private String entityName;
}
