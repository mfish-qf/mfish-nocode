package cn.com.mfish.scheduler.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 调度属性配置
 * @author: mfish
 * @date: 2023/2/7 11:35
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "org.quartz")
@RefreshScope
public class SchedulerProperties {
    /**
     * 是否记录日志
     */
    private boolean logFlag = true;
    /**
     * 是否覆盖原任务，默认为是
     */
    private boolean cover = true;
    /**
     * 是否自动启动
     */
    private Boolean autoStartup = true;
    /**
     * 是否覆盖已经存在的jobs
     */
    private Boolean overwriteExistingJobs = true;
    /**
     * Job接受applicationContext的成员变量名
     */
    private String applicationContextSchedulerContextKey = "applicationContext";
    /**
     * 延迟启动秒数
     */
    private Integer startupDelay = 0;
    /**
     * 调度配置
     */
    private Scheduler scheduler;
    /**
     * 任务库配置
     */
    private JobStore jobStore;
    /**
     * 线程池配置
     */
    private ThreadPool threadPool;

    @Data
    public static class Scheduler {
        private String instanceName = "MfishClusteredScheduler";
        private String instanceId = "AUTO";
    }

    @Data
    public static class JobStore {
        private String jdbcClass = "org.quartz.impl.jdbcjobstore.JobStoreTX";
        private String driverDelegateClass = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
        private String isClustered = "true";
        private String tablePrefix = "QRTZ_";
        private String dataSource = "mf_scheduler";
        private String clusterCheckinInterval = "20000";
        private String misfireThreshold = "60000";
        /**
         * 使用 quartz.properties，不使用默认配置
         */
        private String useProperties = "false";
    }

    @Data
    public static class ThreadPool {
        private String poolClass = "org.quartz.simpl.SimpleThreadPool";
        private String threadCount = "50";
        private String threadPriority = "5";
    }

}
