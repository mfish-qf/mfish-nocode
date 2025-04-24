package cn.com.mfish.common.dataset.datatable;

import cn.com.mfish.common.core.enums.DataType;
import cn.com.mfish.common.dataset.enums.TargetType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @description: 元数据列头
 * @author: mfish
 * @date: 2023/3/20
 */
public class MetaDataHeader implements Serializable {
    public MetaDataHeader() {

    }

    public MetaDataHeader(String colName, String fieldName, String expression, DataType dataType, TargetType targetType, String comment) {
        this.colName = colName;
        this.fieldName = fieldName;
        this.expression = expression;
        this.dataType = dataType;
        this.targetType = targetType;
        this.comment = comment;
    }

    /**
     * 用户重命名
     */
    @Getter
    private String rename;
    /**
     * 列名(必须唯一)
     * 通过字段自动生成列名
     */
    @Getter
    private String colName;
    /**
     * 字段名称 对应数据库中字段
     */
    @Getter
    private String fieldName;

    /**
     * 中文描述
     */
    @Getter
    private String comment;

    /**
     * 运算式子 运算指标设置该属性 主要用于显示
     */
    @Getter
    private String expression;

    /**
     * 数据类型
     */
    private DataType dataType;

    /**
     * 字段所属 表别名 原生查询不赋值
     */
    @Getter
    private String tableAlias;

    /**
     * 指标类型
     */
    @Getter
    private TargetType targetType;

    /**
     * 索引
     */
    @Setter
    @Getter
    private Integer index;


    public MetaDataHeader setDataType(String dataType) {
        this.dataType = DataType.forType(dataType);
        return this;
    }

    public MetaDataHeader setSlimType(DataType.SlimType slimType) {
        this.dataType = DataType.forType(slimType);
        return this;
    }

    public DataType.SlimType getDataType() {
        if (this.dataType == null) {
            return null;
        }
        return this.dataType.getSlimType();
    }

    public DataType getFullDataType() {
        return this.dataType;
    }

    public MetaDataHeader setRename(String rename) {
        this.rename = rename;
        return this;
    }

    public MetaDataHeader setColName(String colName) {
        this.colName = colName;
        return this;
    }

    public MetaDataHeader setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public MetaDataHeader setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public MetaDataHeader setExpression(String expression) {
        this.expression = expression;
        return this;
    }

    public MetaDataHeader setTargetType(TargetType targetType) {
        this.targetType = targetType;
        return this;
    }

    public MetaDataHeader setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetaDataHeader that = (MetaDataHeader) o;
        return Objects.equals(rename, that.rename)
                && Objects.equals(colName, that.colName)
                && Objects.equals(fieldName, that.fieldName)
                && Objects.equals(expression, that.expression)
                && Objects.equals(comment, that.comment)
                && dataType == that.dataType
                && Objects.equals(tableAlias, that.tableAlias)
                && targetType == that.targetType;
    }

    /**
     * 拷贝一个头
     *
     * @return 列头
     */
    public MetaDataHeader copy() {
        MetaDataHeader newHeader = new MetaDataHeader();
        BeanUtils.copyProperties(this, newHeader);
        newHeader.setDataType(this.getFullDataType().getValue());
        return newHeader;
    }
}
