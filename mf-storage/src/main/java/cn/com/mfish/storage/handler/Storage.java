package cn.com.mfish.storage.handler;

import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * @description: 缓存接口
 * @author: mfish
 * @date: 2023/1/5 15:44
 */
public interface Storage {
    void store(InputStream inputStream, long contentLength, String contentType, String keyName);

    Resource loadAsResource(String filePath);

    void delete(String filePath);

    String generateUrl(String keyName);
}
