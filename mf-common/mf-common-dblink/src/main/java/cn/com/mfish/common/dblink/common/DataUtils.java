package cn.com.mfish.common.dblink.common;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.dblink.datatable.MetaDataHeader;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * @author mfish
 * @date 2023/3/21
 */
@Slf4j
public class DataUtils {
    private static final String DATA_NULL = "错误:数值不允许为空";
    private static final String UNKNOWN_DATA = "错误:未知的数值类型";

    /**
     * 计算接口
     */
    interface Operator {
        Object execute(BigDecimal value1, BigDecimal value2);
    }

    /**
     * 运算
     *
     * @param obj1
     * @param obj2
     * @param operator
     * @return
     */
    public static Object calculator(Object obj1, Object obj2, Operator operator) {
        BigDecimal value1 = switchDecimal(obj1);
        BigDecimal value2 = switchDecimal(obj2);
        return operator.execute(value1, value2);
    }

    /**
     * 将数值类型转化为bigdecimal类型
     *
     * @param obj
     * @return
     */
    public static BigDecimal switchDecimal(Object obj) {
        BigDecimal value;
        if (obj instanceof Short) {
            value = new BigDecimal((short) obj);
        } else if (obj instanceof Integer) {
            value = new BigDecimal((int) obj);
        } else if (obj instanceof Long) {
            value = new BigDecimal((long) obj);
        } else if (obj instanceof Float) {
            value = new BigDecimal((float) obj);
        } else if (obj instanceof Double) {
            value = new BigDecimal((double) obj);
        } else if (obj instanceof BigDecimal) {
            value = (BigDecimal) obj;
        } else if (obj instanceof String) {
            //如果是字符串类型强转数字类型
            try {
                value = new BigDecimal(Double.parseDouble((String) obj));
            } catch (NumberFormatException e) {
                log.error(UNKNOWN_DATA);
                return null;
            }
        } else {
            log.error(UNKNOWN_DATA);
            return null;
        }
        return value;
    }

    /**
     * 获取列名 当列名
     *
     * @param col 列
     * @return
     */
    public static String getColName(MetaDataHeader col) {
        //表达式为空时 返回别名
        if (StringUtils.isEmpty(col.getExpression())) {
            if (StringUtils.isEmpty(col.getColName())) {
                col.setColName(col.getFieldName());
            }
            return col.getColName();
        }
        //表达式不为空 且别名=字段名（别名未修改） 返回表达式
        if (col.getColName().equals(col.getFieldName())) {
            return col.getExpression();
        }
        //别名已修改 返回别名
        return col.getColName();
    }


    /**
     * 数字大小比较
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static int numCompare(Object obj1, Object obj2) {
        return (int) calculator(obj1, obj2, (value1, value2) -> {
            int value = NullCompare(value1, value2);
            if (value <= 1) {
                return value;
            }
            return value1.compareTo(value2);
        });
    }

    /**
     * 字符串大小比较
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static int strCompare(Object obj1, Object obj2) {
        int value = NullCompare(obj1, obj2);
        if (value <= 1) {
            return value;
        }
        return obj1.toString().compareTo(obj2.toString());
    }

    /**
     * 空比较 如果都不为空返回2
     *
     * @param value1 值1
     * @param value2 值2
     * @return
     */
    public static int NullCompare(Object value1, Object value2) {
        if (value1 == null && value2 == null) {
            return 0;
        }
        if (value1 == null && value2 != null) {
            return -1;
        }
        if (value1 != null && value2 == null) {
            return 1;
        }
        return 2;
    }

    /**
     * 日期比较
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static int dateCompare(Object obj1, Object obj2) {
        if (obj1 == null) {
            return -1;
        }
        if (obj2 == null) {
            return 1;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(obj1.toString()).compareTo(sdf.parse(obj2.toString()));
        } catch (ParseException e) {
            return -1;
        }
    }

    /**
     * 获取字段别名如果重复会自动递增
     *
     * @param i       从0开始循环
     * @param colName 列名称
     * @param set     从外将别名加入set
     * @return
     */
    public static String getColName(int i, String colName, Set<String> set) {
        while (true) {
            if (!set.contains(colName)) {
                return colName;
            }
            if (colName.endsWith("_" + i)) {
                colName = colName.substring(0, colName.length() - (i + "").length() - 1);
            }
            return getColName(++i, colName + "_" + i, set);
        }
    }
}