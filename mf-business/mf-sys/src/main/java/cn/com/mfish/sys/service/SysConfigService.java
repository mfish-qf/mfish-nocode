package cn.com.mfish.sys.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.SysConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description: 界面配置
 * @author: mfish
 * @date: 2023-03-07
 * @version: V2.3.1
 */
public interface SysConfigService extends IService<SysConfig> {
    /**
     * 查询用户系统配置列表
     *
     * @param userId 用户 ID
     * @return 系统配置列表
     */
    List<SysConfig> querySysConfig(String userId);

    /**
     * 根据用户 ID 和类型查询系统配置
     *
     * @param userId 用户 ID
     * @param type 配置类型
     * @return 系统配置对象
     */
    SysConfig querySysConfig(String userId, Integer type);

    /**
     * 保存系统配置
     *
     * @param sysConfig 系统配置对象
     * @return 操作结果
     */
    Result<SysConfig> saveSysConfig(SysConfig sysConfig);
}
