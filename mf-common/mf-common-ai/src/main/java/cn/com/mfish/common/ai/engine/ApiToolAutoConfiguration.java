package cn.com.mfish.common.ai.engine;

import cn.com.mfish.common.ai.feign.FeignToolProvider;
import cn.com.mfish.common.ai.http.HttpToolProvider;
import cn.com.mfish.common.ai.provider.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * API工具自动配置
 * <p>
 * 注册 {@link ApiToolEngine} 作为顶层工具注册中心，自动发现所有 {@link ToolProvider} 实现，
 * 在所有单例 Bean 初始化完成后触发 {@link ApiToolEngine#initialize} 聚合工具。
 * </p>
 * <p>
 * 默认注册 {@link FeignToolProvider}（当 classpath 存在 FeignClient 时）。
 * 其他 Provider（如 HttpToolProvider）通过实现 ToolProvider 接口并声明为 Bean 即可自动接入。
 * </p>
 * <p>
 * 通过 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 注册。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
@AutoConfiguration
public class ApiToolAutoConfiguration {

    /**
     * 顶层工具引擎
     */
    @Bean
    public ApiToolEngine apiToolEngine() {
        return new ApiToolEngine();
    }

    /**
     * Feign 工具提供者（classpath 存在 FeignClient 时才注册）
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClient")
    public FeignToolProvider feignToolProvider(ApplicationContext applicationContext) {
        return new FeignToolProvider(applicationContext);
    }

    /**
     * HTTP 工具提供者（基于 OpenAPI 文档，通过 WebClient 调用 REST 接口）
     * <p>
     * 默认注册，通过 mf.ai.http-provider.services 配置指定服务列表。
     * 未配置服务时 discoverTools 返回空 Map，不影响其他 Provider。
     * </p>
     */
    @Bean
    public HttpToolProvider httpToolProvider() {
        return new HttpToolProvider();
    }

    /**
     * 引擎初始化触发器：在所有单例 Bean（含所有 ToolProvider）就绪后，调用 ApiToolEngine.initialize 聚合工具
     * <p>
     * 放在此处而非 ApiToolEngine 内，避免 ApiToolEngine 依赖 Spring 容器回调接口，保持引擎本身的可测试性。
     * </p>
     */
    @Bean
    public SmartInitializingSingleton apiToolEngineInitializer(ApiToolEngine apiToolEngine,
                                                                List<ToolProvider> toolProviders) {
        return () -> {
            log.info("[ApiToolAutoConfiguration] 发现 {} 个 ToolProvider: {}", toolProviders.size(),
                    toolProviders.stream().map(ToolProvider::getType).toList());
            apiToolEngine.initialize(toolProviders);
        };
    }
}
