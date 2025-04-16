package cn.com.mfish.common.log.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.SysLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 系统日志
 * @author: mfish
 * @date: 2023-01-08
 * @version: V2.0.0
 */
public interface SysLogService extends IService<SysLog> {
    Result<SysLog> insertLog(SysLog sysLog);
}
