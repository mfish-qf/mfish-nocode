package cn.com.mfish.project.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 人员名称字符串解析工具
 * 将 "周伟、王敏"、'["陈明", "赵刚"]' 等混合格式解析为纯粹的人员姓名数组
 * 与前端 usePersonnel.ts 中 parsePersonnelNames/formatPersonnelNames 逻辑保持一致
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public class PersonnelNameUtils {
    /** 待分配占位符 */
    public static final String UNASSIGNED = "待分配";
    /** 多人分隔符 */
    public static final String SEPARATOR = "、";
    /** 伪人员占位标识(非真实开发人员，解析与统计时应剔除) */
    private static final List<String> PLACEHOLDER_NAMES = List.of(
            "待分配", "待指派", "未安排", "不涉及", "❌不涉及❌", "UNASSIGNED", "无");

    private PersonnelNameUtils() {
    }

    /**
     * 判断是否为伪人员占位标识(待分配/待指派/不涉及 等，非真实开发人员)
     *
     * @param name 名称(可为 null)
     * @return 是否为占位符
     */
    public static boolean isPlaceholderName(String name) {
        if (name == null) {
            return true;
        }
        String str = name.trim();
        if (str.isEmpty()) {
            return true;
        }
        if (PLACEHOLDER_NAMES.contains(str)) {
            return true;
        }
        // 兜底: 含"不涉及"或以 ❌ 包裹的标识均视为占位
        return str.contains("不涉及") || (str.startsWith("❌") && str.endsWith("❌"));
    }

    /**
     * 清洗单个人员名称，去除首尾的方括号、花括号、转义符和各种单双引号
     *
     * @param name 原始名称
     * @return 清洗后的名称
     */
    public static String cleanPersonnelName(String name) {
        if (name == null) {
            return "";
        }
        String str = name.trim();
        if (str.isEmpty()) {
            return "";
        }
        // 过滤姓名中的【】标注 (如 "邢继康【运营】" → "邢继康")，兼容未闭合的尾部括号
        str = str.replaceAll("【[^】]*】", "").replaceAll("【[\\s\\S]*$", "").trim();
        return str.replaceAll("^[\\[\\{\\(\"'\\s\\\\]+|[\\]\\}\\)\"'\\s\\\\]+$", "").trim();
    }

    /**
     * 人员字符串解析：将 "周伟、王敏" 或 JSON 数组字符串解析为纯粹的人员姓名数组
     *
     * @param val 人员串
     * @return 姓名列表(自动去重、剔除空值与待分配占位)
     */
    public static List<String> parsePersonnelNames(String val) {
        List<String> result = new ArrayList<>();
        if (val == null) {
            return result;
        }
        String str = val.trim();
        if (str.isEmpty() || UNASSIGNED.equals(str)) {
            return result;
        }
        // 去除首尾的方括号与引号，兼容历史遗留 JSON 串格式
        str = str.replaceAll("^[\\[\\{\\s'\"]+|[\\]\\}\\s'\"]+$", "");
        String[] parts = str.split("[、,，\\s/]+");
        Set<String> unique = new LinkedHashSet<>();
        for (String part : parts) {
            String cleaned = cleanPersonnelName(part);
            if (!cleaned.isEmpty() && !isPlaceholderName(cleaned)) {
                unique.add(cleaned);
            }
        }
        result.addAll(unique);
        return result;
    }

    /**
     * 人员数组格式化：将任意输入格式化为标准干净的展示串 "周伟、王敏"
     *
     * @param names 人员串
     * @return 展示串(无人时返回 待分配)
     */
    public static String formatPersonnelNames(String names) {
        List<String> list = parsePersonnelNames(names);
        if (list.isEmpty()) {
            return UNASSIGNED;
        }
        return String.join(SEPARATOR, list);
    }

    /**
     * 从名单中剔除指定人员(同时剔除待分配占位)
     *
     * @param names 人员串
     * @param person 要剔除的人员
     * @return 剔除后的名单串(可为空串)
     */
    public static String removePerson(String names, String person) {
        List<String> list = parsePersonnelNames(names);
        list.removeIf(p -> p.equals(person));
        return String.join(SEPARATOR, list);
    }
}
