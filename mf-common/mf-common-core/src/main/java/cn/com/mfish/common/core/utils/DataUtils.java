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
     * @param obj1    第一个运算对象
     * @param obj2    第二个运算对象
     * @param operator 运算符枚举
     * @return 运算结果
     */
    public static Object calculator(Object obj1, Object obj2, Operator operator) {
        BigDecimal value1 = switchDecimal(obj1);
        BigDecimal value2 = switchDecimal(obj2);
        return operator.execute(value1, value2);
    }

    /**
     * 将数值类型转化为bigDecimal类型
     *
     * @param obj 待转换的数值对象，可以是Short、Integer、Long、Float、Double、BigDecimal或String类型
     * @return 转换后的BigDecimal对象，如果输入为null或转换失败则返回null
     */
    public static BigDecimal switchDecimal(Object obj) {
        if (obj == null) return null;
        BigDecimal value;
        switch (obj) {
            case Short ignored -> value = new BigDecimal((short) obj);
            case Integer ignored -> value = new BigDecimal((int) obj);
            case Long ignored -> value = new BigDecimal((long) obj);
            case Float ignored -> value = BigDecimal.valueOf((float) obj);
            case Double ignored -> value = BigDecimal.valueOf((double) obj);
            case BigDecimal bigDecimal -> value = bigDecimal;
            default -> {
                //如果是字符串类型强转数字类型
                try {
                    value = BigDecimal.valueOf(Double.parseDouble((String) obj));
                } catch (NumberFormatException e) {
                    log.error(UNKNOWN_DATA);
                    return null;
                }
            }
        }
        return value;
    }

    /**
     * 数字大小比较
     *
     * @param obj1 待比较的第1个数字对象
     * @param obj2 待比较的第2个数字对象
     * @return 比较结果，-1表示obj1小于obj2，0表示两者相等，1表示obj1大于obj2
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
     * 该方法用于比较两个对象转换为字符串后的大小，主要用于需要对字符串进行排序或比较的场景
     * 在进行比较之前，会首先调用NullCompare方法来检查两个对象是否为null，以及它们的相等性
     * 这是为了处理null值的特殊逻辑，确保比较操作的正确性
     * 如果NullCompare返回的结果不为继续比较，则直接返回该结果
     * 否则，将对象转为字符串并使用字符串的compareTo方法进行比较
     *
     * @param obj1 第一个待比较的对象
     * @param obj2 第二个待比较的对象
     * @return 比较结果，与String的compareTo方法返回值一致，小于0表示obj1小于obj2，等于0表示两者相等，大于0表示obj1大于obj2
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
     * @return 返回比较结果
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
     * @param obj1 第一个日期对象，可以是任意类型
     * @param obj2 第二个日期对象，可以是任意类型
     * @return 如果obj1为null，返回-1；如果obj2为null，返回1；否则返回日期比较的结果
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
     * @param obj1 第一个加数
     * @param obj2 第二个加数
     * @return 两个加数的和，如果任一加数为null，则返回null
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
     * @param obj1 被减数，可以是Number的任意实现类对象
     * @param obj2 减数，可以是Number的任意实现类对象
     * @return 返回两个对象相减的结果，如果任一对象为null，则返回null
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
     * @param obj1 第一个乘数，可以是任何实现了BigDecimal类型的对象
     * @param obj2 第二个乘数，可以是任何实现了BigDecimal类型的对象
     * @return 返回两个乘数的乘积，结果为BigDecimal类型；如果任一乘数为null，则返回null
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
     * 执行两个对象的除法运算
     * 该方法接收两个对象，并使用BigDecimal进行精确的除法运算
     * 选择使用BigDecimal是为了处理可能的数值类型，提高运算的精度和灵活性
     *
     * @param obj1 被除数，可以是任何数值类型，将被转换为BigDecimal
     * @param obj2 除数，可以是任何数值类型，将被转换为BigDecimal
     * @return 返回除法运算的结果，如果除数为零或任一参数为null，则返回null
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
     * 计算平均值
     * <p>
     * 该方法用于计算给定对象与集合大小的平均值，通常用于统计计算等场景
     * 由于方法的参数和返回类型均为Object，此处的说明具有一定的假设性，实际使用时应确保类型安全
     *
     * @param obj1 被除数，理论上可以是任何类型，但应确保能与size进行数值运算
     *             实际使用时应进行相应的类型检查和转换
     * @param size 集合大小，决定了平均值的分母部分，应为数值类型，如Integer或Double等
     *             实际使用时应确保不为零以避免除零错误
     * @return 返回平均值，类型为Object，实际类型依赖于输入参数的具体类型
     *         例如，如果obj1和size都是Integer类型，则返回值为Double类型，表示平均值
     */
    public static Object avg(Object obj1, Object size) {
        return divide(obj1, size);
    }

    /**
     * 获取绝对值
     * 本方法旨在处理各类数值对象的绝对值获取，内部使用BigDecimal进行转换和处理，以确保数据精度
     * 主要针对的是可转换为BigDecimal的数值，包括基本数据类型和包装类等
     *
     * @param object 数据 传入的数值对象，支持基本数据类型及其包装类，或其他可转换为BigDecimal的数据类型
     * @return 返回数值的绝对值如果输入的数据类型无法转换为BigDecimal，则原样返回
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
     * <p>
     * 将给定的对象转换为BigDecimal，然后进行向上取整操作，确保结果为最接近的整数值
     * 如果对象不能转换为BigDecimal，则直接返回原对象
     *
     * @param object 需要进行向上取整处理的对象，预期为数值类型
     * @return 向上取整后的对象，或原对象（如果无法转换）
     */
    public static Object getCeiling(Object object) {
        BigDecimal value = switchDecimal(object);
        if (value != null) {
            return value.setScale(0, RoundingMode.CEILING).longValue();
        }
        return object;
    }

    /**
     * 向下取整方法，用于将传入的对象转换为最接近且小于等于该对象值的整数
     * 主要处理BigDecimal类型的数据，但也可以处理其他可转换为BigDecimal的对象
     *
     * @param object 待处理的对象，可以是BigDecimal类型或其他可转换为BigDecimal的类型
     * @return 处理后的对象，向下取整后的整数或原对象（如果无法处理）
     */
    public static Object getFloor(Object object) {
        BigDecimal value = switchDecimal(object);
        if (value != null) {
            return value.setScale(0, RoundingMode.FLOOR).longValue();
        }
        return object;
    }

    /**
     * 计算e的x次方
     * 该方法接受一个Object类型的参数，尝试将其转换为BigDecimal，
     * 然后计算e的该BigDecimal值次方的结果如果转换失败，
     * 则直接返回原始输入
     *
     * @param object 任何可以转换为BigDecimal的对象，通常代表一个数值
     * @return 计算后的结果，如果转换失败则返回原始输入
     */
    public static Object getExp(Object object) {
        BigDecimal value = switchDecimal(object);
        if (value != null) {
            return Math.pow(Math.E, value.doubleValue());
        }
        return object;
    }

    /**
     * 计算一个数的指定次幂
     *
     * @param base  基数，以字符串形式输入
     * @param value 指数，以字符串形式输入
     * @return 返回计算结果，即基数的指数次幂
     */
    public static double power(String base, String value) {
        double d1 = switchDecimal(base).doubleValue();
        double d2 = switchDecimal(value).doubleValue();
        return Math.pow(d1, d2);

    }

    /**
     * 计算给定值的对数
     * 如果提供了基数，则计算指定基数的对数；否则计算自然对数
     *
     * @param value 数值对象，用于计算对数
     * @param base 基数对象，可为空如果计算自然对数
     * @return 计算得到的对数值
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
     * @param object 待处理的字符串对象，可以是任何类型
     * @return 返回删除左边空格后的字符串，如果输入为null或空字符串，则返回空字符串
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
     * @param object 待处理的字符串对象，可以是任何类型，但会转换为字符串进行处理
     * @return 返回删除右边空格后的字符串，如果输入为null或空字符串，则返回空字符串
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
     * @param str1 需要进行替换操作的源字符串
     * @param str2 需要被替换的子字符串
     * @param str3 用于替换旧子字符串的新子字符串
     * @return 返回替换后的字符串如果源字符串中不包含需要被替换的子字符串，则返回源字符串本身
     */
    public static String replace(String str1, String str2, String str3) {
        return str1.replace(str2, str3);
    }

    /**
     * 四舍五入
     *
     * @param value 需要四舍五入的值
     * @param num   保留几位小数
     * @return 四舍五入后的结果
     */
    public static Double getRound(Object value, int num) {
        BigDecimal b = switchDecimal(value);
        return b.setScale(num, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 开方
     *
     * @param value 需要开放的值
     * @return 开方后的结果
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
     * @return 返回字段
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
     * @param strDate 字符串形式的日期
     * @return 转换后的Date对象
     * @throws ParseException 如果日期格式不正确，抛出此异常
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
