package cn.com.mfish.common.ai.feign;

import cn.com.mfish.common.ai.provider.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Feign工具提供者 —— 实现 {@link ToolProvider}，对接 {@link cn.com.mfish.common.ai.engine.ApiToolEngine}
 * <p>
 * 在所有单例 Bean 初始化完成后，扫描 Spring 上下文中所有 @FeignClient 注解的 Bean，
 * 为每个 public 方法创建 {@link FeignToolCallback}，按服务ID分组返回。
 * </p>
 * <p>
 * 保留原有 {@code FeignToolScanner} 的扫描逻辑，但不再直接注册到 FeignToolRegistry，
 * 而是通过 {@link ToolProvider} 接口统一暴露给 ApiToolEngine 聚合。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class FeignToolProvider implements ToolProvider, SmartInitializingSingleton {

    private final ApplicationContext applicationContext;
    /**
     * 缓存扫描结果，供 ApiToolEngine 在 initialize() 时拉取
     */
    private volatile Map<String, List<ToolCallback>> cachedTools;

    public FeignToolProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String getType() {
        return "feign";
    }

    @Override
    public Map<String, List<ToolCallback>> discoverTools() {
        if (cachedTools == null) {
            // 理论上 afterSingletonsInstantiated 已先行调用，此处兜底
            scanAndCache();
        }
        return cachedTools;
    }

    @Override
    public void afterSingletonsInstantiated() {
        scanAndCache();
    }

    /**
     * 扫描所有 @FeignClient Bean，为每个方法生成 ToolCallback 并缓存
     */
    private void scanAndCache() {
        Map<String, List<ToolCallback>> toolsMap = new HashMap<>();
        Map<String, Object> feignBeans = applicationContext.getBeansWithAnnotation(FeignClient.class);

        for (Map.Entry<String, Object> entry : feignBeans.entrySet()) {
            Object feignProxy = entry.getValue();
            Class<?> feignInterface = getFeignInterface(feignProxy);
            if (feignInterface == null) {
                continue;
            }

            FeignClient annotation = feignInterface.getAnnotation(FeignClient.class);
            if (annotation == null) {
                continue;
            }

            String serviceId = annotation.value().isEmpty() ? annotation.name() : annotation.value();
            if (serviceId.isEmpty()) {
                log.warn("[Feign工具扫描] 跳过未指定服务ID的FeignClient: {}", feignInterface.getName());
                continue;
            }

            List<ToolCallback> callbacks = new ArrayList<>();
            for (Method method : feignInterface.getDeclaredMethods()) {
                if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                    continue;
                }
                if (method.getDeclaringClass() == Object.class) {
                    continue;
                }
                try {
                    FeignToolCallback callback = new FeignToolCallback(feignProxy, method);
                    callbacks.add(callback);
                    log.debug("[Feign工具扫描] 生成工具: {}#{} -> {}",
                            feignInterface.getSimpleName(), method.getName(), serviceId);
                } catch (Exception e) {
                    log.warn("[Feign工具扫描] 生成工具失败: {}#{}",
                            feignInterface.getSimpleName(), method.getName(), e);
                }
            }

            if (!callbacks.isEmpty()) {
                toolsMap.computeIfAbsent(serviceId, k -> new ArrayList<>()).addAll(callbacks);
            }
        }

        cachedTools = toolsMap;
        log.info("[Feign工具扫描] 扫描完成，共发现 {} 个服务的工具", toolsMap.size());
    }

    /**
     * 从 Feign 代理对象中提取原始接口类型
     */
    private Class<?> getFeignInterface(Object feignProxy) {
        Class<?> proxyClass = feignProxy.getClass();
        for (Class<?> iface : proxyClass.getInterfaces()) {
            if (iface.isAnnotationPresent(FeignClient.class)) {
                return iface;
            }
        }
        if (proxyClass.isAnnotationPresent(FeignClient.class)) {
            return proxyClass;
        }
        try {
            Class<?> targetClass = org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass(feignProxy);
            if (targetClass.isAnnotationPresent(FeignClient.class)) {
                return targetClass;
            }
            for (Class<?> iface : targetClass.getInterfaces()) {
                if (iface.isAnnotationPresent(FeignClient.class)) {
                    return iface;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
