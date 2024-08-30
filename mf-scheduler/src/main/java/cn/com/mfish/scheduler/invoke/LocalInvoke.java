package cn.com.mfish.scheduler.invoke;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.scheduler.config.utils.InvokeUtils;
import cn.com.mfish.common.scheduler.api.entity.JobLog;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @description: 本地任务(实现方法写在调度中心的任务)
 * @author: mfish
 * @date: 2023/2/13 16:46
 */
@Slf4j
public class LocalInvoke implements BaseInvoke {

    @Override
    public <T> Object run(JobLog jobLog, List<T> params) {
        try {
            Object obj = InvokeUtils.invokeMethod(jobLog.getClassName(), jobLog.getMethodName(), params);
            log.info("返回结果:{}", obj);
            return obj;
        } catch (InvocationTargetException e) {
            log.error("错误:任务执行异常", e);
            throw new MyRuntimeException(e.getTargetException());
        } catch (Exception e) {
            log.error("错误:任务执行异常", e);
            throw new MyRuntimeException(e.getMessage());
        }
    }
}
