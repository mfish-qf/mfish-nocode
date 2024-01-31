package cn.com.mfish.common.dblink.service;

import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.dataset.datatable.MetaDataHeader;
import cn.com.mfish.common.dataset.datatable.MetaDataTable;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;

import java.util.List;

/**
 * @author: mfish
 * @description: 表信息
 * @date: 2022/8/31 22:59
 */
public interface TableService {
    /**
     * 获取表字段信息
     *
     * @param connectId 数据库连接ID
     * @param tableName 表名
     * @return
     */
    List<FieldInfo> getFieldList(String connectId, String tableName, ReqPage reqPage);

    /**
     * 获取表信息
     *
     * @param connectId 数据库连接ID
     * @param tableName 表名
     * @return
     */
    TableInfo getTableInfo(String connectId, String tableName, ReqPage reqPage);

    /**
     * 获取表列表
     *
     * @param connectId 数据库连接ID
     * @param tableName 表名
     * @return
     */
    List<TableInfo> getTableList(String connectId, String tableName, ReqPage reqPage);

    /**
     * 获取数据
     *
     * @param connectId 数据库连接ID
     * @param tableName 表名
     * @return
     */
    MetaDataTable getDataTable(String connectId, String tableName, ReqPage reqPage);

    /**
     * 获取字段
     *
     * @param connectId 数据库连接ID
     * @param tableName 表名
     * @param reqPage   分页
     * @return
     */
    List<MetaDataHeader> getDataHeaders(String connectId, String tableName, ReqPage reqPage);
}
