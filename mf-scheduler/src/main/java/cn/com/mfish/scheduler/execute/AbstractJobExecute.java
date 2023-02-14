package cn.com.mfish.scheduler.execute;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.scheduler.common.InvokeUtils;
import cn.com.mfish.scheduler.common.JobUtils;
import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.enums.JobType;
import cn.com.mfish.scheduler.invoke.BaseInvoke;
import com.alibaba.fastjson.JSON;
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

    /**
     * 执行job
     * 参数支持普通对象数组类型 例如:["****",11] 和 invokeParams对象数组类型
     * 例如：[{"type":"java.lang.String","value":"inner"},{"type":"cn.com.mfish.sys.api.entity.SysLog","value":{"title":"aaaa"}}]
     * @param context
     */
    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobMap = context.getMergedJobDataMap();
        Job job = JSON.parseObject(jobMap.get(JobUtils.JOB_DATA_MAP).toString(), Job.class);
        String strParams = job.getParams();
        List<?> list = null;
        if (!StringUtils.isEmpty(strParams)) {
            //默认转化为invokeParams类型，如果转换失败，直接转换为普通数组
            try {
                list = JSON.parseArray(strParams, InvokeUtils.InvokeParams.class);
            } catch (Exception e) {
                list = JSON.parseArray(strParams);
            }
        }
        execute(JobType.getJob(job.getJobType()), job.getClassName(), job.getMethodName(), list);
    }

    protected abstract <T> void execute(BaseInvoke baseJob, String className, String methodName, List<T> params);
}
