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
    @GetMapping("/dbConnect")
    Result<PageResult<DbConnect>> queryPageList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqDbConnect reqDbConnect, @SpringQueryMap ReqPage reqPage);

    @GetMapping("/dbConnect/{id}")
    Result<DbConnect> queryById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id);

    @GetMapping("/dbConnect/tables")
    Result<PageResult<TableInfo>> getTableList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, @SpringQueryMap ReqPage reqPage);

    @GetMapping("/dbConnect/fields")
    Result<PageResult<FieldInfo>> getFieldList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, @SpringQueryMap ReqPage reqPage);

    @GetMapping("/dbConnect/data")
    Result<MetaHeaderDataTable> getDataTable(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, @SpringQueryMap ReqPage reqPage);

    @GetMapping("/dbConnect/headers")
    Result<List<MetaDataHeader>> getDataHeaders(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestParam(name = "connectId") String connectId, @RequestParam(name = "tableName", required = false) String tableName, @SpringQueryMap ReqPage reqPage);
}
