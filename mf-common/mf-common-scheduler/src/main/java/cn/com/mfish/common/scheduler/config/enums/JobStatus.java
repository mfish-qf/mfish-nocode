package cn.com.mfish.common.scheduler.config.enums;

/**
 * @description: 任务状态
 * @author: mfish
 * @date: 2023/2/15 15:06
 */
public enum JobStatus {
    开始(0),
    调度成功(1),
    调度失败(2),
    执行成功(3),
    执行失败(4);
    private Integer status;

    JobStatus(int status) {
        this.status = status;
    }

    public int getValue() {
        return status;
    }
}
