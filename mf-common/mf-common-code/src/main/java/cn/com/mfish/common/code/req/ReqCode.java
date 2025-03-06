package cn.com.mfish.common.code.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author: mfish
 * @description: 代码生成参数
 * @date: 2022/12/23 19:39
 */
@Data
@Schema(description = "代码生成参数")
public class ReqCode {
    @Schema(description = "数据库连接ID(必传 通过数据库列表接口查询)")
    private String connectId;
    @Schema(description = "表前缀(不传使用空字符串)")
    private String tableSchema;
    @Schema(description = "表名(必传)")
    private String tableName;
    @Schema(description = "表描述(不传会获取数据库表中的中文描述，如果也为空则使用表名)")
    private String tableComment;
    @Schema(description = "项目包名(不传使用默认包名 cn.com.mfish.sys)")
    private String packageName;
    @Schema(description = "实体类名(不传会使用表名驼峰化)")
    private String entityName;
    @Schema(description = "接口路径前缀 例如:/oauth2/user接口前缀为oauth2(不传会使用packageName，最底层包名 例如:cn.com.mfish.sys包会使用sys)")
    private String apiPrefix;
    @Schema(description = "查询条件列表 所有条件为and关系")
    private List<ReqSearch> searches;
}
