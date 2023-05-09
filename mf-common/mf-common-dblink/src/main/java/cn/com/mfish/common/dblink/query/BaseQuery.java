package cn.com.mfish.common.dblink.query;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.utils.DataUtils;
import cn.com.mfish.common.dataset.datatable.MetaDataHeader;
import cn.com.mfish.common.dataset.datatable.MetaDataHeaders;
import cn.com.mfish.common.dataset.datatable.MetaDataRow;
import cn.com.mfish.common.dataset.datatable.MetaDataTable;
import cn.com.mfish.common.dataset.enums.TargetType;
import cn.com.mfish.common.dblink.common.Constant;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.manger.PoolManager;
import cn.com.mfish.common.dblink.page.BoundSql;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * @description: 数据库查询基础类
 * @author: mfish
 * @date: 2023/3/20
 */
@Slf4j
public class BaseQuery {
    private final DataSourceOptions options;
    private static final long TIMEOUT = 15000;

    public BaseQuery(final DataSourceOptions options) {
        this.options = options;
    }

    /**
     * 查询数据
     *
     * @param boundSql sql包装类
     * @return
     */
    public MetaDataTable query(BoundSql boundSql) {
        return query(boundSql, rs -> {
            try {
                return getMetaDataTable(rs, getColHeaders(rs.getMetaData()));
            } catch (SQLException e) {
                log.error(String.format("获取metaData异常:%s，Msg:%s", boundSql.getSql(), e));
                throw new MyRuntimeException(e);
            }
        });
    }

    /**
     * 查询泛型结果
     *
     * @param boundSql
     * @param <T>
     * @return
     */
    public <T> Page<T> query(BoundSql boundSql, Class<T> cls) {
        return query(boundSql, rs -> {
            try {
                return changeT(rs, cls);
            } catch (Exception e) {
                log.error(String.format("获取metaData异常:%s，Msg:%s", boundSql.getSql(), e));
                throw new MyRuntimeException(e);
            }
        });
    }

