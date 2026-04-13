package cn.com.mfish.sys.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dataset.datatable.MetaDataHeader;
import cn.com.mfish.common.dataset.datatable.MetaHeaderDataTable;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;
import cn.com.mfish.sys.api.fallback.RemoteDbConnectFallback;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: 数据库连接服务
 * @author: mfish
 * @date: 2023/3/23 21:38
 */
@FeignClient(contextId = "remoteDbConnectService", value = ServiceConstants.SYS_SERVICE, fallbackFactory = RemoteDbConnectFallback.class)
public interface RemoteDbConnectService {
    /**
     * 分页查询数据库连接列表
     *
     * @param origin       来源
     * @param reqDbConnect 查询条件
     * @param reqPage      分页参数
     * @return 数据库连接分页列表
     */
    @GetMapping("/dbConnect")
    Result<PageResult<DbConnect>> queryPageList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqDbConnect reqDbConnect, @SpringQueryMap ReqPage reqPage);

    /**
     * 根据ID查询数据库连接
     *
     * @param origin 来源
     * @param id     连接ID
     * @return 数据库连接信息
     */
    @GetMapping("/dbConnect/{id}")
    Result<DbConnect> queryById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id);

    /**
     * 获取数据库表列表
     *
     * @param origin      来源
     * @param connectId   连接ID
     * @param tableSchema 数据库schema
     * @param tableName   表名（模糊查询）
     * @param reqPage     分页参数
     * @return 表信息分页列表
     */
    @GetMapping("/dbConnect/tables")
    Result<PageResult<TableInfo>> getTableList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableSchema", required = false) String tableSchema, @RequestParam(name = "tableName", required = false) String tableName, @SpringQueryMap ReqPage reqPage);

    /**
     * 获取表字段列表
     *
     * @param origin      来源
     * @param connectId   连接ID
     * @param tableSchema 数据库schema
     * @param tableName   表名
     * @param reqPage     分页参数
     * @return 字段信息分页列表
     */
    @GetMapping("/dbConnect/fields")
    Result<PageResult<FieldInfo>> getFieldList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableSchema", required = false) String tableSchema, @RequestParam(name = "tableName", required = false) String tableName, @SpringQueryMap ReqPage reqPage);

    /**
     * 获取表数据
     *
     * @param origin      来源
     * @param connectId   连接ID
     * @param tableSchema 数据库schema
     * @param tableName   表名
     * @param reqPage     分页参数
     * @return 表数据
     */
    @GetMapping("/dbConnect/data")
    Result<MetaHeaderDataTable> getDataTable(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableSchema", required = false) String tableSchema, @RequestParam(name = "tableName", required = false) String tableName, @SpringQueryMap ReqPage reqPage);

    /**
     * 获取表字段头信息
     *
     * @param origin      来源
     * @param connectId   连接ID
     * @param tableSchema 数据库schema
     * @param tableName   表名
     * @param reqPage     分页参数
     * @return 字段头信息列表
     */
    @GetMapping("/dbConnect/headers")
    Result<List<MetaDataHeader>> getDataHeaders(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableSchema", required = false) String tableSchema, @RequestParam(name = "tableName", required = false) String tableName, @SpringQueryMap ReqPage reqPage);
}
