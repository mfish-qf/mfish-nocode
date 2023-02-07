package cn.com.mfish.scheduler.job;

import cn.com.mfish.scheduler.common.JobUtils;
import cn.com.mfish.scheduler.entity.Job;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

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
        execute(job);
    }

    protected abstract void execute(Job job);
}
