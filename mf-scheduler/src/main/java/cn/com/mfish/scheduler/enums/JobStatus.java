package cn.com.mfish.scheduler.enums;

/**
 * @description: 任务状态
 * @author: mfish
 * @date: 2023/2/15 15:06
 */
public enum JobStatus {
    开始(0),
    成功(1),
    失败(2);
    private Integer status;

    JobStatus(int status) {
        this.status = status;
    }

    public int getValue() {
        return status;
    }
}
