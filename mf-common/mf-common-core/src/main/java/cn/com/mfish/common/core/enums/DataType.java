package cn.com.mfish.common.core.enums;

import cn.com.mfish.common.core.constants.DataConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @description: 数据类型
 * @author: mfish
 * @date: 2023/3/20
 */
public enum DataType {
    UNKNOWN("unknown", SlimType.UNKNOWN),//未知类型
    STRING("String", SlimType.STRING),//字符类型
    BOOLEAN("Boolean", SlimType.BOOLEAN),//布尔类型
    INTEGER("Integer", SlimType.NUMBER),//整数类型
    LONG("Long", SlimType.NUMBER),//整数类型
    FLOAT("Float", SlimType.NUMBER),//浮点类型
    DOUBLE("Double", SlimType.NUMBER),//双精度类型
    BIGDECIMAL("BigDecimal", SlimType.NUMBER),//双精度类型
    DATE("Date", SlimType.NUMBER);//日期类型

    /**
     * 精简类型
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum SlimType {
        UNKNOWN("unknown"),
        STRING("string"),
        NUMBER("number"),
        BOOLEAN("boolean"),
        DATE("date");

        private final String type;

        SlimType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    /**
     * 数据类型
     */
    private final String dataType;
    /**
     * -- GETTER --
     *  获取精简类型
     *
     * @return
     */
    @Getter
    private final SlimType slimType;

    DataType(String dataType, SlimType slimType) {
        this.dataType = dataType;
        this.slimType = slimType;
    }

    private static final Map<String, DataType> typeMap = new HashMap<>();

    /**
     * 数据库数据类型转换java类型(部分类型不完全准确)
     */
    static {
        for (DataType con : DataType.values()) {
            typeMap.put(con.dataType, con);
        }
        typeMap.put(DataConstant.DataType.ARRAY, DataType.STRING);
        typeMap.put(DataConstant.DataType.TEXT, DataType.STRING);
        typeMap.put(DataConstant.DataType.MEDIUMTEXT, DataType.STRING);
        typeMap.put(DataConstant.DataType.TINYTEXT, DataType.STRING);
        typeMap.put(DataConstant.DataType.LONGTEXT, DataType.STRING);
        typeMap.put(DataConstant.DataType.CHAR, DataType.STRING);
        typeMap.put(DataConstant.DataType.LONGVARCHAR, DataType.STRING);
        typeMap.put(DataConstant.DataType.BINARY, DataType.STRING);
        typeMap.put(DataConstant.DataType.VARBINARY, DataType.STRING);
        typeMap.put(DataConstant.DataType.LONGVARBINARY, DataType.STRING);
        typeMap.put(DataConstant.DataType.NULL, DataType.STRING);
        typeMap.put(DataConstant.DataType.OTHER, DataType.STRING);
        typeMap.put(DataConstant.DataType.BLOB, DataType.STRING);
        typeMap.put(DataConstant.DataType.CLOB, DataType.STRING);
        typeMap.put(DataConstant.DataType.CURSOR, DataType.STRING);
        typeMap.put(DataConstant.DataType.NVARCHAR, DataType.STRING);
        typeMap.put(DataConstant.DataType.NCHAR, DataType.STRING);
        typeMap.put(DataConstant.DataType.NCLOB, DataType.STRING);
        typeMap.put(DataConstant.DataType.STRUCT, DataType.STRING);
        typeMap.put(DataConstant.DataType.JAVA_OBJECT, DataType.STRING);
        typeMap.put(DataConstant.DataType.VARCHAR2, DataType.STRING);
        typeMap.put(DataConstant.DataType.STRING, DataType.STRING);
        typeMap.put(DataConstant.DataType.VARCHAR, DataType.STRING);
        typeMap.put(DataConstant.DataType.TINYBLOB, DataType.STRING);
        typeMap.put(DataConstant.DataType.MEDIUMBLOB, DataType.STRING);
        typeMap.put(DataConstant.DataType.LONGBLOB, DataType.STRING);
        typeMap.put(DataConstant.DataType.GEOMETRY, DataType.STRING);
        typeMap.put(DataConstant.DataType.UNDEFINED, DataType.STRING);

        typeMap.put(DataConstant.DataType.BIT, DataType.INTEGER);
        typeMap.put(DataConstant.DataType.INT, DataType.INTEGER);
        typeMap.put(DataConstant.DataType.INT4, DataType.INTEGER);
        typeMap.put(DataConstant.DataType.INT8, DataType.INTEGER);
        typeMap.put(DataConstant.DataType.TINYINT, DataType.INTEGER);
        typeMap.put(DataConstant.DataType.SMALLINT, DataType.INTEGER);
        typeMap.put(DataConstant.DataType.INTEGER, DataType.INTEGER);
        typeMap.put(DataConstant.DataType.BIGINT, DataType.LONG);
        typeMap.put(DataConstant.DataType.MEDIUMINT, DataType.INTEGER);
        typeMap.put(DataConstant.DataType.FLOAT, DataType.FLOAT);
        typeMap.put(DataConstant.DataType.REAL, DataType.FLOAT);
        typeMap.put(DataConstant.DataType.DOUBLE, DataType.DOUBLE);
        typeMap.put(DataConstant.DataType.NUMERIC, DataType.BIGDECIMAL);
        typeMap.put(DataConstant.DataType.DECIMAL, DataType.BIGDECIMAL);
        typeMap.put(DataConstant.DataType.BOOLEAN, DataType.BOOLEAN);
        typeMap.put(DataConstant.DataType.NUMBER, DataType.FLOAT);

        typeMap.put(DataConstant.DataType.DATETIME, DataType.DATE);
        typeMap.put(DataConstant.DataType.TIME, DataType.DATE);
        typeMap.put(DataConstant.DataType.TIMESTAMP, DataType.DATE);
        typeMap.put(DataConstant.DataType.DATETIMEOFFSET, DataType.DATE);
        typeMap.put(DataConstant.DataType.DATE, DataType.DATE);
        typeMap.put(DataConstant.DataType.YEAR, DataType.DATE);
    }

    public String getValue() {
        return dataType;
    }


    /**
     * 根据值获取操作条件
     *
     * @param type 类型
     * @return
     */
    public static DataType forType(String type) {
        type = type.toUpperCase(Locale.ROOT);
        if (typeMap.containsKey(type)) {
            return typeMap.get(type);
        }
        return DataType.UNKNOWN;
    }

    /**
     * 通过精简数据类型获取数据类型
     * @param slimType 精简数据类型
     * @return 数据类型
     */
    public static DataType forType(SlimType slimType) {
        return forType(slimType.toString());
    }

    /**
     * 根据值获取精简数据类型
     *
     * @param type 类型
     * @return
     */
    public static SlimType forSlimType(String type) {
        return forType(type).slimType;
    }

}