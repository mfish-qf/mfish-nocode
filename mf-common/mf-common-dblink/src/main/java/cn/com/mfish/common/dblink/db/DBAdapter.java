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

    private static Map<DBType, DBDialect> dialectMap = new HashMap<>();

    static {
        dialectMap.put(DBType.mysql, new MysqlDBDialect());
        dialectMap.put(DBType.postgre, new PostgreDBDialect());
        dialectMap.put(DBType.oracle, new OracleDBDialect());
    }

    /**
     * 获取当前数据库方言
     *
     * @param dbType
     * @return
     */
    public static DBDialect getDBDialect(DBType dbType) {
        if (dialectMap.containsKey(dbType)) {
            return dialectMap.get(dbType);
        }
        throw new MyRuntimeException("错误:未知数据库类型");
    }
}
