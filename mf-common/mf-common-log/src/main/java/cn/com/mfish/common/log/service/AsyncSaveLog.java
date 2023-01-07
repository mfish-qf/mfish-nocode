package cn.com.mfish.common.log.service;

import cn.com.mfish.common.core.constants.CredentialConstants;
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

    @Async
    public void saveLog(SysLog sysLog) {
        Result result = remoteLogService.addLog(CredentialConstants.INNER, sysLog);
        if (result.isSuccess()) {
            return;
        }
        log.error("错误:保存日志出错", result.getMsg());
    }
}
