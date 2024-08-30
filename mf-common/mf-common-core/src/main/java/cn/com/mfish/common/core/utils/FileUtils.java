package cn.com.mfish.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @description: 缓存通用处理方法
 * @author: mfish
 * @date: 2023/1/5 21:18
 */
@Slf4j
public class FileUtils {
    /**
     * 格式化文件路径,去除后缀 /
     * <p>
     * 该方法首先将文件路径中的所有反斜杠(\)替换为正斜杠(/)，以保证路径在不同操作系统下的兼容性。
     * 此外，如果路径的末尾是正斜杠(/)，则会将此字符移除，确保路径格式的标准化。
     * 这对于后续处理，比如文件操作或URL构建等场景尤为重要。
     *
     * @param path 原始文件路径
     * @return 格式化后的文件路径
     */
    public static String formatFilePath(String path) {
        path = path.replace("\\", "/");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 转换文件名称
     * 使用UTF-8编码方式对文件名进行URL编码，以处理文件名中包含的特殊字符
     * 如果编码过程中发生异常，将记录错误日志并返回原始文件名
     *
     * @param fileName 原始文件名，可能包含特殊字符
     * @return 转换后的文件名，如果转换过程中发生异常，则返回原始文件名
     */
    public static String encodeFileName(String fileName) {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
