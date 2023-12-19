package cn.com.mfish.common.file.handler;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.InputStream;

/**
 * @description: 缓存接口
 * @author: mfish
 * @date: 2023/1/5 15:44
 */
public interface Storage {
    /**
     * 存储文件
     *
     * @param inputStream
     * @param contentLength
     * @param contentType
     * @param filePath
     */
    void store(InputStream inputStream, long contentLength, String contentType, String filePath);

    /**
     * 加载文件资源
     *
     * @param filePath
     * @return
     */
    Resource loadAsResource(String filePath);

    /**
     * 获取文件流
     * @param filePath
     * @return
     */
    InputStream getInputStream(String filePath);

    /**
     * 获取文件
     * @param filePath
     * @return
     */
    File getFile(String filePath);

    /**
     * 删除文件
     *
     * @param filePath
     */
    void delete(String filePath);

    /**
     * 构建文件外部访问链接
     *
     * @param filePath
     * @param isPrivate
     * @return
     */
    String buildUrl(String filePath, Integer isPrivate);
}
