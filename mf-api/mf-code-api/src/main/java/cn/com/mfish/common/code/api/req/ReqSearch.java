package cn.com.mfish.common.code.api.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 搜索请求
 * @author: mfish
 * @date: 2023/5/9 19:31
 */
@ApiModel("查询条件")
@Data
@Accessors(chain = true)
public class ReqSearch {
    @ApiModelProperty("字段")
    private String field;
    @ApiModelProperty("查询条件")
    private String condition;
}
