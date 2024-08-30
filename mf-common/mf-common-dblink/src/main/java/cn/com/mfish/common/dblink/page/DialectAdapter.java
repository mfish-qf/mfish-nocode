package cn.com.mfish.common.dblink.page;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.page.dialect.*;
import cn.com.mfish.common.dblink.query.BaseQuery;
import com.github.pagehelper.PageException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 方言适配
 * @author: mfish
 * @date: 2023/3/21 23:03
 */
public class DialectAdapter {
    private static final Map<DBType, Class<? extends Dialect>> dialectAliasMap = new HashMap<>();

    public static void registerDialectAlias(DBType dbType, Class<? extends Dialect> property) {
        dialectAliasMap.put(dbType, property);
    }

    static {
        registerDialectAlias(DBType.mysql, MysqlDialect.class);
        registerDialectAlias(DBType.oracle, OracleDialect.class);
        registerDialectAlias(DBType.postgre, PostgreDialect.class);
    }

    private final ThreadLocal<AbstractDialect> dialectThreadLocal = new ThreadLocal<>();

    /**
     * 初始化代理
     *
     * @param dataSourceOptions 数据源配置选项
     */
    public void initDelegateDialect(DataSourceOptions<?> dataSourceOptions) {
        dialectThreadLocal.set(initDialect(dataSourceOptions));
    }

    //获取当前的代理对象
    public AbstractDialect getDelegate() {
        return dialectThreadLocal.get();
    }

    /**
     * 根据数据库类型解析方言类
     * <p>
     * 本方法通过查找方言别名映射来确定与指定数据库类型对应的方言类。如果找到了对应的类，则返回该类；
     * 如果没有找到，则返回null。
     *
     * @param dbType 数据库类型，用于确定所需的方言类
     * @return 返回找到的方言类，如果没有找到，则返回null
     */
    private Class<? extends Dialect> resolveDialectClass(DBType dbType) {
        if (dialectAliasMap.containsKey(dbType)) {
            return dialectAliasMap.get(dbType);
        }
        return null;
    }


    /**
     * 初始化 helper
     *
     * @param dataSourceOptions 数据源配置
     */
    @SuppressWarnings("all")
    private AbstractDialect initDialect(DataSourceOptions<?> dataSourceOptions) {
        AbstractDialect dialect;
        try {
            Class<? extends Dialect> property = resolveDialectClass(dataSourceOptions.getDbType());
            if (property == null) {
                throw new MyRuntimeException("错误的查询类型");
            }
            if (AbstractDialect.class.isAssignableFrom(property)) {
                Constructor<?> sCon = property.getDeclaredConstructor(BaseQuery.class);
                Constructor<?> qCon = BaseQuery.class.getDeclaredConstructor(dataSourceOptions.getClass());
                dialect = (AbstractDialect) sCon.newInstance(qCon.newInstance(dataSourceOptions));
            } else {
                throw new PageException("使用 PageHelper 时，方言必须是实现 " + AbstractDialect.class.getCanonicalName() + " 接口的实现类!");
            }
        } catch (Exception e) {
            throw new PageException("初始化 helper [" + dataSourceOptions.getDbType() + "]时出错:" + e.getMessage(), e);
        }
        return dialect;
    }

    /**
     * 移除代理对象
     */
    public void clearDelegate() {
        dialectThreadLocal.remove();
    }
}
