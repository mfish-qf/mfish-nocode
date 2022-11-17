package cn.com.mfish.common.web.common;

import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：qiufeng
 * @description：分页结果
 * @date ：2022/11/17 9:35
 */
@Data
@Accessors(chain = true)
public class PageResult<T> implements Serializable {
    private int pageNum;
    private int pageSize;
    private List<T> list;
    private long total;

    public PageResult(List<T> list) {
        PageInfo pageInfo = new PageInfo<>(list);
        this.setList(pageInfo.getList())
                .setPageNum(pageInfo.getPageNum())
                .setPageSize(pageInfo.getPageSize())
                .setTotal(pageInfo.getTotal());
    }
}
