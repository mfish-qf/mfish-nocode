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
     *
     * @param header
     * @return 返回列名
     */
    public void addColumn(MetaDataHeader header) {
        String colName = DataSetUtils.calcColName(0, DataSetUtils.chooseColName(header), this.keySet());
        if (StringUtils.isEmpty(colName)) {
            throw new MyRuntimeException(Constant.EmptyColNameException);
        }
        if (!colName.equals(header.getColName())) {
            header.setColName(colName);
        }
        this.put(colName, header);
    }

    /**
     * 通过列名获取列头
     *
     * @param colName 列名
     * @return
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
     * @return
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
