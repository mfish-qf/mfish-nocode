package cn.com.mfish.common.dblink.datatable;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.dblink.common.Constant;
import cn.com.mfish.common.dblink.common.DataUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description: 元数据行
 * @author: mfish
 * @date: 2023/3/20
 */
public class MetaDataRow extends LinkedHashMap<String, Object> implements Comparable<MetaDataRow> {

    private MetaDataHeaders headers;

    public MetaDataHeaders getColHeaders() {
        return headers;
    }

    public MetaDataRow(MetaDataHeaders headers) {
        this.headers = headers;
        for (Map.Entry<String, MetaDataHeader> entry : headers.entrySet()) {
            this.put(entry.getKey(), null);
        }
    }

    /**
     * 添加列 如果列头中不存在则创建列头
     * 如果存在则直接赋值
     *
     * @param header 列头
     * @return
     */
    public MetaDataRow addColumn(MetaDataHeader header, Object value) {
        if (!headers.containsKey(DataUtils.getColName(header))) {
            headers.addColumn(header);
        }
        this.put(header.getColName(), value);
        return this;
    }

    /**
     * 添加行中多列值 如果列中存在则重命名
     *
     * @param cells 多列值
     * @return
     */
    public MetaDataRow addColumn(Map<MetaDataHeader, Object> cells) {
        cells.forEach(this::addColumn);
        return this;
    }

    /**
     * 获取单元值
     *
     * @param colName 列名
     * @return
     */
    public Object getCellValue(final String colName) {
        if (this.containsKey(colName)) {
            return this.get(colName);
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 根据索引获取单元值
     *
     * @param index 索引 起始从0开始
     * @return
     */
    public Object getCellValue(final int index) {
        Iterator<Map.Entry<String, Object>> iterator = this.entrySet().iterator();
        if (index >= this.size()) {
            throw new MyRuntimeException(Constant.IndexOverException);
        }
        int i = 0;
        while (iterator.hasNext()) {
            if (index == i++) {
                return iterator.next().getValue();
            }
            iterator.next();
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 通过索引获取列名称
     *
     * @param index 索引
     * @return
     */
    public String getColName(final int index) {
        Iterator<Map.Entry<String, MetaDataHeader>> iterator = headers.entrySet().iterator();
        if (index >= this.size()) {
            throw new MyRuntimeException(Constant.IndexOverException);
        }
        int i = 0;
        while (iterator.hasNext()) {
            if (index == i++) {
                return iterator.next().getValue().getColName();
            }
            iterator.next();
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 设置单元值
     *
     * @param colName 列名
     * @param value   值
     */
    public void setCellValue(final String colName, Object value) {
        if (this.containsKey(colName)) {
            this.put(colName, value);
            return;
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 通过索引设置值
     *
     * @param index 索引
     * @param value 值
     */
    public void setCellValue(final int index, Object value) {
        Iterator<Map.Entry<String, MetaDataHeader>> iterator = headers.entrySet().iterator();
        if (index >= this.size()) {
            throw new MyRuntimeException(Constant.IndexOverException);
        }
        int i = 0;
        while (iterator.hasNext()) {
            if (index == i++) {
                setCellValue(iterator.next().getValue().getColName(), value);
                return;
            }
            iterator.next();
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 删除列
     *
     * @param colName 列名称
     */
    public void removeColumn(String colName) {
        if (this.containsKey(colName)) {
            this.remove(colName);
            return;
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 根据索引删除列
     *
     * @param index 索引
     */
    public void removeColumn(int index) {
        String colName = getColName(index);
        if (this.containsKey(colName)) {
            this.remove(colName);
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 移除单元值
     *
     * @param colName 列名称
     */
    public void removeCell(String colName) {
        setCellValue(colName, null);
    }


    /**
     * 移除单元值
     *
     * @param index 索引
     */
    public void removeCell(final int index) {
        setCellValue(index, null);
    }

    /**
     * 根据列索引获取列头
     *
     * @param index 索引
     * @return
     */
    public MetaDataHeader getColHeader(int index) {
        if (headers == null || headers.size() == 0) {
            return null;
        }
        return headers.getColHeader(index);
    }

    /**
     * 根据列名称获取列头
     *
     * @param colName 列名
     * @return
     */
    public MetaDataHeader getColHeader(String colName) {
        return headers.getColHeader(colName);
    }

    /**
     * 行比较 从第一列开始循环 谁先碰到大的数为大
     *
     * @param row 比较的行
     * @return
     */
    @Override
    public int compareTo(@NotNull MetaDataRow row) {
        int value = 0;
        for (Map.Entry<String, MetaDataHeader> header : headers.entrySet()) {
            switch (header.getValue().getDataType()) {
                case NUMERIC:
                    value = DataUtils.numCompare(this.getCellValue(header.getValue().getColName()), row.getCellValue(header.getValue().getColName()));
                    break;
                case DATE:
                    value = DataUtils.dateCompare(this.getCellValue(header.getValue().getColName()), row.getCellValue(header.getValue().getColName()));
                    break;
                case VARCHAR:
                    value = DataUtils.strCompare(this.getCellValue(header.getValue().getColName()), row.getCellValue(header.getValue().getColName()));
                    break;
            }
            if (value != 0) {
                return value;
            }
        }
        return 0;
    }
}
