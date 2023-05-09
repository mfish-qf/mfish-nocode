package cn.com.mfish.common.code.api.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 搜索请求
 * @author: mfish
 * @date: 2023/5/9 19:31
 */
@ApiModel("查询条件")
@Data
public class ReqSearch {
    @ApiModelProperty("字段")
    private String field;
    @ApiModelProperty("查询值")
    private String value;
    @ApiModelProperty("查询条件")
    private String condition;
}
