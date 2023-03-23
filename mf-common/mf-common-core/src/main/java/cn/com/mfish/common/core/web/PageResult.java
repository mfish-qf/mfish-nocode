package cn.com.mfish.common.core.web;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author: mfish
 * @description: 分页结果
 * @date: 2022/11/17 9:35
 */
@Data
@Accessors(chain = true)
@ApiModel("分页查询结果")
public class PageResult<T> implements Serializable {

    @ApiModelProperty("第几页")
    private int pageNum;
    @ApiModelProperty("每页条数")
    private int pageSize;
    @ApiModelProperty("总页数")
    private int pages;
    @ApiModelProperty("总条数")
    private long total;
    @ApiModelProperty("数据结果")
    private List<T> list;

    public PageResult(){

    }

    /**
     * 通过pageHelper中的pageInfo进行包装
     *
     * @param list
     */
    public PageResult(List<T> list) {
        PageInfo pageInfo = new PageInfo<>(list);
        this.setList(pageInfo.getList())
                .setPageNum(pageInfo.getPageNum())
                .setPageSize(pageInfo.getPageSize())
                .setPages(pageInfo.getPages())
                .setTotal(pageInfo.getTotal());
    }

    /**
     * 外部计算直接包装分页
     *
     * @param list
     * @param pageNum
     * @param pageSize
     * @param total
     */
    public PageResult(List<T> list, int pageNum, int pageSize, long total) {
        int pages = (int) (total / pageSize);
        pages = total % pageSize != 0 ? pages + 1 : pages;
        this.setList(list)
                .setPageNum(pageNum)
                .setPageSize(pageSize)
                .setPages(pages)
                .setTotal(total);
    }
}
