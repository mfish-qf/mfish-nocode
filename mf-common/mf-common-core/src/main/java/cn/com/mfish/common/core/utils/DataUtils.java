package cn.com.mfish.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 数据处理通用方法
 * @author: mfish
 * @date: 2023/5/9 20:34
 */
@Slf4j
public class DataUtils {
    private static final String DATA_NULL = "错误:数值不允许为空";
    private static final String UNKNOWN_DATA = "错误:未知的数值类型";

    /**
     * 计算接口
     */
    public interface Operator {
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
            value = BigDecimal.valueOf((float) obj);
        } else if (obj instanceof Double) {
            value = BigDecimal.valueOf((double) obj);
        } else if (obj instanceof BigDecimal) {
            value = (BigDecimal) obj;
        } else {
            //如果是字符串类型强转数字类型
            try {
                value = BigDecimal.valueOf(Double.parseDouble((String) obj));
            } catch (NumberFormatException e) {
                log.error(UNKNOWN_DATA);
                return BigDecimal.ZERO;
            }
        }
        return value;
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
        if (value1 == null) {
            return -1;
        }
        if (value2 == null) {
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
     * 求和
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static Object sum(Object obj1, Object obj2) {
        return calculator(obj1, obj2,
                (value1, value2) -> {
                    if (value1 == null || value2 == null) {
                        return null;
                    }
                    return value1.add(value2).setScale(4, RoundingMode.HALF_UP);
                });
    }

    /**
     * 求差
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static Object subtract(Object obj1, Object obj2) {
        return calculator(obj1, obj2,
                (value1, value2) -> {
                    if (value1 == null || value2 == null) {
                        return null;
                    }
                    return value1.subtract(value2).setScale(4, RoundingMode.HALF_UP);
                });
    }

    /**
     * 数字转字符串类型
     */
    public static String num2char(Object value) {
        if (null == value) {
            return "";
        }
        return value + "";
    }

    /**
     * 字符转数字类型
     */

    public static BigDecimal char2num(Object value) {
        return switchDecimal(value);
    }

    /**
     * 求乘积
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static Object multiply(Object obj1, Object obj2) {
        return calculator(obj1, obj2,
                (value1, value2) -> {
                    if (value1 == null || value2 == null) {
                        return null;
                    }
                    return value1.multiply(value2).setScale(4, RoundingMode.HALF_UP);
                });
    }

    /**
     * 除法
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static Object divide(Object obj1, Object obj2) {
        return calculator(obj1, obj2, (value1, value2) -> {
            if (value1 == null || value2 == null || value2.equals(BigDecimal.ZERO)) {
                return null;
            }
            return value1.divide(value2, 4, RoundingMode.HALF_UP);
        });
    }

    /**
     * 算平均值
     *
     * @param obj1
     * @param size
     * @return
     */
    public static Object avg(Object obj1, Object size) {
        return divide(obj1, size);
    }

    /**
     * 获取绝对值
     *
     * @param object 数据
     * @return
     */
    public static Object getABS(Object object) {
        BigDecimal value = switchDecimal(object);
        if (value != null) {
            return Math.abs(value.doubleValue());
        }
        return object;
    }

    /**
     * 向上取整
     *
     * @param object
     * @return
     */
    public static Object getCeiling(Object object) {
        BigDecimal value = switchDecimal(object);
        if (value != null) {
            return value.setScale(0, RoundingMode.CEILING).longValue();
        }
        return object;
    }

    /**
     * 向下取整
     *
     * @param object
     * @return
     */
    public static Object getFloor(Object object) {
        BigDecimal value = switchDecimal(object);
        if (value != null) {
            return value.setScale(0, RoundingMode.FLOOR).longValue();
        }
        return object;
    }

    /**
     * e的x次方
     *
     * @param object
     * @return
     */
    public static Object getExp(Object object) {
        BigDecimal value = switchDecimal(object);
        if (value != null) {
            return Math.pow(Math.E, value.doubleValue());
        }
        return object;
    }

    /**
     * x的y次方
     *
     * @param base  基数
     * @param value 指数
     * @return
     */
    public static double power(String base, String value) {
        double d1 = switchDecimal(base).doubleValue();
        double d2 = switchDecimal(value).doubleValue();
        return Math.pow(d1, d2);

    }

    /**
     * log
     *
     * @param value
     * @param base
     * @return
     */
    public static double getLog(Object base, Object value) {
        if (null == base) {
            return Math.log(switchDecimal(value).doubleValue());
        } else {
            return Math.log(switchDecimal(value).doubleValue()) / Math.log(switchDecimal(base).doubleValue());
        }
    }

    /**
     * 删除字符左边空格
     *
     * @param object
     * @return
     */
    public static String getLTrim(Object object) {
        if (object == null || StringUtils.isEmpty(object.toString())) {
            return "";
        }
        return object.toString().replaceAll("^\\s+", "");
    }

    /**
     * 删除字符右边空格
     *
     * @param object
     * @return
     */
    public static String getRTrim(Object object) {
        if (object == null || StringUtils.isEmpty(object.toString())) {
            return "";
        }
        return object.toString().replaceAll("\\s+$", "");
    }

    /**
     * 替换
     *
     * @param str1 字符
     * @param str2 字符
     * @param str3 字符
     * @return
     */
    public static String replace(String str1, String str2, String str3) {
        return str1.replace(str2, str3);
    }

    /**
     * 四舍五入
     *
     * @param value 需要四舍五入的值
     * @param num   保留几位小数
     * @return
     */
    public static Double getRound(Object value, int num) {
        BigDecimal b = switchDecimal(value);
        return b.setScale(num, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 开方
     *
     * @param value 需要开放的值
     * @return
     */
    public static double getSqrt(Object value) {
        return Math.sqrt(switchDecimal(value).doubleValue());
    }

    /**
     * 截取字段
     *
     * @param str    字符串
     * @param start  开始
     * @param length 长度
     * @return
     */
    public static String subStr(String str, int start, int length) {
        if (start > 0) {
            start = start - 1;
        } else {
            start = 0;
        }
        return StringUtils.substring(str, start, start + length);
    }

    /**
     * 转换日期
     *
     * @param strDate 字符日期
     * @return
     * @throws ParseException
     */
    public static Date switchDate(String strDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String value = strDate.trim().replace("/", "-");
        if (!value.contains(":")) {
            value += " 00:00:00";
        }
        return df.parse(value);
    }
}
