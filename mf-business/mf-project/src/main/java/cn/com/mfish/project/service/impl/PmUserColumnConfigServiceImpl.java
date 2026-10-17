package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.entity.PmUserColumnConfig;
import cn.com.mfish.project.mapper.PmUserColumnConfigMapper;
import cn.com.mfish.project.service.PmUserColumnConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description: 用户表格列配置服务实现(按登录人员保存)
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.2
 */
@Slf4j
@Service
public class PmUserColumnConfigServiceImpl extends ServiceImpl<PmUserColumnConfigMapper, PmUserColumnConfig>
        implements PmUserColumnConfigService {

    @Resource
    private PmUserColumnConfigMapper userColumnConfigMapper;

    @Override
    public Result<String> getCurrentUserConfig() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail("错误:无法识别当前登录用户, 请重新登录!");
        }
        PmUserColumnConfig config = userColumnConfigMapper.selectOne(
                new LambdaQueryWrapper<PmUserColumnConfig>()
                        .eq(PmUserColumnConfig::getUserId, userId)
                        .last("limit 1"));
        if (config == null || StringUtils.isEmpty(config.getConfigJson())) {
            return Result.ok("", "当前用户暂无列配置!");
        }
        return Result.ok(config.getConfigJson(), "获取列配置成功!");
    }

    @Override
    public Result<String> updateCurrentUserConfig(String configJson) {
        if (StringUtils.isEmpty(configJson)) {
            return Result.fail("错误:列配置内容不能为空!");
        }
        String userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail("错误:无法识别当前登录用户, 请重新登录!");
        }
        Date now = new Date();
        PmUserColumnConfig exist = userColumnConfigMapper.selectOne(
                new LambdaQueryWrapper<PmUserColumnConfig>()
                        .eq(PmUserColumnConfig::getUserId, userId)
                        .last("limit 1"));
        if (exist == null) {
            PmUserColumnConfig record = new PmUserColumnConfig();
            record.setUserId(userId);
            record.setConfigJson(configJson);
            record.setCreateBy(userId);
            record.setCreateTime(now);
            record.setUpdateBy(userId);
            record.setUpdateTime(now);
            userColumnConfigMapper.insert(record);
        } else {
            exist.setConfigJson(configJson);
            exist.setUpdateBy(userId);
            exist.setUpdateTime(now);
            userColumnConfigMapper.updateById(exist);
        }
        return Result.ok(configJson, "列配置保存成功!");
    }

    /**
     * 安全获取当前登录用户标识(优先用户ID, 无请求上下文时回退账号)
     *
     * @return 用户标识, 无法获取时返回 null
     */
    private String getCurrentUserId() {
        try {
            String userId = AuthInfoUtils.getCurrentUserId();
            return StringUtils.isEmpty(userId) ? AuthInfoUtils.getCurrentAccount() : userId;
        } catch (Exception e) {
            log.warn("获取当前登录用户失败: {}", e.getMessage());
            return null;
        }
    }
}
