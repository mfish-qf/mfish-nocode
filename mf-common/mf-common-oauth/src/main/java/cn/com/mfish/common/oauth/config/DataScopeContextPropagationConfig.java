package cn.com.mfish.common.oauth.config;

import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.common.DataScopeUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * @description: DataScope ThreadLocal 跨线程传播配置
 * <p>
 * WebFlux响应式环境下，subscribeOn(Schedulers.boundedElastic)会将阻塞调用切换到弹性线程池执行。
 * DataScopeUtils.context 是ThreadLocal，不会自动跟随线程切换，导致SelectInterceptor在执行线程读不到数据权限配置。
 * </p>
 * <p>
 * 本配置通过 Schedulers.onScheduleHook 拦截 Reactor 调度器的任务提交：
 * 1. 在提交线程（通常是Netty IO）捕获当前 DataScopeUtils.context 的快照
 * 2. 不清理提交线程的ThreadLocal（避免多级subscribeOn链中断链）
 * 3. 在目标线程执行任务前设置ThreadLocal（若快照非null），任务完成后在finally中清理
 * </p>
 * <p>
 * 相比早期"捕获后清理"方案的改进：
 * - 早期方案在提交线程清理ThreadLocal，导致多级subscribeOn链中第二级调度时捕获不到值（断链）
 * - 本方案只在目标线程的finally中清理，提交线程的ThreadLocal由DataScopeAspect的doFinally统一清理
 * - 目标线程的finally清理保证线程池复用时无残留
 * </p>
 * <p>
 * 并发安全性：
 * - 提交线程的ThreadLocal在doFinally前不会被其他请求修改（同一Netty线程串行处理）
 * - 目标线程的ThreadLocal由finally保证清理，线程池复用安全
 * - 多级subscribeOn链中每级调度都会捕获当前线程的ThreadLocal快照，链路不断
 * </p>
 * <p>
 * 注意：Servlet环境不使用Reactor调度器，此hook不生效，不影响Servlet服务。
 * </p>
 * @author: mfish
 * @date: 2026/7/14
 */
@Slf4j
@Configuration
public class DataScopeContextPropagationConfig {
    private static final String SCHEDULE_HOOK_KEY = "mfish.datascope.propagation";

    @PostConstruct
    public void init() {
        Schedulers.onScheduleHook(SCHEDULE_HOOK_KEY, runnable -> {
            // 在提交线程捕获快照（不清理，避免断链）
            List<DataScope> captured = DataScopeUtils.context.get();
            if (captured == null) {
                return runnable;
            }
            // 在目标线程执行任务前设置ThreadLocal，任务完成后finally清理（防止线程池复用残留）
            return () -> {
                DataScopeUtils.context.set(captured);
                try {
                    runnable.run();
                } finally {
                    DataScopeUtils.context.remove();
                }
            };
        });
    }
}
