package cn.com.mfish.common.core.enums;

import lombok.Getter;

/**
 * @description: http类型
 * @author: mfish
 * @date: 2023/2/8 16:15
 */
@Getter
public enum HttpType {
    HTTP(0),
    HTTPS(1);
    private final int value;

    HttpType(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * 根据URL判断HTTP类型
     *
     * @url 请求URL
     * @return HTTP类型枚举
     */
    public static HttpType getHttpType(String url) {
        if (url.startsWith("https:")) {
            return HttpType.HTTPS;
        }
        return HttpType.HTTP;
    }
}