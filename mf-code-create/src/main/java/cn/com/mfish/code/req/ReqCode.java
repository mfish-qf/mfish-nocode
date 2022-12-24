package cn.com.mfish.code.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：qiufeng
 * @description：代码生成参数
 * @date ：2022/12/23 19:39
 */
@Data
@ApiModel("代码生成参数")
public class ReqCode {
    @ApiModelProperty("库名")
    private String schema;
    @ApiModelProperty("表名")
    private String tableName;
    @ApiModelProperty("表描述")
    private String tableComment;
    @ApiModelProperty("项目包名(不传使用默认包名)")
    private String packageName;
    @ApiModelProperty("实体类名(不传会使用表名驼峰化)")
    private String entityName;
}
