package cn.com.mfish.common.dblink.datatable;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.dblink.common.Constant;
import cn.com.mfish.common.dblink.common.DataUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public void addColumn(MetaDataHeaders columns) {
        for (Map.Entry<String, MetaDataHeader> entry : columns.entrySet()) {
            addColumn(entry.getValue());
        }
    }

    /**
     * 增加列
     *
     * @param header 列头
     */
    public void addColumn(MetaDataHeader header) {
        String colName = DataUtils.calcColName(0, DataUtils.chooseColName(header), this.keySet());
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
        MetaDataHeader[] headers = map.values().toArray(new MetaDataHeader[map.size()]);
        if (headers.length != this.size()) {
            return false;
        }
        for (int i = 0; i < headers.length; i++) {
            if (!headers[i].equals(this.values().toArray()[i])) {
                return false;
            }
        }
        return true;
    }
}
