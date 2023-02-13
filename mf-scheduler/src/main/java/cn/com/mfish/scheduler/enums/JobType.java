package cn.com.mfish.scheduler.enums;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.scheduler.invoke.BaseInvoke;
import cn.com.mfish.scheduler.invoke.LocalInvoke;
import cn.com.mfish.scheduler.invoke.MQInvoke;
import cn.com.mfish.scheduler.invoke.RPCInvoke;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 任务类型
 * @author: mfish
 * @date: 2023/2/13 17:43
 */
public enum JobType {
    LocalJob(0),
    RpcJob(1),
    MqJob(2);
    private int value;
    private static final Map<Integer, BaseInvoke> map = new HashMap<>();

    JobType(int type) {
        value = type;
    }

    static {
        map.put(JobType.LocalJob.value, SpringBeanFactory.getBean(LocalInvoke.class));
        map.put(JobType.RpcJob.value, SpringBeanFactory.getBean(RPCInvoke.class));
        map.put(JobType.MqJob.value, SpringBeanFactory.getBean(MQInvoke.class));
    }

    /**
     * 获取任务类型
     *
     * @param value
     * @return
     */
    public static JobType getJobType(Integer value) {
        for (JobType type : JobType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return LocalJob;
    }

    /**
     * 获取任务处理方法
     *
     * @param value
     * @return
     */
    public static BaseInvoke getJob(Integer value) {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        return map.get(LocalJob.value);
    }
}
