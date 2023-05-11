package cn.com.mfish.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @description: 缓存通用处理方法
 * @author: mfish
 * @date: 2023/1/5 21:18
 */
@Slf4j
public class FileUtils {
    /**
     * 格式化文件路径,去除后缀 /
     *
     * @return
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
     *
     * @param fileName
     * @return
     */
    public static String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.error("错误:转换文件名异常!文件名:" + fileName, e);
        }
        return fileName;
    }
}
