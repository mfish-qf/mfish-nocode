package cn.com.mfish.common.scheduler.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.scheduler.api.entity.JobLog;
import cn.com.mfish.common.scheduler.api.fallback.RemoteSchedulerFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @description: RPC调度接口服务
 * @author: mfish
 * @date: 2023/3/1 18:21
 */
@FeignClient(contextId = "remoteSchedulerService", value = ServiceConstants.SCHEDULER_SERVICE, fallbackFactory = RemoteSchedulerFallBack.class)
public interface RemoteSchedulerService {

    /**
     * 回调更新任务执行状态
     *
     * @param origin 来源
     * @param jobLog 任务日志对象
     * @return 更新结果
     */
    @PutMapping("/jobLog/callBackStatus")
    Result<Boolean> callBackStatus(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @RequestBody JobLog jobLog);
}
