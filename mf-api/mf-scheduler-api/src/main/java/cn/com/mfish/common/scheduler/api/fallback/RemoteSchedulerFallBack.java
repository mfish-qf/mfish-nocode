package cn.com.mfish.common.scheduler.api.fallback;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.scheduler.api.remote.RemoteSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @description: 调度接口降级处理
 * @author: mfish
 * @date: 2023/3/1 21:40
 */
@Component
@Slf4j
public class RemoteSchedulerFallBack implements FallbackFactory<RemoteSchedulerService> {
    @Override
    public RemoteSchedulerService create(Throwable cause) {
        log.error("错误:调度接口调用异常", cause);
        return (origin, jobLog) -> Result.fail(false, "错误:回调状态失败" + cause.getMessage());
    }
}
