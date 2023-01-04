package cn.com.mfish.sys.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel("字典请求参数")
public class ReqDict {
    @ApiModelProperty(value = "字典名称")
    private String dictName;
    @ApiModelProperty(value = "字典编码")
    private String dictCode;
    @ApiModelProperty(value = "状态(0正常 1停用)")
    private Integer status;
}
