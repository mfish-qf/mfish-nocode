package cn.com.mfish.scheduler.execute;

import cn.com.mfish.scheduler.entity.Job;
import org.springframework.stereotype.Component;

/**
 * @description: 通用job执行
 * @author: mfish
 * @date: 2023/2/7 11:01
 */
@Component
public class GeneralJobExecute extends AbstractJobExecute {
    @Override
    protected void execute(Job job) {
        System.out.println(job.toString());
    }
}
