package cn.com.mfish.code.service.impl;

import cn.com.mfish.code.common.CodeUtils;
import cn.com.mfish.code.dialect.CodeBuild;
import cn.com.mfish.code.dialect.CodeBuildAdapter;
import cn.com.mfish.code.entity.FieldInfo;
import cn.com.mfish.code.entity.TableInfo;
import cn.com.mfish.code.service.TableService;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.query.QueryHandler;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.remote.RemoteDbConnectService;
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
    public List<FieldInfo> getColumns(String connectId, String tableName) {
        List<FieldInfo> list = getDbConnect(connectId, FieldInfo.class, (build, dbName) -> build.getColumns(dbName, tableName));
        return list;
    }

    @Override
    public TableInfo getTableInfo(String connectId, String tableName) {
        List<TableInfo> list = getDbConnect(connectId, TableInfo.class, (build, dbName) -> build.getTableInfo(dbName, tableName));
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private <T> List<T> getDbConnect(String schemaId, Class<T> cls, BiFunction<CodeBuild, String, BoundSql> function) {
        Result<DbConnect> result = remoteDbConnectService.queryById(RPCConstants.INNER, schemaId);
        if (!result.isSuccess()) {
            throw new MyRuntimeException("错误:获取数据库连接出错");
        }
        DbConnect dbConnect = result.getData();
        DataSourceOptions dataSourceOptions = CodeUtils.buildDataSourceOptions(dbConnect);
        //代码生成不使用连接池，强制设置为无池状态
        dataSourceOptions.setPoolType(PoolType.NoPool);
        CodeBuild build = CodeBuildAdapter.getCodeBuild(dataSourceOptions.getDbType());
        return QueryHandler.queryT(dataSourceOptions, function.apply(build, dbConnect.getDbName()), cls);
    }
}
