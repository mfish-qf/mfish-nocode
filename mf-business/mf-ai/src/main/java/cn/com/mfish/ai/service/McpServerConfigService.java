package cn.com.mfish.ai.service;

import cn.com.mfish.ai.api.entity.McpServerConfig;
import cn.com.mfish.common.ai.capability.McpServerConfigProvider;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import com.baomidou.mybatisplus.spring.service.IService;

/**
 * @description: MCP服务器配置信息
 * @author: mfish
 * @date: 2026-07-21
 * @version: V2.4.1
 */
public interface McpServerConfigService extends IService<McpServerConfig>, McpServerConfigProvider {

    /**
     * 分页查询
     */
    Result<PageResult<McpServerConfig>> queryPageList(McpServerConfig req, ReqPage reqPage);

    /**
     * 新增
     */
    Result<McpServerConfig> insert(McpServerConfig entity);

    /**
     * 修改
     */
    Result<McpServerConfig> update(McpServerConfig entity);

    /**
     * 删除
     */
    Result<Boolean> delete(String id);
}
