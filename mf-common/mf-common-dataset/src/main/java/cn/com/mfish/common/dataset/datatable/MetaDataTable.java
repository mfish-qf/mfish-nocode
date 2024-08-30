package cn.com.mfish.common.dataset.datatable;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.dataset.common.Constant;
import com.github.pagehelper.Page;

import java.util.Collection;
import java.util.List;

/**
 * @description: 元数据表格
 * @author: mfish
 * @date: 2023/3/20
 */
public class MetaDataTable extends Page<MetaDataRow> {
    private final MetaDataHeaders headers = new MetaDataHeaders();

    public MetaDataTable() {
        super();
    }

    public MetaDataTable(MetaDataHeaders headers) {
        this();
        addColumn(headers);
    }

    public MetaDataTable(MetaDataHeaders headers, int pageNum, int pageSize) {
        super(pageNum, pageSize);
        addColumn(headers);
    }

    public MetaDataTable(int pageNum, int pageSize) {
        super(pageNum, pageSize);
    }

    public void copyTable(MetaDataTable table) {
        if (!headers.isEmpty()) {
            headers.clear();
        }
        headers.addColumn(table.getColHeaders());
        super.setPageNum(table.getPageNum());
        super.setPageSize(table.getPageSize());
        super.setTotal(table.getTotal());
        this.addAll(table);
    }

    /**
     * 新增行
     *
     * @param row 行数据
     */
    @Override
    public boolean add(MetaDataRow row) {
        checkHeader(row.getColHeaders());
        return super.add(row);
    }

    /**
     * 新增行
     *
     * @param index 行索引
     * @param row   行数据
     */
    @Override
    public void add(int index, MetaDataRow row) {
        checkHeader(row.getColHeaders());
        super.add(index, row);
    }

    /**
     * 新增多行
     *
     * @param rows 行列表
     * @return 是否
     */
    @Override
    public boolean addAll(Collection<? extends MetaDataRow> rows) {
        for (MetaDataRow row : rows) {
            checkHeader(row.getColHeaders());
        }
        return super.addAll(rows);
    }

    /**
     * 新增多行
     *
     * @param index 索引
     * @param rows  行列表
     * @return 是否
     */
    @Override
    public boolean addAll(int index, Collection<? extends MetaDataRow> rows) {
        for (MetaDataRow row : rows) {
            checkHeader(row.getColHeaders());
        }
        return super.addAll(index, rows);
    }

    /**
     * 检查头列头是否相同
     *
     * @param metaDataHeaders 元数据头
     */
    private void checkHeader(MetaDataHeaders metaDataHeaders) {
        if (!metaDataHeaders.equals(this.headers)) {
            throw new MyRuntimeException(Constant.ErrorRowException);
        }
    }

    /**
     * 增加列
     *
     * @param columns 列
     */
    public void addColumn(MetaDataHeaders columns) {
        for (MetaDataHeader header : columns.values()) {
            addColumn(header);
        }
    }

    /**
     * 增加列
     *
     * @param header 列
     */
    public void addColumn(MetaDataHeader header) {
        this.headers.addColumn(header);
        for (MetaDataRow row : this) {
            row.addColumn(header, null);
        }
    }

    /**
     * 移除多列
     *
     * @param colNames 列集合
     */
    public void removeColumns(List<String> colNames) {
        for (String colName : colNames) {
            this.headers.remove(colName);
        }
        for (MetaDataRow row : this) {
            for (String colName : colNames) {
                row.removeColumn(colName);
            }
        }
    }

    /**
     * 表格删除一列
     *
     * @param colName 列名
     */
    public void removeColumn(String colName) {
        this.headers.remove(colName);
        for (MetaDataRow row : this) {
            row.removeColumn(colName);
        }
    }

    /**
     * 表格根据索引删除一列
     *
     * @param index 索引
     */
    public void removeColumn(int index) {
        MetaDataHeader header = headers.getColHeader(index);
        if (header != null) {
            removeColumn(header.getColName());
        }
    }

    /**
     * 合并表格
     *
     * @param table 新表格
     */
    public void mergeTable(MetaDataTable table) {
        if (this.size() != table.size()) {
            throw new MyRuntimeException("行数不相等不允许合并");
        }
        this.addColumn(table.getColHeaders());
        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < table.getColHeaders().size(); j++) {
                this.get(i).addColumn(table.getColHeader(j), table.getCellValue(i, j));
            }
        }
    }

    public void mergeTable(List<MetaDataCell> list) {
        if (this.size() != list.size()) {
            throw new MyRuntimeException("行数不相等不允许合并");
        }
        if (list.isEmpty()) {
            throw new MyRuntimeException(Constant.NotFoundException);
        }
        MetaDataHeader header = list.getFirst().getHeader();
        addColumn(header);
        for (int i = 0; i < this.size(); i++) {
            this.get(i).addColumn(header, list.get(i).getValue());
        }
    }

    /**
     * 获取所有列
     *
     * @return 列头
     */
    public MetaDataHeaders getColHeaders() {
        return headers;
    }

    /**
     * 判断是否包含列
     *
     * @param colName 列名
     * @return 是否
     */
    public boolean containColumn(String colName) {
        if (headers == null || headers.isEmpty()) {
            return false;
        }
        return headers.containsKey(colName);
    }

    /**
     * 根据列索引获取列
     *
     * @param index 索引
     * @return 列头
     */
    public MetaDataHeader getColHeader(int index) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        return headers.getColHeader(index);
    }

    /**
     * 根据列名称获取
     *
     * @param colName 列别名
     * @return 列头
     */
    public MetaDataHeader getColHeader(String colName) {
        if (containColumn(colName)) {
            return headers.getColHeader(colName);
        }
        return null;
    }

    /**
     * 根据行索引列别名获取当前值
     *
     * @param rowIndex 行索引
     * @param colName  列名称
     * @return 返回
     */
    public Object getCellValue(int rowIndex, String colName) {
        MetaDataRow biMetaDataRow = this.get(rowIndex);
        if (biMetaDataRow == null) {
            return null;
        }
        return biMetaDataRow.getCellValue(colName);
    }

    /**
     * 根据行索引 列索引获取值
     *
     * @param rowIndex 行索引
     * @param colIndex 列索引
     * @return 返回
     */
    public Object getCellValue(int rowIndex, int colIndex) {
        MetaDataRow biMetaDataRow = this.get(rowIndex);
        if (biMetaDataRow == null) {
            return null;
        }
        return biMetaDataRow.getCellValue(colIndex);
    }
}
