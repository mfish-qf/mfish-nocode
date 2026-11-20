package cn.com.mfish.project.utils;

import cn.com.mfish.project.dto.ScheduleConfigDTO;
import cn.com.mfish.project.dto.SkippedDateDTO;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 排期日历工作日核心计算工具类
 * 精准跳过周六日、跳过节假日，计入调休补班工作日，推算实际完成日期
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public class ScheduleDateUtils {
    /** 标准日期格式 */
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** 默认排期开始日期(无任何配置时的兜底起点) */
    public static final LocalDate DEFAULT_START_DATE = LocalDate.of(2026, 9, 8);

    private ScheduleDateUtils() {
    }

    /**
     * 判断指定日期是否为周末(周六或周日)
     *
     * @param date 日期
     * @return 是否周末
     */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 判断指定日期是否在排期中被跳过(休假/非工作日/手动设置的跳过日期)
     * 优先级：调休补班工作日绝不跳过 > 手动跳过日期 > 周末
     *
     * @param date   日期
     * @param config 排期配置
     * @return 是否跳过
     */
    public static boolean isDateSkipped(LocalDate date, ScheduleConfigDTO config) {
        String dateStr = date.format(FORMATTER);
        boolean skipWeekends = config == null || config.getSkipWeekends() == null || config.getSkipWeekends();
        // 1. 优先检查是否为调休补班工作日(补班日绝不跳过)
        if (config != null && config.getMakeupWorkdays() != null && config.getMakeupWorkdays().contains(dateStr)) {
            return false;
        }
        // 2. 检查是否在手动配置的跳过日期集合中
        if (config != null && config.getSkippedDates() != null) {
            for (SkippedDateDTO item : config.getSkippedDates()) {
                if (item != null && dateStr.equals(item.getDate())) {
                    return true;
                }
            }
        }
        // 3. 检查是否为周末
        return skipWeekends && isWeekend(date);
    }

    /**
     * 根据研发开始时间、预估工时(人/天)及排期配置，自动推导实际完成日期
     * 规则：
     * 1. 自动跳过手动设置的休假日/跳过日，以及周六与周日(可通过 skipWeekends 控制)
     * 2. 1 人/天工时占用 1 个有效工作日(起止日期相同)
     * 3. 2 人/天工时占用 2 个有效工作日(遇跳过日期自动后延顺延)
     * 4. 若开始日期恰好是跳过日/周末，自动从下一个有效工作日顺延起算
     * 5. 调休补班工作日即使在周末也计入工作
     *
     * @param startDate 开始日期
     * @param workload   预估工时(人/天)
     * @param config     排期配置
     * @return 实际完成日期
     */
    public static LocalDate calculateDevEndDate(LocalDate startDate, BigDecimal workload, ScheduleConfigDTO config) {
        if (startDate == null) {
            startDate = DEFAULT_START_DATE;
        }
        LocalDate current = startDate;
        // 若开始日期为跳过日或周末，则顺延至下一个有效工作日
        while (isDateSkipped(current, config)) {
            current = current.plusDays(1);
        }
        // 计算所需工作日数量(最少占用1个工作日，向上取整兼容0.5/1.5工时)
        BigDecimal effectiveWorkload = (workload == null || workload.compareTo(BigDecimal.ZERO) <= 0)
                ? BigDecimal.ONE : workload;
        int neededWorkdays = Math.max(1, (int) Math.ceil(effectiveWorkload.doubleValue()));
        int workdaysCounted = 1;
        while (workdaysCounted < neededWorkdays) {
            current = current.plusDays(1);
            if (!isDateSkipped(current, config)) {
                workdaysCounted++;
            }
        }
        return current;
    }

    /**
     * 获取有效工作日开始日期(若为跳过日或周末，自动顺延到下一个有效工作日)
     *
     * @param date   候选日期
     * @param config 排期配置
     * @return 有效工作日
     */
    public static LocalDate getWorkdayStartDate(LocalDate date, ScheduleConfigDTO config) {
        LocalDate current = date == null ? DEFAULT_START_DATE : date;
        while (isDateSkipped(current, config)) {
            current = current.plusDays(1);
        }
        return current;
    }

    /**
     * 获取指定日期的下一个有效工作日(跳过配置的休假日及周末)
     *
     * @param date   当前日期
     * @param config 排期配置
     * @return 下一个有效工作日
     */
    public static LocalDate getNextWorkday(LocalDate date, ScheduleConfigDTO config) {
        LocalDate current = date == null ? DEFAULT_START_DATE : date;
        current = current.plusDays(1);
        while (isDateSkipped(current, config)) {
            current = current.plusDays(1);
        }
        return current;
    }

    /**
     * 计算两个日期之间所跨越的所有自然月份列表(格式 yyyy-MM)
     *
     * @param startDate 开始日期
     * @param endDate   结束日期(为空时仅返回开始日期所在月份)
     * @return 月份列表
     */
    public static List<String> getSpannedMonths(LocalDate startDate, LocalDate endDate) {
        List<String> months = new ArrayList<>();
        if (startDate == null) {
            return months;
        }
        LocalDate end = endDate;
        if (end == null || end.isBefore(startDate)) {
            months.add(formatYearMonth(startDate));
            return months;
        }
        LocalDate cursor = startDate.withDayOfMonth(1);
        LocalDate endMonth = end.withDayOfMonth(1);
        while (!cursor.isAfter(endMonth)) {
            months.add(formatYearMonth(cursor));
            cursor = cursor.plusMonths(1);
        }
        return months;
    }

    /**
     * 计算 yyyy-MM 格式月份字符串
     */
    public static String formatYearMonth(LocalDate date) {
        return String.format("%d-%02d", date.getYear(), date.getMonthValue());
    }

    /**
     * 计算两个日期之间的自然天数(包含首尾)
     */
    public static long calculateDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * 解析排期配置中的跳过日期字符串集合(便于快速比对)
     *
     * @param config 排期配置
     * @return 跳过日期字符串集合
     */
    public static Set<String> extractSkippedDateSet(ScheduleConfigDTO config) {
        Set<String> dates = new HashSet<>();
        if (config != null && config.getSkippedDates() != null) {
            for (SkippedDateDTO item : config.getSkippedDates()) {
                if (item != null && item.getDate() != null) {
                    dates.add(item.getDate());
                }
            }
        }
        return dates;
    }
}
