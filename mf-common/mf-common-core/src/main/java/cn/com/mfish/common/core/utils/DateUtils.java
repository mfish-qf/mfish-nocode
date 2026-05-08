package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description: 时间工具类
 * @author: mfish
 * @date: 2024/7/1
 */
public class DateUtils {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};


    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * 获取当前时间，格式为yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间字符串
     */
    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取当前时间戳字符串，格式为yyyyMMddHHmmss
     *
     * @return 时间戳字符串
     */
    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    /**
     * 按指定格式获取当前时间字符串
     *
     * @param format 日期格式
     * @return 格式化后的当前时间字符串
     */
    public static String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    /**
     * 将日期格式化为yyyy-MM-dd字符串
     *
     * @param date 日期对象
     * @return 格式化后的日期字符串
     */
    public static String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    /**
     * 将日期按指定格式转换为字符串
     *
     * @param format 日期格式
     * @param date   日期对象
     * @return 格式化后的日期字符串
     */
    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 将字符串按指定格式解析为日期对象
     *
     * @param format 日期格式
     * @param ts     日期字符串
     * @return 解析后的日期对象
     * @throws MyRuntimeException 解析失败时抛出异常
     */
    public static Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new MyRuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 判断是否日期
     *
     * @param str 日期字符
     * @return 返回是否
     */
    public static boolean isDate(Object str) {
        Date date = parseDate(str);
        return date != null;
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 获取指定日期所在月份的开始时间
     */
    public static Date getMonthStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);   // 当月第一天
        cal.set(Calendar.HOUR_OF_DAY, 0);    // 00:00:00.000
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取指定日期所在月份的结束时间
     */
    public static Date getMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // 当月最后一天
        cal.set(Calendar.HOUR_OF_DAY, 23);   // 23:59:59.999
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}