package cn.com.mfish.scheduler.invoke;

import java.util.List;

/**
 * @description: 基础任务
 * @author: mfish
 * @date: 2023/2/13 17:41
 */
public interface BaseInvoke {
    <T> Object run(String className, String methodName, List<T> params);
}
