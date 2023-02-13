package cn.com.mfish.scheduler.execute;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.scheduler.enums.JobType;
import cn.com.mfish.scheduler.common.JobUtils;
import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.invoke.BaseInvoke;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

/**
 * @description: 抽象job执行
 * @author: mfish
 * @date: 2023/2/7 10:41
 */
@Slf4j
public abstract class AbstractJobExecute extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobMap = context.getMergedJobDataMap();
        Job job = JSON.parseObject(jobMap.get(JobUtils.JOB_DATA_MAP).toString(), Job.class);
        String strParams = job.getParams();
        List<Object> list = null;
        if (!StringUtils.isEmpty(strParams)) {
            list = JSON.parseArray(strParams);
        }
        execute(JobType.getJob(job.getJobType()), job.getClassName(), job.getMethodName(), list);
    }

    protected abstract void execute(BaseInvoke baseJob, String className, String methodName, List<Object> params);
}
