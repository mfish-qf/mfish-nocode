package cn.com.mfish.common.dblink.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 数据类型
 * @author: mfish
 * @date: 2023/3/20
 */
public enum DataType {
    UNKNOWN("UNKNOWN"),//未知类型
    VARCHAR("VARCHAR"),//字符类型
    NUMERIC("NUMERIC"),//数字类型
    DATE("DATE");//日期类型

    private String dataType;

    DataType(String dataType) {
        this.dataType = dataType;
    }

    private static Map<String, DataType> typeMap = new HashMap<>();

    static {
        for (DataType con : DataType.values()) {
            typeMap.put(con.dataType, con);
        }
        typeMap.put("ARRAY", DataType.VARCHAR);
        typeMap.put("TEXT", DataType.VARCHAR);
        typeMap.put("MEDIUMTEXT", DataType.VARCHAR);
        typeMap.put("TINYTEXT", DataType.VARCHAR);
        typeMap.put("LONGTEXT", DataType.VARCHAR);
        typeMap.put("CHAR", DataType.VARCHAR);
        typeMap.put("LONGVARCHAR", DataType.VARCHAR);
        typeMap.put("BINARY", DataType.VARCHAR);
        typeMap.put("VARBINARY", DataType.VARCHAR);
        typeMap.put("LONGVARBINARY", DataType.VARCHAR);
        typeMap.put("NULL", DataType.VARCHAR);
        typeMap.put("OTHER", DataType.VARCHAR);
        typeMap.put("BLOB", DataType.VARCHAR);
        typeMap.put("CLOB", DataType.VARCHAR);
        typeMap.put("CURSOR", DataType.VARCHAR);
        typeMap.put("NVARCHAR", DataType.VARCHAR);
        typeMap.put("NCHAR", DataType.VARCHAR);
        typeMap.put("NCLOB", DataType.VARCHAR);
        typeMap.put("STRUCT", DataType.VARCHAR);
        typeMap.put("JAVA_OBJECT", DataType.VARCHAR);
        typeMap.put("VARCHAR2", DataType.VARCHAR);
        typeMap.put("STRING", DataType.VARCHAR);
        typeMap.put("VARCHAR", DataType.VARCHAR);
        typeMap.put("TINYBLOB", DataType.VARCHAR);
        typeMap.put("MEDIUMBLOB", DataType.VARCHAR);
        typeMap.put("LONGBLOB", DataType.VARCHAR);


        typeMap.put("BIT", DataType.NUMERIC);
        typeMap.put("INT", DataType.NUMERIC);
        typeMap.put("INT4", DataType.NUMERIC);
        typeMap.put("INT8", DataType.NUMERIC);
        typeMap.put("GEOMETRY",DataType.VARCHAR);
        typeMap.put("TINYINT", DataType.NUMERIC);
        typeMap.put("SMALLINT", DataType.NUMERIC);
        typeMap.put("INTEGER", DataType.NUMERIC);
        typeMap.put("BIGINT", DataType.NUMERIC);
        typeMap.put("FLOAT", DataType.NUMERIC);
        typeMap.put("REAL", DataType.NUMERIC);
        typeMap.put("DOUBLE", DataType.NUMERIC);
        typeMap.put("NUMERIC", DataType.NUMERIC);
        typeMap.put("DECIMAL", DataType.NUMERIC);
        typeMap.put("BOOLEAN", DataType.NUMERIC);
        typeMap.put("UNDEFINED", DataType.NUMERIC);
        typeMap.put("NUMBER", DataType.NUMERIC);
        typeMap.put("MEDIUMINT", DataType.NUMERIC);

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