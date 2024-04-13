package cn.com.mfish.common.core.utils.excel;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description: 上传存储操作
 * @author: mfish
 * @date: 2023/12/18
 */
@Repository
public interface UploadDAO<T> {

    /**
     * 保存操作
     * 如果是mybatis,尽量别直接调用多次insert,自己写一个mapper里面新增一个方法batchInsert,所有数据一次性插入
     *
     * @param list 数据列表
     */
    void save(List<T> list);
}
