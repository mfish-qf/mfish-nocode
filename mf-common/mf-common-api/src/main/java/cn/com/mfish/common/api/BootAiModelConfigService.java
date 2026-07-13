package cn.com.mfish.common.api;

import cn.com.mfish.ai.api.entity.AiModelConfig;
import cn.com.mfish.ai.api.remote.RemoteAiModelConfigService;
import cn.com.mfish.ai.api.req.ReqAiModelConfig;
import cn.com.mfish.ai.api.vo.AiModelConfigVo;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.ai.service.AiModelConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: ai模型配置服务
 * @author: mfish
 * @date: 2026/07/03
 */
@Service("remoteAiModelConfigService")
public class BootAiModelConfigService implements RemoteAiModelConfigService {

    @Resource
    private AiModelConfigService aiModelConfigService;


    @Override
    public Result<PageResult<AiModelConfigVo>> queryPageList(String origin, ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) {
        return aiModelConfigService.queryPageList(reqAiModelConfig, reqPage);
    }

    @Override
    public Result<List<AiModelConfig>> queryList(String origin, ReqAiModelConfig reqAiModelConfig) {
        return Result.ok(aiModelConfigService.queryList(reqAiModelConfig), "查询模型配置列表成功");
    }
}
