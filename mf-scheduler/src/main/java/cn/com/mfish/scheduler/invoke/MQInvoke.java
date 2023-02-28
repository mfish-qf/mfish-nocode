package cn.com.mfish.scheduler.invoke;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: 消息队列任务
 * @author: mfish
 * @date: 2023/2/13 16:47
 */
@Slf4j
public class MQInvoke implements BaseInvoke {


    /**
     * 消息推送调度
     *
     * @param className
     * @param methodName
     * @param params
     * @return
     */
    @Override
    public <T> Object run(String className, String methodName, List<T> params) {

        return null;
    }
}
