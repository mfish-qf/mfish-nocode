package cn.com.mfish.project.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.entity.PmUserColumnConfig;
import com.baomidou.mybatisplus.spring.service.IService;

/**
 * @description: 用户表格列配置(按登录人员保存)
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.2
 */
public interface PmUserColumnConfigService extends IService<PmUserColumnConfig> {

    /**
     * 获取当前登录用户的列配置JSON
     *
     * @return 列配置JSON(无配置时返回空字符串)
     */
    Result<String> getCurrentUserConfig();

    /**
     * 保存当前登录用户的列配置JSON(无记录则新增, 有记录则更新)
     *
     * @param configJson 列配置JSON
     * @return 保存后的列配置JSON
     */
    Result<String> updateCurrentUserConfig(String configJson);
}
