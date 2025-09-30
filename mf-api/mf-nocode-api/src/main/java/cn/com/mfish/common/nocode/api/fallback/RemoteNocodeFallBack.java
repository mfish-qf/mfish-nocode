package cn.com.mfish.common.nocode.api.fallback;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.nocode.api.remote.RemoteNocodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @description: 低代码服务接口降级处理
 * @author: mfish
 * @date: 2025/09/26
 */
@Component
@Slf4j
public class RemoteNocodeFallBack implements FallbackFactory<RemoteNocodeService> {
    @Override
    public RemoteNocodeService create(Throwable cause) {
        log.error("错误: 低代码服务接口调用异常", cause);
        return new RemoteNocodeService() {

            @Override
            public Result<String> approved(String origin, String prefix, String id, WorkflowCompleteResult result) {
                return Result.fail("错误：资源审核通过回调接口异常");
            }

            @Override
            public Result<String> rejected(String origin, String prefix, String id, WorkflowCompleteResult result) {
                return Result.fail("错误：资源审核拒绝回调接口异常");
            }

            @Override
            public Result<String> canceled(String origin, String prefix, String id, WorkflowCompleteResult result) {
                return Result.fail("错误：资源审核取消回调接口异常");
            }
        };

    }
}
