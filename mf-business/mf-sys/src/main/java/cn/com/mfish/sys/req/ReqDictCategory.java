package cn.com.mfish.sys.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 树形分类
 * @author: mfish
 * @date: 2024-03-12
 * @version: V1.2.0
 */
@Data
@Accessors(chain = true)
@ApiModel("树形分类请求参数")
public class ReqDictCategory {
    @ApiModelProperty(value = "分类名称")
    private String categoryName;
    @ApiModelProperty(value = "分类编码")
    private String categoryCode;
}
