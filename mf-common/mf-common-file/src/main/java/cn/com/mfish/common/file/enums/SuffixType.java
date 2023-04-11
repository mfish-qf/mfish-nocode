package cn.com.mfish.common.file.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 常用文件后缀类型
 * @author: mfish
 * @date: 2023/1/7 14:22
 */
public enum SuffixType {
    UNKNOWN("", ""),
    PNG("image/png", ".png"),
    JPG("image/jpeg", ".jpg"),
    GIF("image/gif", ".gif"),
    HTML("text/html", ".html"),
    TEXT("text/plain", ".txt"),
    XML("text/xml", ".xml"),
    PDF("application/pdf", ".pdf"),
    DOC("application/msword", ".doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
    XLS("application/vnd.ms-excel", ".xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    PPT("application/vnd.ms-powerpoint", ".ppt"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx");
    private String value;
    private String key;
    private static Map<String, SuffixType> typeMap = new HashMap<>();

    static {
        for (SuffixType type : SuffixType.values()) {
            typeMap.put(type.key, type);
        }
    }

    SuffixType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static SuffixType getSuffixType(String key) {
        if (typeMap.containsKey(key)) {
            return typeMap.get(key);
        }
        return SuffixType.UNKNOWN;
    }
}
