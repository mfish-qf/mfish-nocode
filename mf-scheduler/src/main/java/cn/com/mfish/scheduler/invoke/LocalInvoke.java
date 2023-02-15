package cn.com.mfish.scheduler.invoke;

import cn.com.mfish.scheduler.common.InvokeUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: 本地任务(实现类写在scheduler中)
 * @author: mfish
 * @date: 2023/2/13 16:46
 */
@Slf4j
public class LocalInvoke implements BaseInvoke {

    @Override
    public <T> Object run(String className, String methodName, List<T> params) {
        try {
            Object obj = InvokeUtils.invokeMethod(className, methodName, params);
            log.info("返回结果:" + obj);
            return obj;
        } catch (Exception e) {
            log.error("任务执行出错", e);
            return null;
        }
    }
}