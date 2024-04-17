package cn.com.mfish.common.sys.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 字典项
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.2.1
 */
@Data
@Accessors(chain = true)
@ApiModel("字典项请求参数")
public class ReqDictItem {
    @ApiModelProperty(value = "字典ID")
    private String dictId;
    @ApiModelProperty(value = "字典编码")
    private String dictCode;
    @ApiModelProperty(value = "字典标签")
    private String dictLabel;
    @ApiModelProperty(value = "字典键值")
    private String dictValue;
    @ApiModelProperty(value = "状态(0正常 1停用)")
    private Integer status;
}
