package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.dblink.enums.DBType;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 代码构建适配不同数据库
 * @author: mfish
 * @date: 2023/3/23 19:58
 */
public class DBAdapter {
    private DBAdapter() {

    }

    private static final Map<DBType, DBDialect> dialectMap = new HashMap<>();

    static {
        dialectMap.put(DBType.mysql, new MysqlDBDialect());
        dialectMap.put(DBType.postgre, new PostgreDBDialect());
        dialectMap.put(DBType.oracle, new OracleDBDialect());
    }

    /**
     * 获取当前数据库方言
     * <p>
     * 根据传入的数据库类型，返回对应的数据库方言对象
     * 这是为了在操作不同数据库时，能够针对各自的特点进行查询或操作，
     * 不同的数据库可能有不同的SQL语法和特性
     *
     * @param dbType 数据库类型，枚举了常见的数据库系统
     * @return 返回对应的数据库方言对象，是具体数据库操作的基石
     * @throws MyRuntimeException 如果传入的数据库类型不受支持，则抛出运行时异常
     */
    public static DBDialect getDBDialect(DBType dbType) {
        if (dialectMap.containsKey(dbType)) {
            return dialectMap.get(dbType);
        }
        throw new MyRuntimeException("错误:未知数据库类型");
    }
}
