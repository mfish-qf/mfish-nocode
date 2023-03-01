package cn.com.mfish.scheduler.invoke;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.scheduler.config.utils.InvokeUtils;
import cn.com.mfish.common.scheduler.api.entity.JobLog;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: 远程RPC调用任务(实现类写在具体服务中, 通过feign接口访问)
 * @author: mfish
 * @date: 2023/2/13 16:46
 */
@Slf4j
public class RPCInvoke implements BaseInvoke {

    /**
     * RPC调度尽量不要调用时间过长的任务，一般用于服务之间简单通知
     *
     * @param jobLog
     * @param params
     * @return
     */
    @Override
    public <T> Object run(JobLog jobLog, List<T> params) {
        try {
            Object obj = InvokeUtils.invokeFeignMethod(jobLog.getClassName(), jobLog.getMethodName(), params);
            log.info("返回结果:" + obj);
            //如果返回结果为Result类型，判断结果是否成功。不成功认为任务失败
            if (obj instanceof Result) {
                Result result = (Result) obj;
                if (!result.isSuccess()) {
                    throw new MyRuntimeException(result.getMsg());
                }
            }
            return obj;
        } catch (Exception e) {
            log.error("错误:任务执行异常", e);
            throw new MyRuntimeException(e.getMessage());
        }
    }

}
