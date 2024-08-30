package cn.com.mfish.common.core.enums;

import lombok.Getter;

/**
 * @author: mfish
 * @description: 请求来源
 * @date: 2022/9/6 17:33
 */
@Getter
public enum ReqSource {
    //其他
    OTHER(0),
    //管理端
    MANAGER(1),
    //手机端
    PHONE(2);
    private final Integer value;

    ReqSource(Integer value) {
        this.value = value;
    }

}
