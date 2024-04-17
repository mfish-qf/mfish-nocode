package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dataset.datatable.MetaDataHeader;
import cn.com.mfish.common.dataset.datatable.MetaDataTable;
import cn.com.mfish.common.dataset.datatable.MetaHeaderDataTable;
import cn.com.mfish.common.dataset.enums.TargetType;
import cn.com.mfish.common.dblink.db.DBAdapter;
import cn.com.mfish.common.dblink.db.DBDialect;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.page.MfPageHelper;
import cn.com.mfish.common.dblink.query.QueryHandler;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;
import cn.com.mfish.common.dblink.service.DbConnectService;
import cn.com.mfish.common.dblink.service.TableService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author: mfish
 * @description: 表信息
 * @date: 2022/8/31 23:05
 */
@Service
@RefreshScope
public class TableServiceImpl implements TableService {
    @Resource
    DbConnectService dbConnectService;
    @Value("${DBConnect.password.privateKey}")
    private String privateKey;

    @Override
    public List<FieldInfo> getFieldList(String connectId, String tableName, ReqPage reqPage) {
        return queryT(connectId, FieldInfo.class, (build, dbName) -> build.getColumns(dbName, tableName), reqPage);
    }

    @Override
    public TableInfo getTableInfo(String connectId, String tableName, ReqPage reqPage) {
        List<TableInfo> list = getTableList(connectId, tableName, reqPage);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<TableInfo> getTableList(String connectId, String tableName, ReqPage reqPage) {
        return queryT(connectId, TableInfo.class, (build, dbName) -> build.getTableInfo(dbName, tableName), reqPage);
    }

    @Override
    public Result<MetaHeaderDataTable> getHeaderDataTable(String connectId, String tableName, ReqPage reqPage) {
        MetaDataTable table = getDataTable(connectId, tableName, reqPage);
        return Result.ok(new MetaHeaderDataTable(table), "获取表数据成功");
    }

    @Override
    public MetaDataTable getDataTable(String connectId, String tableName, ReqPage reqPage) {
        return query(connectId, "select * from " + tableName, reqPage);
    }

    private DataSourceOptions<?> buildDBQuery(String connectId) {
        Result<DbConnect> result = dbConnectService.queryById(connectId);
        if (!result.isSuccess()) {
            throw new MyRuntimeException("错误:获取数据库连接出错");
        }
        DbConnect dbConnect = result.getData();
        return QueryHandler.buildDataSourceOptions(dbConnect, privateKey);
    }

    /**
     * 查询数据
     *
     * @param connectId 连接ID
     * @param sql       sql语句
     * @return
     */
    private MetaDataTable query(String connectId, String sql, ReqPage reqPage) {
        DataSourceOptions<?> dataSourceOptions = buildDBQuery(connectId);
        if (reqPage != null) {
            MfPageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        }
        return QueryHandler.query(dataSourceOptions, sql);
    }

    private <T> List<T> queryT(String connectId, Class<T> cls, BiFunction<DBDialect, String, BoundSql> function, ReqPage reqPage) {
        DataSourceOptions<?> dataSourceOptions = buildDBQuery(connectId);
        DBDialect dialect = DBAdapter.getDBDialect(dataSourceOptions.getDbType());
        if (reqPage != null) {
            MfPageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        }
        return QueryHandler.queryT(dataSourceOptions, function.apply(dialect, dataSourceOptions.getDbName()), cls);
    }

    /**
     * 获取数据集列头
     *
     * @param connectId 连接ID
     * @param tableName 表名
     * @return
     */
    @Override
    public List<MetaDataHeader> getDataHeaders(String connectId, String tableName, ReqPage reqPage) {
        List<FieldInfo> list = getFieldList(connectId, tableName, reqPage);
        List<MetaDataHeader> headers = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return headers;
        }
        for (FieldInfo fieldInfo : list) {
            headers.add(new MetaDataHeader()
                    .setFieldName(fieldInfo.getFieldName())
                    .setColName(fieldInfo.getFieldName())
                    .setDataType(fieldInfo.getType())
                    .setTargetType(TargetType.ORIGINAL)
                    .setTableAlias(tableName)
                    .setComment(fieldInfo.getComment()));
        }
        return headers;
    }

}
