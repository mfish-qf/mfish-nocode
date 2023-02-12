package cn.com.mfish.scheduler.execute;

import cn.com.mfish.scheduler.entity.Job;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

/**
 * @description: 禁止并发job执行
 * @author: mfish
 * @date: 2023/2/7 10:45
 */
@DisallowConcurrentExecution
@Component
public class DisallowConcurrentJobExecute extends AbstractJobExecute {

    @Override
    protected void execute(Job job) {
        System.out.println(job.toString());
    }
}
