package cn.com.mfish.common.core.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ：qiufeng
 * @description：分页请求
 * @date ：2022/11/16 23:14
 */
@ApiModel("分页请求")
@Data
@Accessors(chain = true)
public class ReqPage implements Serializable {
    @ApiModelProperty("第几页")
    private Integer pageNum = 1;
    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;
}
