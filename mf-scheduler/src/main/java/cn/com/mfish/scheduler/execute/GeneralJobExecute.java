package cn.com.mfish.scheduler.execute;

import cn.com.mfish.common.scheduler.api.entity.JobLog;
import cn.com.mfish.scheduler.invoke.BaseInvoke;

import java.util.List;

/**
 * @description: 通用job执行
 * @author: mfish
 * @date: 2023/2/7 11:01
 */
public class GeneralJobExecute extends AbstractJobExecute {
    @Override
    protected <T> void execute(BaseInvoke baseJob, JobLog jobLog, List<T> params) {
        baseJob.run(jobLog, params);
    }
}
