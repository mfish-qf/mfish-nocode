package cn.com.mfish.common.core.enums;

/**
 * @author: mfish
 * @description：请求来源
 * @date: 2022/9/6 17:33
 */
public enum ReqSource {
    //其他
    OTHER(0),
    //管理端
    MANAGER(1),
    //手机端
    PHONE(2);
    private Integer value;

    ReqSource(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
