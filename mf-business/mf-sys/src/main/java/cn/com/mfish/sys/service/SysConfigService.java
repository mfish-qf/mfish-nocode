package cn.com.mfish.sys.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.SysConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 界面配置
 * @author: mfish
 * @date: 2023-03-07
 * @version: V1.3.0
 */
public interface SysConfigService extends IService<SysConfig> {
    SysConfig querySysConfig(String userId);

    Result<SysConfig> saveSysConfig(SysConfig sysConfig);
}
