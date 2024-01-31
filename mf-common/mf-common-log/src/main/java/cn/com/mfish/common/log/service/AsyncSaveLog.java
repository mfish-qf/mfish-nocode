package cn.com.mfish.common.log.service;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.SysLog;
import cn.com.mfish.sys.api.remote.RemoteLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: mfish
 * @description: 异步保存日志
 * @date: 2022/9/7 15:24
 */
@Service
@EnableAsync
@Slf4j
public class AsyncSaveLog {
    @Resource
    RemoteLogService remoteLogService;
    @Resource
    SysLogService sysLogService;

    @Async
    public void saveLog(SysLog sysLog) {
        //单实例时直接入库 微服务时使用远程日志接口
        Result<SysLog> result;
        if (ServiceConstants.isBoot(Utils.getServiceType())) {
            if (!sysLogService.save(sysLog)) {
                log.error("错误:保存日志出错");
            }
            return;
        }
        result = remoteLogService.addLog(RPCConstants.INNER, sysLog);
        if (result.isSuccess()) {
            return;
        }
        log.error("错误:保存日志出错，错误信息:" + result.getMsg());

    }
}
