package cn.com.mfish.common.api.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: mfish
 * @description: Controller方法注解解析器
 * <p>
 * 单实例模式下Feign接口直接调用Boot*Service实现，不经过Controller层。
 * 但权限注解（@RequiresPermissions、@RequiresRoles）声明在Controller方法上。
 * <p>
 * 本解析器在首次调用时扫描所有Controller，构建 HTTP路径 → Controller方法 的映射。
 * 通过Feign接口方法的HTTP路径，反查到对应的Controller方法，获取其上的权限注解。
 * <p>
 * 路径匹配原理：
 * - Feign接口方法 @GetMapping("/menu/tree") → 路径 /menu/tree
 * - Controller类 @RequestMapping("/menu") + 方法 @GetMapping("/tree") → 路径 /menu/tree
 * - 两者路径一致，可匹配
 * @date: 2026/07/15
 */
@Slf4j
@Component
public class ControllerMethodResolver {

    private final ApplicationContext applicationContext;
    /**
     * key = HTTP方法:路径（如 "GET:/menu/tree"），value = Controller方法
     */
    private final Map<String, Method> pathToMethod = new HashMap<>();
    private volatile boolean initialized = false;

    public ControllerMethodResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 懒加载初始化，首次调用时扫描所有Controller
     */
    private synchronized void ensureInitialized() {
        if (initialized) return;
        Map<String, Object> beans = new HashMap<>();
        beans.putAll(applicationContext.getBeansWithAnnotation(RestController.class));
        beans.putAll(applicationContext.getBeansWithAnnotation(Controller.class));
        for (Object bean : beans.values()) {
            Class<?> clazz = ClassUtils.getUserClass(bean);
            registerController(clazz);
        }
        initialized = true;
        log.info("[BootApiAuth] Controller方法映射初始化完成, 共{}个映射", pathToMethod.size());
    }

    /**
     * 注册Controller类的所有映射方法
     */
    private void registerController(Class<?> controllerClass) {
        // 类级别 @RequestMapping 提取基础路径
        String basePath = "";
        RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(controllerClass, RequestMapping.class);
        if (classMapping != null && classMapping.value().length > 0) {
            basePath = classMapping.value()[0];
        }
        for (Method method : controllerClass.getDeclaredMethods()) {
            MappingInfo info = extractMappingInfo(method);
            if (info == null) continue;
            String fullPath = joinPath(basePath, info.path);
            String key = info.httpMethod + ":" + fullPath;
            pathToMethod.putIfAbsent(key, method);
        }
    }

    /**
     * 从方法注解中提取HTTP方法和路径
     */
    private MappingInfo extractMappingInfo(Method method) {
        GetMapping get = AnnotatedElementUtils.findMergedAnnotation(method, GetMapping.class);
        if (get != null) return new MappingInfo("GET", firstPath(get.value(), get.path()));
        PostMapping post = AnnotatedElementUtils.findMergedAnnotation(method, PostMapping.class);
        if (post != null) return new MappingInfo("POST", firstPath(post.value(), post.path()));
        PutMapping put = AnnotatedElementUtils.findMergedAnnotation(method, PutMapping.class);
        if (put != null) return new MappingInfo("PUT", firstPath(put.value(), put.path()));
        DeleteMapping delete = AnnotatedElementUtils.findMergedAnnotation(method, DeleteMapping.class);
        if (delete != null) return new MappingInfo("DELETE", firstPath(delete.value(), delete.path()));
        PatchMapping patch = AnnotatedElementUtils.findMergedAnnotation(method, PatchMapping.class);
        if (patch != null) return new MappingInfo("PATCH", firstPath(patch.value(), patch.path()));
        // 直接使用 @RequestMapping
        RequestMapping rm = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (rm != null) {
            String path = firstPath(rm.value(), rm.path());
            if (rm.method().length > 0) {
                return new MappingInfo(rm.method()[0].name(), path);
            }
            return new MappingInfo("ANY", path);
        }
        return null;
    }

    private String firstPath(String[] values, String[] paths) {
        if (values != null && values.length > 0) return values[0];
        if (paths != null && paths.length > 0) return paths[0];
        return "";
    }

    /**
     * 拼接路径，确保以 / 开头，无重复斜杠
     */
    private String joinPath(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isEmpty()) continue;
            if (!part.startsWith("/") && sb.length() > 0) sb.append("/");
            sb.append(part);
        }
        String result = sb.toString();
        return result.isEmpty() ? "/" : result;
    }

    /**
     * 通过Feign接口方法查找对应Controller方法上的注解
     *
     * @param feignMethod    Feign接口方法
     * @param annotationType 要查找的注解类型
     * @return Controller方法上的注解，未找到返回null
     */
    public <A extends Annotation> A findAnnotationFromController(Method feignMethod, Class<A> annotationType) {
        ensureInitialized();
        MappingInfo info = extractMappingInfo(feignMethod);
        if (info == null) return null;
        String key = info.httpMethod + ":" + info.path;
        Method controllerMethod = pathToMethod.get(key);
        if (controllerMethod == null) return null;
        return controllerMethod.getAnnotation(annotationType);
    }

    private record MappingInfo(String httpMethod, String path) {
    }
}
