package cn.com.mfish.common.oauth.common;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.oauth.annotation.DataScope;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @description: 数据范围上下文
 * @author: mfish
 * @date: 2024/4/26
 */
public class DataScopeUtils {
    public static final ThreadLocal<List<DataScope>> context = new ThreadLocal<>();
    public static Set<String> FOLLOW_KEYWORD = new HashSet<>();
    public static Set<Character> START_KEYWORD = new HashSet<>();
    public static Set<Character> END_KEYWORD = new HashSet<>();

    @Data
    @Accessors(chain = true)
    public static class SqlSplit {
        @Schema(name = "语句类型")
        private boolean haveTable = false;
        @Schema(name = "语句")
        private String statement;
        @Schema(name = "是否存在别名")
        private boolean haveAlias = false;
    }

    static {
        START_KEYWORD.add('.');
        START_KEYWORD.add('(');
        START_KEYWORD.add(' ');
        START_KEYWORD.add('\n');
        START_KEYWORD.add('\r');
        START_KEYWORD.add('\t');
        //结束标示
        END_KEYWORD.add(' ');
        END_KEYWORD.add('\n');
        END_KEYWORD.add('\r');
        END_KEYWORD.add('\t');
        END_KEYWORD.add(')');
        //表名后允许的关键字
        FOLLOW_KEYWORD.add("as");
        FOLLOW_KEYWORD.add("where");
        FOLLOW_KEYWORD.add("inner");
        FOLLOW_KEYWORD.add("left");
        FOLLOW_KEYWORD.add("right");
        FOLLOW_KEYWORD.add("full");
        FOLLOW_KEYWORD.add("join");
        FOLLOW_KEYWORD.add("group");
        FOLLOW_KEYWORD.add("limit");
        FOLLOW_KEYWORD.add("order");
    }

    /**
     * 分解SQL
     *
     * @param sql      需要分解的SQL语句
     * @param tableName 数据库表名
     * @return 返回分解后的SQL列表
     */
    public static List<SqlSplit> splitSql(String sql, String tableName) {
        List<SqlSplit> list = new ArrayList<>();
        findTable(sql, tableName, list);
        return list;
    }

    /**
     * 找出SQL中指定表
     * #############并不严谨，适用与一般SQL。奇葩SQL请自行解决#############
     *
     * @param sql       原SQL
     * @param tableName 表名
     * @param list      列表
     */
    private static void findTable(String sql, String tableName, List<SqlSplit> list) {
        int index = sql.toLowerCase().indexOf(tableName.toLowerCase());
        if (index <= 0) {
            //未找到表名直接返回
            list.add(new SqlSplit().setStatement(sql).setHaveTable(false));
            return;
        }
        SqlSplit sqlSplit = new SqlSplit().setStatement(sql.substring(0, index + tableName.length()));
        boolean isTable = START_KEYWORD.contains(sql.toCharArray()[index - 1]);
        String leaveSql = sql.substring(index + tableName.length());
        //表名已结尾说明当前SQL存在表名，直接返回
        if (leaveSql.trim().isEmpty()) {
            list.add(sqlSplit.setHaveTable(isTable));
            return;
        }
        //不符合表开始字符或结束字符说明当前字符并非表名，继续判断后续SQL
        if (!isTable || !END_KEYWORD.contains(leaveSql.charAt(0))) {
            list.add(sqlSplit);
            findTable(leaveSql, tableName, list);
            return;
        }
        //表名后非括回结尾可能存在别名，进行别名判断
        if (leaveSql.trim().charAt(0) == ')') {
            list.add(sqlSplit.setHaveTable(isTable));
            findTable(leaveSql, tableName, list);
            return;
        }
        String keyword = followKeyword(leaveSql);
        int keywordIndex = leaveSql.indexOf(keyword) + keyword.length();
        //不在表后面跟随的关键字中，说明当前字符为别名
        if (!FOLLOW_KEYWORD.contains(keyword.toLowerCase())) {
            list.add(sqlSplit.setHaveTable(true).setHaveAlias(true));
        } else if (keyword.equalsIgnoreCase("as")) {
            //当前关键字为as，说明后面跟随字符为别名
            list.add(sqlSplit.setHaveTable(true).setHaveAlias(true));
            String nextKeyword = followKeyword(leaveSql.trim().substring(keyword.length()));
            keyword += " " + nextKeyword;
            keywordIndex = leaveSql.indexOf(nextKeyword) + nextKeyword.length();
        } else {
            //不是别名直接返回
            list.add(sqlSplit.setHaveTable(true));
            findTable(leaveSql, tableName, list);
            return;
        }
        list.add(new SqlSplit().setStatement(" " + keyword));
        leaveSql = leaveSql.substring(keywordIndex);
        findTable(leaveSql, tableName, list);
    }

    /**
     * 获取表后面的关键字
     *
     * @param sql 剩余SQL语句
     * @return 关键字
     */
    private static String followKeyword(String sql) {
        StringBuilder sb = new StringBuilder();
        for (char c : sql.trim().toCharArray()) {
            if (END_KEYWORD.contains(c)) {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 构建查询条件
     *
     * @param fieldName
     * @param values
     * @return
     */
    public static String buildCondition(String fieldName, String[] values) {
        if (StringUtils.isEmpty(fieldName)) {
            throw new MyRuntimeException("错误：未传入条件字段名称");
        }
        if (values == null || values.length == 0) {
            return "1<>1";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(fieldName);
        if (values.length > 1) {
            sb.append(" in ('");
            sb.append(String.join("','", values));
            sb.append("')");
        } else {
            sb.append(" = '");
            sb.append(values[0]);
            sb.append("'");
        }
        return sb.toString();
    }
}
