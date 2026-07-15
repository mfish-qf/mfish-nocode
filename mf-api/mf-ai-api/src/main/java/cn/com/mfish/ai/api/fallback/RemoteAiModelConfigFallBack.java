package cn.com.mfish.ai.api.fallback;

import cn.com.mfish.ai.api.entity.AiModelConfig;
import cn.com.mfish.ai.api.remote.RemoteAiModelConfigService;
import cn.com.mfish.ai.api.req.ReqAiModelConfig;
import cn.com.mfish.ai.api.vo.AiModelConfigVo;
import cn.com.mfish.common.core.utils.FeignFallbackHelper;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: ai模型配置服务降级处理
 * @author: mfish
 * @date: 2026/07/03
 */
@Component
@Slf4j
public class RemoteAiModelConfigFallBack implements FallbackFactory<RemoteAiModelConfigService> {
    /**
     * 创建ai模型配置服务降级实例
     *
     * @param cause 导致降级的异常原因
     * @return 降级后的请假申请审批服务实例
     */
    @Override
    public RemoteAiModelConfigService create(Throwable cause) {
        log.error("错误: demo请假申请审批回调接口调用异常", cause);
        return new RemoteAiModelConfigService() {
            @Override
            public Result<PageResult<AiModelConfigVo>> queryPageList(String origin, ReqAiModelConfig reqAiModelConfig, ReqPage reqPage) {
                return Result.fail(FeignFallbackHelper.resolveErrorMsg(cause, "错误:查询分页列表失败"));
            }

            @Override
            public Result<List<AiModelConfig>> queryList(String origin, ReqAiModelConfig reqAiModelConfig) {
                return Result.fail(FeignFallbackHelper.resolveErrorMsg(cause, "错误:查询列表失败"));
            }
        };
    }
}