    /**
     * 结果转换成对象
     *
     * @param rs  结果
     * @param cls 类型
     * @param <T> 泛型
     * @return
     * @throws SQLException           sql异常
     * @throws InstantiationException 反射异常
     * @throws IllegalAccessException 反射异常
     */
    private <T> Page<T> changeT(final ResultSet rs, Class<T> cls) throws SQLException, InstantiationException, IllegalAccessException {
        final ResultSetMetaData metaData = rs.getMetaData();
        List<Field> fields = Utils.getAllFields(cls);
        Page<T> list = new Page<>();
        while (rs.next()) {
            T t = cls.newInstance();
            for (Field field : fields) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = StringUtils.toCamelCase(metaData.getColumnLabel(i));
                    if (!field.getName().equals(columnName)) {
                        continue;
                    }
                    String type = rs.getMetaData().getColumnTypeName(i);
                    Object columnValue = formatValue(type, rs.getObject(i));
                    field.setAccessible(true);
                    //数据库中是数字类型，字段类型为boolean时强制转换为boolean
                    if (field.getType().getTypeName().equals("java.lang.Boolean") && columnValue instanceof Number) {
                        columnValue = DataUtils.numCompare(columnValue, 0) > 0 ? true : false;
                    }
                    field.set(t, columnValue);
                    break;
                }
            }
            list.add(t);
        }
        return list;
    }

    /**
     * 基础查询方法
     *
     * @param boundSql sql包装类
     * @param function 结果处理函数
     * @param <R>      返回结果类型
     * @return
     */
    protected <R> R query(BoundSql boundSql, Function<ResultSet, R> function) {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            Connection conn = getConnect();
            stmt = conn.prepareStatement(boundSql.getSql());
            if (boundSql.getParams() != null && !boundSql.getParams().isEmpty()) {
                for (int i = 0; i < boundSql.getParams().size(); i++) {
                    stmt.setObject(i + 1, boundSql.getParams().get(i));
                }
            }
            log.info("执行查询:" + boundSql.getSql());
            long start = System.currentTimeMillis();
            rs = stmt.executeQuery();
            long end = System.currentTimeMillis();
            log.info("查询SQL耗时:" + (end - start));
            return function.apply(rs);
        } catch (SQLException ex) {
            log.error(String.format("执行SQL:%s，Msg:%s", boundSql.getSql(), ex));
            throw new MyRuntimeException(ex);
        } finally {
            PoolManager.release();
            release(stmt, rs);
        }
    }

    /**
     * 获取列头
     *
     * @param boundSql
     * @return
     */
    public MetaDataHeaders getColHeaders(BoundSql boundSql) {
        return query(boundSql, (rs) -> {
            try {
                return getColHeaders(rs.getMetaData());
            } catch (SQLException e) {
                log.error(String.format("获取metaData异常:%s，Msg:%s", boundSql.getSql(), e));
                throw new MyRuntimeException(e);
            }
        });
    }

    /**
     * 获取元数据表头
     *
     * @param rsMataData 数据库元数据
     * @return
     */
    private MetaDataHeaders getColHeaders(final ResultSetMetaData rsMataData) {
        try {
            MetaDataHeaders headers = new MetaDataHeaders();
            for (int i = 1; i <= rsMataData.getColumnCount(); i++) {
                String colName = rsMataData.getColumnLabel(i);
                if (Constant.ORACLE_ROW.equals(colName)) {
                    continue;
                }
                final MetaDataHeader header = new MetaDataHeader();
                //****默认别名与查询名称相同 后期修改别名后别名必须唯一
                header.setColName(colName);
                header.setFieldName(colName);
                //存在类似INT UNSIGNED类型的结果进行特殊处理
                String dataType = rsMataData.getColumnTypeName(i).toUpperCase();
                int index = dataType.indexOf(" ");
                if (index > 0) {
                    dataType = dataType.substring(0, index);
                }
                header.setDataType(dataType);
                header.setTargetType(TargetType.ORIGINAL);
                headers.addColumn(header);
            }
            return headers;
        } catch (SQLException ex) {
            log.error("获取列失败" + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 获取元数据表格
     *
     * @param rs      结果
     * @param headers 表头
     * @return
     * @throws SQLException
     */
    protected MetaDataTable getMetaDataTable(final ResultSet rs, final MetaDataHeaders headers) throws SQLException {
        final MetaDataTable table = new MetaDataTable(headers);
        long start = System.currentTimeMillis();
        while (rs.next()) {
            final MetaDataRow row = new MetaDataRow(table.getColHeaders());
            Iterator<Map.Entry<String, MetaDataHeader>> iterator = headers.entrySet().iterator();
            int i = 1;
            while (iterator.hasNext()) {
                String type = rs.getMetaData().getColumnTypeName(i).toUpperCase(Locale.ROOT);
                Object value = formatValue(type, rs.getObject(i));
                if (value instanceof java.util.Date) {
                    //MetaDataTable返回类型日期强制格式为 yyyy-MM-dd HH:mm:ss
                    if (type.equals("DATE")) {
                        value = new SimpleDateFormat("yyyy-MM-dd").format(value);
                    } else {
                        value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
                    }
                }
                row.setCellValue(iterator.next().getValue().getColName(), value);
                i++;
            }
            table.add(row);
        }
        long end = System.currentTimeMillis();
        log.info("组合table耗时:" + (end - start));
        return table;
    }

    /**
     * 格式化查询到的值(不完善 后面根据使用情况补充)
     *
     * @param columnTypeName 列类型名称
     * @param value          查询值
     * @return
     */
    public Object formatValue(String columnTypeName, Object value) {
        if (value == null) {
            return null;
        }
        columnTypeName = columnTypeName.toUpperCase(Locale.ROOT);
        if (columnTypeName.contains("BINARY") || columnTypeName.contains("BLOB")) {
            value = new String((byte[]) value);
        } else if (columnTypeName.contains("GEOMETRY")) {
            value = value.toString();
        } else if (columnTypeName.contains("BIT")) {
            value = value instanceof Boolean && (Boolean) value ? 1 : 0;
        } else if (columnTypeName.contains("DATETIME")) {
            //LocalDateTime 转成Date类型
            value = value instanceof LocalDateTime ? Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant()) : value;
        } else if (columnTypeName.contains("TIMESTAMP") && value != null) {
            String time = value.toString();
            if (!StringUtils.isEmpty(time)) {
                try {
                    value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time.substring(0, time.length() - 2));
                } catch (ParseException e) {
                    log.error("错误:日期转换异常");
                }
            }
        }
        return value;
    }

    /**
     * 获取连接
     *
     * @return
     * @throws SQLException sql异常
     */
    public Connection getConnect() throws SQLException {
        return PoolManager.getConnection(options, TIMEOUT);
    }

    /**
     * 查询资源释放
     *
     * @param stmt Statement
     * @param rs   ResultSet
     */
    private void release(final Statement stmt, final ResultSet rs) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (final SQLException ex) {
            log.error("数据库资源释放异常", ex);
            throw new RuntimeException("数据库资源释放异常", ex);
        }
    }
}
