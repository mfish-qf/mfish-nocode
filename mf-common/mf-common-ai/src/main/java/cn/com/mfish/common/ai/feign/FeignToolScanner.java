package cn.com.mfish.common.ai.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.ai.tool.ToolCallback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Feign接口扫描器
 * <p>
 * 在所有单例Bean初始化完成后，扫描Spring上下文中所有@FeignClient注解的Bean，
 * 为每个public方法创建FeignToolCallback，按服务ID分组注册到FeignToolRegistry。
 * <p>
 * 开发人员只需在mf-api模块下新增Feign接口，启动后自动生成对应的Spring AI Tool，零代码。
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class FeignToolScanner implements SmartInitializingSingleton {

    private final ApplicationContext applicationContext;
    private final FeignToolRegistry registry;

    public FeignToolScanner(ApplicationContext applicationContext, FeignToolRegistry registry) {
        this.applicationContext = applicationContext;
        this.registry = registry;
    }

    @Override
    public void afterSingletonsInstantiated() {
        scanAndRegister();
    }

    /**
     * 扫描所有@FeignClient Bean，为每个方法生成ToolCallback并注册
     */
    private void scanAndRegister() {
        // serviceId → 工具列表
        Map<String, List<ToolCallback>> toolsMap = new HashMap<>();

        // 获取所有带@FeignClient注解的Bean
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
                // 跳过非public方法
                if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                    continue;
                }
                // 跳过Object方法
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
                // 同一serviceId可能有多个FeignClient，追加到已有列表
                toolsMap.computeIfAbsent(serviceId, k -> new ArrayList<>()).addAll(callbacks);
            }
        }

        // 批量注册
        for (Map.Entry<String, List<ToolCallback>> entry : toolsMap.entrySet()) {
            registry.register(entry.getKey(), entry.getValue());
        }

        log.info("[Feign工具扫描] 扫描完成，共注册 {} 个服务的工具", toolsMap.size());
    }

    /**
     * 从Feign代理对象中提取原始接口类型
     * Feign代理通常是JDK动态代理，通过AopProxyUtils或直接遍历接口获取
     */
    private Class<?> getFeignInterface(Object feignProxy) {
        Class<?> proxyClass = feignProxy.getClass();
        // JDK动态代理：遍历接口找到带@FeignClient注解的
        for (Class<?> iface : proxyClass.getInterfaces()) {
            if (iface.isAnnotationPresent(FeignClient.class)) {
                return iface;
            }
        }
        // 非代理对象本身可能就是带注解的接口（如某些Feign实现）
        if (proxyClass.isAnnotationPresent(FeignClient.class)) {
            return proxyClass;
        }
        // 尝试通过AopProxyUtils获取目标类
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
