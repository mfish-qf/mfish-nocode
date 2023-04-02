package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dblink.db.DBAdapter;
import cn.com.mfish.common.dblink.db.DBDialect;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.query.QueryHandler;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.remote.RemoteDbConnectService;
import cn.com.mfish.sys.service.TableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author: mfish
 * @description: 表信息
 * @date: 2022/8/31 23:05
 */
@Service
public class TableServiceImpl implements TableService {
    @Resource
    RemoteDbConnectService remoteDbConnectService;

    @Override
    public List<FieldInfo> getFieldList(String connectId, String tableName) {
        return getDbConnect(connectId, FieldInfo.class, (build, dbName) -> build.getColumns(dbName, tableName));
    }

    @Override
    public TableInfo getTableInfo(String connectId, String tableName) {
        List<TableInfo> list = getTableList(connectId, tableName);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<TableInfo> getTableList(String connectId, String tableName) {
        return getDbConnect(connectId, TableInfo.class, (build, dbName) -> build.getTableInfo(dbName, tableName));
    }

    private <T> List<T> getDbConnect(String schemaId, Class<T> cls, BiFunction<DBDialect, String, BoundSql> function) {
        Result<DbConnect> result = remoteDbConnectService.queryById(RPCConstants.INNER, schemaId);
        if (!result.isSuccess()) {
            throw new MyRuntimeException("错误:获取数据库连接出错");
        }
        DbConnect dbConnect = result.getData();
        DataSourceOptions dataSourceOptions = buildDataSourceOptions(dbConnect);
        //代码生成不使用连接池，强制设置为无池状态
        dataSourceOptions.setPoolType(PoolType.NoPool);
        DBDialect build = DBAdapter.getDBDialect(dataSourceOptions.getDbType());
        return QueryHandler.queryT(dataSourceOptions, function.apply(build, dbConnect.getDbName()), cls);
    }

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
