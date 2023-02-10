package cn.com.mfish.common.core.enums;

/**
 * @description: http类型
 * @author: mfish
 * @date: 2023/2/8 16:15
 */
public enum HttpType {
    HTTP(0),
    HTTPS(1);
    private int value;

    HttpType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static HttpType getHttpType(String url) {
        if (url.startsWith("https:")) {
            return HttpType.HTTPS;
        }
        return HttpType.HTTP;
    }
}