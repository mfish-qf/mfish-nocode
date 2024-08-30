package cn.com.mfish.common.dataset.datatable;

import cn.com.mfish.common.core.enums.DataType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.DataUtils;
import cn.com.mfish.common.dataset.common.Constant;
import cn.com.mfish.common.dataset.common.DataSetUtils;
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

    public void setColHeaders(MetaDataHeaders headers) {
        this.headers = new MetaDataHeaders();
        this.headers.addColumn(headers);
    }

    public MetaDataRow(MetaDataHeaders headers) {
        this.headers = new MetaDataHeaders();
        this.headers.addColumn(headers);
        for (Map.Entry<String, MetaDataHeader> entry : headers.entrySet()) {
            this.put(entry.getKey(), null);
        }
    }

    /**
     * 添加列 如果列头中不存在则创建列头
     * 如果存在则直接赋值
     *
     * @param header 列头
     * @param value 列的值
     * @return 当前的MetaDataRow对象
     */
    public MetaDataRow addColumn(MetaDataHeader header, Object value) {
        if (!headers.containsKey(DataSetUtils.chooseColName(header))) {
            headers.addColumn(header);
        }
        this.put(header.getColName(), value);
        return this;
    }

    /**
     * 添加行中多列值 如果列中存在则重命名
     *
     * @param cells 多列值
     * @return 当前的MetaDataRow对象
     */
    public MetaDataRow addColumn(Map<MetaDataHeader, Object> cells) {
        cells.forEach(this::addColumn);
        return this;
    }

    /**
     * 判断是否包列
     *
     * @param colName 列名
     * @return 是否包含
     */
    public boolean containsColumn(final String colName) {
        return this.containsKey(colName);
    }

    /**
     * 获取单元值
     * <p>
     * 本方法旨在通过列名获取特定单元的值。首先，它会检查指定的列名是否存在于当前数据结构中。
     * 如果存在，则直接返回该列名对应的值。如果不存在，则抛出一个自定义的运行时异常，
     * 表明请求的列名未找到。这种设计确保了方法在面对无效输入时能够给出明确的反馈，
     * 而不是默默地失败或返回错误的结果。
     *
     * @param colName 列名，即单元的标识名称，用于定位特定的值。
     * @return 如果列名存在，返回该列名对应的值；否则，抛出异常。
     * @throws MyRuntimeException 如果列名不存在，抛出表示未找到的异常。
     */
    public Object getCellValue(final String colName) {
        if (containsColumn(colName)) {
            return this.get(colName);
        }
        throw new MyRuntimeException(Constant.NotFoundException);
    }

    /**
     * 根据索引获取单元值
     *
     * @param index 索引 起始从0开始
     * @return 索引对应的值
     * @throws MyRuntimeException 如果索引超出范围或未找到对应值
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
     * @return 对应索引的列名称
     * @throws MyRuntimeException 如果索引超出范围或未找到对应列名称
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
        this.headers.remove(colName);
        this.remove(colName);
    }

    /**
     * 根据索引删除列
     *
     * @param index 索引
     */
    public void removeColumn(int index) {
        String colName = getColName(index);
        this.removeColumn(colName);
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
     * <p>
     * 此方法用于通过列索引（index）获取相应的列头信息如果当前没有列头信息或者索引超出了列头的范围，
     * 则返回null否则，返回与索引对应的列头信息
     *
     * @param index 索引，用于指定需要获取的列头在列表中的位置
     * @return 如果索引有效且有对应的列头信息，则返回MetaDataHeader对象；否则返回null
     */
    public MetaDataHeader getColHeader(int index) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        return headers.getColHeader(index);
    }

    /**
     * 根据列名称获取列头
     *
     * @param colName 列名
     * @return 对应列名的列头对象，如果找不到则返回null
     */
    public MetaDataHeader getColHeader(String colName) {
        return headers.getColHeader(colName);
    }

    /**
     * 获取数据类型
     *
     * @param colName 列名
     * @return 返回指定列名的数据类型
     */
    public DataType.SlimType getHeaderDataType(String colName) {
        return getColHeader(colName).getDataType();
    }

    /**
     * 行比较 从第一列开始循环 谁先碰到大的数为大
     * <p>
     * 此方法实现了两行数据的比较逻辑，用于确定两行数据之间的顺序关系它根据列的数值进行比较，
     * 数值大的行视为较大行比较时首先根据数字类型的数据进行比较，然后是日期类型，最后是字符串类型，
     * 这种比较方式符合常规的数据排序规则
     *
     * @param row 比较的行
     * @return 如果当前行大于参数row，返回正整数；如果当前行小于参数row，返回负整数；如果两者相等，返回0
     */
    @Override
    public int compareTo(@NotNull MetaDataRow row) {
        int value = 0;
        for (Map.Entry<String, MetaDataHeader> header : headers.entrySet()) {
            value = switch (header.getValue().getDataType()) {
                case NUMBER ->
                        DataUtils.numCompare(this.getCellValue(header.getValue().getColName()), row.getCellValue(header.getValue().getColName()));
                case DATE ->
                        DataUtils.dateCompare(this.getCellValue(header.getValue().getColName()), row.getCellValue(header.getValue().getColName()));
                case STRING ->
                        DataUtils.strCompare(this.getCellValue(header.getValue().getColName()), row.getCellValue(header.getValue().getColName()));
                default -> value;
            };
            if (value != 0) {
                return value;
            }
        }
        return 0;
    }
}
