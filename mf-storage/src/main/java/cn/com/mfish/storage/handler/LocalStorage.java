package cn.com.mfish.storage.handler;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.storage.common.StorageUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @description: 本地缓存
 * @author: mfish
 * @date: 2023/1/5 15:45
 */
@Slf4j
@Data
public class LocalStorage implements Storage {
    private String storagePath;
    private String address;

    public void setStoragePath(String storagePath) {
        this.storagePath = StorageUtils.formatFilePath(storagePath);
    }

    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        File file = new File(getFilePath(keyName));
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            throw new MyRuntimeException("错误:存储文件失败");
        }
    }

    public String getFilePath(String keyName) {
        return this.storagePath + "/" + keyName;
    }

    @Override
    public Resource loadAsResource(String filePath) {
        try {
            Path file = Paths.get(getFilePath(filePath));
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            return null;
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void delete(String filePath) {
        Path file = Paths.get(getFilePath(filePath));
        try {
            Files.delete(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String generateUrl(String filePath) {
        int index = filePath.lastIndexOf("/");
        String keyName = "";
        if (index > 0 && index < filePath.length()) {
            keyName = filePath.substring(index + 1);
        } else {
            keyName = filePath;
        }
        return StorageUtils.formatFilePath(address) + "/" + keyName;
    }
}