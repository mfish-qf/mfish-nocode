package cn.com.mfish.common.core.web;

import com.github.pagehelper.PageInfo;
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
public class PageResult<T> implements Serializable {
    private int pageNum;
    private int pageSize;
    private List<T> list;
    private long total;

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
        this.setList(list)
                .setPageNum(pageNum)
                .setPageSize(pageSize)
                .setTotal(total);
    }
}
