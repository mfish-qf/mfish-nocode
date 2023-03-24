package cn.com.mfish.code.common;

import cn.com.mfish.common.dblink.db.DBAdapter;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.sys.api.entity.DbConnect;

/**
 * @description: 代码生成通用类
 * @author: mfish
 * @date: 2023/3/23 21:54
 */
public class CodeUtils {
    /**
     * 数据库连接转配置信息
     *
     * @param dbConnect 数据库连接信息
     * @return
     */
    public static DataSourceOptions buildDataSourceOptions(DbConnect dbConnect) {
        DBType dbType = DBType.getType(dbConnect.getDbType());
        DataSourceOptions dataSourceOptions = new DataSourceOptions().setDbType(dbType)
                .setPoolType(PoolType.getPoolType(dbConnect.getPoolType()))
                .setUser(dbConnect.getUsername())
                .setPassword(dbConnect.getPassword())
                .setJdbcUrl(DBAdapter.getDBDialect(dbType).getJdbc(dbConnect.getHost(), dbConnect.getPort(), dbConnect.getDbName()));
        return dataSourceOptions;
    }
}
