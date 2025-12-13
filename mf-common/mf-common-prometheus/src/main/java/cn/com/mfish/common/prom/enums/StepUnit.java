package cn.com.mfish.common.prom.enums;

import lombok.Getter;

/**
 * @description: 时间步长单位
 * @author: mfish
 * @date: 2025/12/9
 */
public enum StepUnit {
    ms("ms"),
    s("s"),
    m("m"),
    h("h"),
    d("d"),
    w("w"),
    y("y");

    @Getter
    private final String value;

    StepUnit(String value) {
        this.value = value;
    }
}
