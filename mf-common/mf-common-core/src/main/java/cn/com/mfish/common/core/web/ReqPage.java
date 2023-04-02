package cn.com.mfish.common.core.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author: mfish
 * @description: 分页请求
 * @date: 2022/11/16 23:14
 */
@ApiModel("分页请求")
public class ReqPage implements Serializable {
    @ApiModelProperty("第几页(默认值:1)")
    private Integer pageNum = 1;
    @ApiModelProperty("每页条数(默认值:10)")
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return pageNum;
    }

    public ReqPage setPageNum(Integer pageNum) {
        if (pageNum == null) {
            return this;
        }
        this.pageNum = pageNum;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public ReqPage setPageSize(Integer pageSize) {
        if (pageNum == null) {
            return this;
        }
        this.pageSize = pageSize;
        return this;
    }
}
