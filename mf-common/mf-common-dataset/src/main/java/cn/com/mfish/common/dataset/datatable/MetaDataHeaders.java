package cn.com.mfish.common.dataset.datatable;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.dataset.common.Constant;
import cn.com.mfish.common.dataset.common.DataSetUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @description: 元数据列头列表
 * @author: mfish
 * @date: 2023/3/20
 */
public class MetaDataHeaders extends LinkedHashMap<String, MetaDataHeader> {
    /**
     * 增加列
     *
     * @param columns 列头组
     */
    public void addColumn(List<MetaDataHeader> columns) {
        for (MetaDataHeader col : columns) {
            addColumn(col);
        }
    }

    public void addColumn(MetaDataHeaders columns) {
        for (Map.Entry<String, MetaDataHeader> entry : columns.entrySet()) {
            addColumn(entry.getValue());
        }
    }

    /**
     * 增加列
     * <p>
     * 本方法用于向数据集元数据中添加一个新地列定义。它首先计算出一个唯一的列名，
     * 确保该列名未被使用。如果计算出的列名为空，则抛出异常，表示列名生成失败。
     * 如果计算出的列名与原始列名不一致，说明原始列名已存在，因此更新原始列名
     * 为新的唯一列名。最后，将列名与列的元数据添加到数据集中。
     *
     * @param header 列的元数据信息，包含列的初始名称和其他列属性
     */
    public void addColumn(MetaDataHeader header) {
        String colName = DataSetUtils.calcColName(0, DataSetUtils.chooseColName(header), this.keySet());
        if (StringUtils.isEmpty(colName)) {
            throw new MyRuntimeException(Constant.EmptyColNameException);
        }
        if (!colName.equals(header.getColName())) {
            header.setColName(colName);
        }
        header.setIndex(this.size());
        this.put(colName, header);
    }

    /**
     * 通过列名获取列头
     *
     * @param colName 列名
     * @return 返回对应的列头信息，如果不存在则抛出异常
     * @throws MyRuntimeException 如果给定的列名不存在于当前映射中，则抛出此异常
     */
    public MetaDataHeader getColHeader(String colName) {
        if (this.containsKey(colName)) {
            return this.get(colName);
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 根据索引获取列头
     *
     * @param index 列索引
     * @return 返回指定索引的列头信息
     * @throws MyRuntimeException 如果索引超出范围或未找到对应的列头，则抛出自定义运行时异常
     */
    public MetaDataHeader getColHeader(int index) {
        Iterator<Map.Entry<String, MetaDataHeader>> iterator = this.entrySet().iterator();
        int i = 0;
        if (index >= this.size()) {
            throw new MyRuntimeException(Constant.IndexOverException);
        }
        while (iterator.hasNext()) {
            if (index == i++) {
                return iterator.next().getValue();
            }
            iterator.next();
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkedHashMap<String, MetaDataHeader> map = (LinkedHashMap<String, MetaDataHeader>) o;
        String[] headers = map.keySet().toArray(new String[0]);
        String[] objs = this.keySet().toArray(new String[0]);
        return Arrays.equals(headers, objs);
    }
}
