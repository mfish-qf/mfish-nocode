package cn.com.mfish.ai.service.impl;

import cn.com.mfish.ai.api.entity.McpServerConfig;
import cn.com.mfish.ai.mapper.McpServerConfigMapper;
import cn.com.mfish.ai.service.McpServerConfigService;
import cn.com.mfish.common.ai.capability.McpServerInfo;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: MCP服务器配置信息实现
 * @author: mfish
 * @date: 2026-07-21
 * @version: V2.4.1
 */
@Slf4j
@Service
public class McpServerConfigServiceImpl extends ServiceImpl<McpServerConfigMapper, McpServerConfig>
        implements McpServerConfigService {

    @Override
    public Result<PageResult<McpServerConfig>> queryPageList(McpServerConfig req, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<McpServerConfig> wrapper = new LambdaQueryWrapper<>();
        if (req != null) {
            wrapper.like(StringUtils.isNotEmpty(req.getServerName()), McpServerConfig::getServerName, req.getServerName());
            wrapper.eq(StringUtils.isNotEmpty(req.getTransportType()), McpServerConfig::getTransportType, req.getTransportType());
            wrapper.eq(req.getStatus() != null, McpServerConfig::getStatus, req.getStatus());
        }
        wrapper.orderByDesc(McpServerConfig::getCreateTime);
        List<McpServerConfig> list = baseMapper.selectList(wrapper);
        return Result.ok(new PageResult<>(list), "查询成功");
    }

    @Override
    @Transactional
    public Result<McpServerConfig> insert(McpServerConfig entity) {
        if (StringUtils.isEmpty(entity.getId())) {
            entity.setId(Utils.uuid32());
        }
        entity.setCreateTime(new Date());
        baseMapper.insert(entity);
        return Result.ok(entity, "新增成功");
    }

    @Override
    @Transactional
    public Result<McpServerConfig> update(McpServerConfig entity) {
        entity.setUpdateTime(new Date());
        baseMapper.updateById(entity);
        return Result.ok(entity, "修改成功");
    }

    @Override
    @Transactional
    public Result<Boolean> delete(String id) {
        baseMapper.deleteById(id);
        return Result.ok(true, "删除成功");
    }

    /**
     * 实现 {@link McpServerConfigProvider}：查询状态为"正常"的 MCP 服务器配置，
     * 转换为 {@link McpServerInfo} 列表供 {@code McpCapabilityEngine} 使用
     */
    @Override
    public List<McpServerInfo> getActiveServerConfigs() {
        LambdaQueryWrapper<McpServerConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(McpServerConfig::getStatus, (short) 0);
        List<McpServerConfig> configs = baseMapper.selectList(wrapper);
        List<McpServerInfo> result = new ArrayList<>();
        for (McpServerConfig config : configs) {
            result.add(new McpServerInfo()
                    .setServerName(config.getServerName())
                    .setTransportType(config.getTransportType())
                    .setCommand(config.getCommand())
                    .setArgs(config.getArgs())
                    .setEnv(config.getEnv())
                    .setSseUrl(config.getSseUrl())
                    .setSseEndpoint(config.getSseEndpoint())
                    .setAuthToken(config.getAuthToken()));
        }
        return result;
    }
}
