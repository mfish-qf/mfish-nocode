package cn.com.mfish.ai.api.remote;

import cn.com.mfish.ai.api.entity.AiModelConfig;
import cn.com.mfish.ai.api.fallback.RemoteAiModelConfigFallBack;
import cn.com.mfish.ai.api.req.ReqAiModelConfig;
import cn.com.mfish.ai.api.vo.AiModelConfigVo;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @description: ai模型配置服务
 * @author: mfish
 * @date: 2026/07/03
 */
@FeignClient(contextId = "remoteAiModelConfigService", value = ServiceConstants.AI_SERVICE, fallbackFactory = RemoteAiModelConfigFallBack.class)
public interface RemoteAiModelConfigService {
    @GetMapping("/aiModelConfig")
    Result<PageResult<AiModelConfigVo>> queryPageList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqAiModelConfig reqAiModelConfig, @SpringQueryMap ReqPage reqPage);

    @GetMapping("/aiModelConfig/list")
    Result<List<AiModelConfig>> queryList(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @SpringQueryMap ReqAiModelConfig reqAiModelConfig);
}
