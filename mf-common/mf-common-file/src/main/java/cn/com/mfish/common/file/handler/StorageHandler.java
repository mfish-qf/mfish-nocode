package cn.com.mfish.common.file.handler;

import cn.com.mfish.common.core.utils.FileUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.file.enums.SuffixType;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.io.InputStream;
import java.util.Date;

/**
 * @description: 缓存处理
 * @author: mfish
 * @date: 2023/1/5 15:56
 */
@Component
@Slf4j
public class StorageHandler {
    @Resource
    Storage storage;

    /**
     * 文件存储
     *
     * @param inputStream   文件流
     * @param contentLength 长度
     * @param contentType   类型
     * @param fileName      文件名称
     * @param path          业务路径
     * @param isPrivate     是否私有
     * @return
     */
    public StorageInfo store(InputStream inputStream, long contentLength, String contentType, String fileName, String path, Integer isPrivate) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String id = Utils.uuid32();
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setId(id);
        storageInfo.setFileName(fileName);
        storageInfo.setFileSize((int) contentLength);
        String formatPath = FileUtils.formatFilePath(path);
        if (StringUtils.isEmpty(formatPath)) {
            storageInfo.setFilePath(DateFormatUtils.format(new Date(), "yyyy/MM/dd"));
        } else {
            storageInfo.setFilePath(formatPath + "/" + DateFormatUtils.format(new Date(), "yyyy/MM/dd"));
        }
        storageInfo.setFileType(contentType);
        String key = buildKey(fileName, id, contentType);
        storageInfo.setFileKey(key);
        String filePath = storageInfo.getFilePath() + "/" + key;
        String url = storage.buildUrl(filePath, isPrivate);
        storageInfo.setFileUrl(url);
        storageInfo.setIsPrivate(isPrivate);
        storage.store(inputStream, contentLength, contentType, filePath);
        return storageInfo;
    }

    /**
     * 获取文件
     *
     * @param key      文件key
     * @param fileName 文件名称
     * @param filePath 文件路径
     * @param fileType 文件类型
     * @param size     文件大小
     * @return
     */
    public ResponseEntity<org.springframework.core.io.Resource> fetch(String key, String fileName, String filePath, String fileType, Integer size) {
        org.springframework.core.io.Resource file = loadAsResource(buildFilePath(filePath, key));
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = MediaType.parseMediaType(fileType);
        String disposition = "filename*=utf-8'zh_cn'";
        //将header值变成attachment;filename*=utf-8'zh_cn'可以强制文件转换为下载
        //图片直接查看，其他文件类型下载
        if (!StringUtils.isEmpty(fileType) && !fileType.toLowerCase().startsWith("image")) {
            disposition = "attachment;" + disposition;
        }
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok().contentType(mediaType);
        if (size != null && size > 0) {
            builder.contentLength(size);
        }
        if (StringUtils.isEmpty(fileName)) {
            disposition = disposition + FileUtils.encodeFileName(key);
        } else {
            disposition = disposition + FileUtils.encodeFileName(fileName);
        }
        builder.header("Content-Disposition", disposition);
        return builder.body(file);
    }

    private String buildFilePath(String filePath, String key) {
        if (filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }
        return filePath + "/" + key;
    }

    /**
     * 构建文件key
     *
     * @param originalFilename
     * @param id
     * @param contentType
     * @return
     */
    public String buildKey(String originalFilename, String id, String contentType) {
        int index = originalFilename.lastIndexOf('.');
        if (index >= 0) {
            String suffix = originalFilename.substring(index);
            return id + suffix;
        }
        return id + SuffixType.getSuffixType(contentType).toString();
    }

    public org.springframework.core.io.Resource loadAsResource(String filePath) {
        return storage.loadAsResource(filePath);
    }

    /**
     * 获取文件流
     *
     * @param filePath 文件路径
     * @param key      文件key
     * @return
     */
    public InputStream getInputStream(String filePath, String key) {
        return storage.getInputStream(buildFilePath(filePath, key));
    }

    public void delete(String filePath) {
        storage.delete(filePath);
    }

    /**
     * 构建文件访问链接
     *
     * @param filePath
     * @param isPrivate
     * @return
     */
    public String buildFileUrl(String filePath, Integer isPrivate) {
        return storage.buildUrl(filePath, isPrivate);
    }

}
