package cn.com.mfish.common.ds.aspect;

import cn.com.mfish.common.core.annotation.GlobalException;
import cn.com.mfish.common.ds.annotation.DataScope;
import cn.com.mfish.common.ds.annotation.DataScopes;
import cn.com.mfish.common.ds.common.DataScopeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 数据范围切面
 * @author: mfish
 * @date: 2024/4/25
 */
@Aspect
@Component
@GlobalException
@Slf4j
public class DataScopeAspect {

    @Before("@annotation(cn.com.mfish.common.ds.annotation.DataScopes)||@annotation(cn.com.mfish.common.ds.annotation.DataScope)")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        List<DataScope> list = new ArrayList<>();
        if (method.isAnnotationPresent(DataScopes.class)) {
            DataScopes dataScopes = method.getAnnotation(DataScopes.class);
            list.addAll(Arrays.stream(dataScopes.value()).toList());
        } else {
            list.add(method.getAnnotation(DataScope.class));
        }
        DataScopeUtils.context.set(list);
    }

    @AfterReturning("@annotation(cn.com.mfish.common.ds.annotation.DataScopes)||@annotation(cn.com.mfish.common.ds.annotation.DataScope)")
    public void doAfterReturning() {
        DataScopeUtils.context.remove();

    }

    @AfterThrowing("@annotation(cn.com.mfish.common.ds.annotation.DataScopes)||@annotation(cn.com.mfish.common.ds.annotation.DataScope)")
    public void doAfterThrowing() {
        DataScopeUtils.context.remove();
    }
}
