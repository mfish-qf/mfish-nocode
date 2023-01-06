package cn.com.mfish.storage.common;

/**
 * @description: 缓存通用处理方法
 * @author: mfish
 * @date: 2023/1/5 21:18
 */
public class StorageUtils {
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
}
