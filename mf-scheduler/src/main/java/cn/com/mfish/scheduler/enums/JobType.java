package cn.com.mfish.scheduler.enums;

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
    private final int value;
    private static final Map<Integer, BaseInvoke> map = new HashMap<>();

    JobType(int type) {
        value = type;
    }

    static {
        map.put(JobType.LocalJob.value, new LocalInvoke());
        map.put(JobType.RpcJob.value, new RPCInvoke());
        map.put(JobType.MqJob.value, new MQInvoke());
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
