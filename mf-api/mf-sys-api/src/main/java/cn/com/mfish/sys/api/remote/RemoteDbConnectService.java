package cn.com.mfish.sys.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.fallback.RemoteDbConnectFallback;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @description: 数据库连接服务
 * @author: mfish
 * @date: 2023/3/23 21:38
 */
@FeignClient(contextId = "remoteDbConnectService", value = ServiceConstants.SYS_SERVICE, fallbackFactory = RemoteDbConnectFallback.class)
public interface RemoteDbConnectService {
    @GetMapping("/dbConnect")
    Result<PageResult<DbConnect>> queryPageList(@SpringQueryMap ReqDbConnect reqDbConnect, @SpringQueryMap ReqPage reqPage);
    @GetMapping("/dbConnect/{id}")
    Result<DbConnect> queryById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id);
}
