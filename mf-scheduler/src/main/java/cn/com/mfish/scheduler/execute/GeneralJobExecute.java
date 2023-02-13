package cn.com.mfish.scheduler.execute;

import cn.com.mfish.scheduler.invoke.BaseInvoke;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 通用job执行
 * @author: mfish
 * @date: 2023/2/7 11:01
 */
@Component
public class GeneralJobExecute extends AbstractJobExecute {
    @Override
    protected void execute(BaseInvoke baseJob, String className, String methodName, List<Object> params) {
        baseJob.run(className, methodName, params);
    }
}
