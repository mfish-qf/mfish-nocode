package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.SysConfig;
import cn.com.mfish.sys.mapper.SysConfigMapper;
import cn.com.mfish.sys.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @description: 界面配置
 * @author: mfish
 * @date: 2023-03-07
 * @version: V2.3.1
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    /**
     * 查询用户所有系统配置
     *
     * @param userId 用户ID
     * @return 系统配置列表
     */
    @Override
    public List<SysConfig> querySysConfig(String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new MyRuntimeException("错误：用户ID或类型不允许为空");
        }
        return baseMapper.selectList(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getUserId, userId));
    }

    /**
     * 根据用户ID和配置类型查询系统配置
     *
     * @param userId 用户ID
     * @param type   配置类型
     * @return 系统配置对象
     */
    @Override
    public SysConfig querySysConfig(String userId, Integer type) {
        if (StringUtils.isEmpty(userId) || null == type) {
            throw new MyRuntimeException("错误：用户ID或类型不允许为空");
        }
        return baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getUserId, userId)
                .eq(SysConfig::getType, type));
    }

    /**
     * 保存系统配置（已存在则更新，否则新增）
     *
     * @param sysConfig 系统配置对象
     * @return 保存结果
     */
    @Override
    public Result<SysConfig> saveSysConfig(SysConfig sysConfig) {
        String userId = AuthInfoUtils.getCurrentUserId();
        SysConfig config = querySysConfig(userId, sysConfig.getType());
        if (config != null) {
            config.setConfig(sysConfig.getConfig());
            if (baseMapper.updateById(config) == 1) {
                return Result.ok(sysConfig, "界面配置-编辑成功!");
            }
            return Result.fail(sysConfig, "错误:界面配置-编辑失败!");
        }
        sysConfig.setUserId(userId);
        if (baseMapper.insert(sysConfig) == 1) {
            return Result.ok(sysConfig, "界面配置-新增成功!");
        }
        return Result.fail(sysConfig, "错误:界面配置-新增失败!");
    }
}
