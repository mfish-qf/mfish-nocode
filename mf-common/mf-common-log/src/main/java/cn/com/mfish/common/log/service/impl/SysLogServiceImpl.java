package cn.com.mfish.common.log.service.impl;

import cn.com.mfish.common.log.mapper.SysLogMapper;
import cn.com.mfish.common.log.service.SysLogService;
import cn.com.mfish.sys.api.entity.SysLog;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @description: 系统日志
 * @author: mfish
 * @date: 2023-01-08
 * @version: V1.2.0
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

}
