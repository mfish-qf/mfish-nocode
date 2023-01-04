package cn.com.mfish.code.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: mfish
 * @date: 2022/08/25 10:58
 */
public enum DataType {
    UNKNOWN("unknown"),//未知类型
    STRING("String"),//字符类型
    BOOLEAN("Boolean"),//布尔类型
    INTEGER("Integer"),//整数类型
    LONG("Long"),//整数类型
    FLOAT("Float"),//浮点类型
    DOUBLE("Double"),//双精度类型
    BIGDECIMAL("BigDecimal"),//双精度类型
    DATE("Date");//日期类型

    private String dataType;

    DataType(String dataType) {
        this.dataType = dataType;
    }

    private static Map<String, DataType> typeMap = new HashMap<>();

    static {
        for (DataType con : DataType.values()) {
            typeMap.put(con.dataType, con);
        }
        typeMap.put("ARRAY", DataType.STRING);
        typeMap.put("TEXT", DataType.STRING);
        typeMap.put("MEDIUMTEXT", DataType.STRING);
        typeMap.put("TINYTEXT", DataType.STRING);
        typeMap.put("LONGTEXT", DataType.STRING);
        typeMap.put("CHAR", DataType.STRING);
        typeMap.put("LONGVARCHAR", DataType.STRING);
        typeMap.put("BINARY", DataType.STRING);
        typeMap.put("VARBINARY", DataType.STRING);
        typeMap.put("LONGVARBINARY", DataType.STRING);
        typeMap.put("NULL", DataType.STRING);
        typeMap.put("OTHER", DataType.STRING);
        typeMap.put("BLOB", DataType.STRING);
        typeMap.put("CLOB", DataType.STRING);
        typeMap.put("CURSOR", DataType.STRING);
        typeMap.put("NVARCHAR", DataType.STRING);
        typeMap.put("NCHAR", DataType.STRING);
        typeMap.put("NCLOB", DataType.STRING);
        typeMap.put("STRUCT", DataType.STRING);
        typeMap.put("JAVA_OBJECT", DataType.STRING);
        typeMap.put("VARCHAR2", DataType.STRING);
        typeMap.put("STRING", DataType.STRING);
        typeMap.put("VARCHAR", DataType.STRING);
        typeMap.put("TINYBLOB", DataType.STRING);
        typeMap.put("MEDIUMBLOB", DataType.STRING);
        typeMap.put("LONGBLOB", DataType.STRING);
        typeMap.put("GEOMETRY",DataType.STRING);
        typeMap.put("UNDEFINED", DataType.STRING);

        typeMap.put("BIT", DataType.INTEGER);
        typeMap.put("INT", DataType.INTEGER);
        typeMap.put("INT4", DataType.INTEGER);
        typeMap.put("INT8", DataType.INTEGER);
        typeMap.put("TINYINT", DataType.INTEGER);
        typeMap.put("SMALLINT", DataType.INTEGER);
        typeMap.put("INTEGER", DataType.INTEGER);
        typeMap.put("BIGINT", DataType.INTEGER);
        typeMap.put("MEDIUMINT", DataType.INTEGER);
        typeMap.put("FLOAT", DataType.FLOAT);
        typeMap.put("REAL", DataType.FLOAT);
        typeMap.put("DOUBLE", DataType.DOUBLE);
        typeMap.put("NUMERIC", DataType.BIGDECIMAL);
        typeMap.put("DECIMAL", DataType.BIGDECIMAL);
        typeMap.put("BOOLEAN", DataType.BOOLEAN);
        typeMap.put("NUMBER", DataType.FLOAT);

        typeMap.put("DATETIME", DataType.DATE);
        typeMap.put("TIME", DataType.DATE);
        typeMap.put("TIMESTAMP", DataType.DATE);
        typeMap.put("DATETIMEOFFSET", DataType.DATE);
        typeMap.put("DATE", DataType.DATE);
        typeMap.put("YEAR", DataType.DATE);
    }

    public String getValue() {
        return dataType;
    }

    public void setDataType(String dataType) {
        if (typeMap.containsKey(dataType)) {
            this.dataType = typeMap.get(dataType).getValue();
        }
        this.dataType = DataType.UNKNOWN.getValue();
    }

    /**
     * 根据值获取操作条件
     *
     * @param con
     * @return
     */
    public static DataType forType(String con) {
        if (typeMap.containsKey(con)) {
            return typeMap.get(con);
        }
        return DataType.UNKNOWN;
    }
}
