package cn.com.mfish.common.file.handler;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
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
public class LocalStorage extends AbstractStorage {

    public LocalStorage(String address) {
        super(address);
    }

    /**
     * 文件存储路径
     */
    private String storagePath;

    public LocalStorage setStoragePath(String storagePath) {
        this.storagePath = FileUtils.formatFilePath(storagePath);
        return this;
    }

    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String filePath) {
        File file = new File(getFilePath(filePath));
        try {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            throw new MyRuntimeException("错误:文件存储异常");
        }
    }

    /**
     * 获取本地路径，补充本地目录信息
     *
     * @param filePath 传入的文件路径
     * @return 补充后的完整文件路径
     */
    public String getFilePath(String filePath) {
        return this.storagePath + "/" + filePath;
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
            throw new MyRuntimeException("错误:文件删除异常");
        }
    }
}