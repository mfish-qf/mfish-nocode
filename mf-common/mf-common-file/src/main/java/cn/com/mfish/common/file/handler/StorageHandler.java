package cn.com.mfish.common.file.handler;

import cn.com.mfish.common.core.utils.FileUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.file.enums.SuffixType;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
     * @param strPrivate     是否私有文件 0否 1是
     * @return 返回存储信息对象，包含文件的详细存储信息
     */
    public StorageInfo store(StorageInfo storageInfo, InputStream inputStream, long contentLength, String contentType, String fileName, String path, String strPrivate) {
        if (StringUtils.isEmpty(path)) {
            path = "";
        } else if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String formatPath = FileUtils.formatFilePath(path);
        if (StringUtils.isEmpty(formatPath)) {
            storageInfo.setFilePath(DateFormatUtils.format(new Date(), "yyyy/MM/dd"));
        } else {
            storageInfo.setFilePath(formatPath + "/" + DateFormatUtils.format(new Date(), "yyyy/MM/dd"));
        }
        storageInfo.setFileType(contentType);
        storageInfo.setFileName(fileName);
        storageInfo.setFileSize((int) contentLength);
        String key = buildKey(fileName, storageInfo.getId(), contentType);
        storageInfo.setFileKey(key);
        String filePath = storageInfo.getFilePath() + "/" + key;
        Integer isPrivate = "0".equals(strPrivate) ? 0 : 1;
        String url = storage.buildUrl(filePath, isPrivate);
        storageInfo.setFileUrl(url);
        storageInfo.setIsPrivate(isPrivate);
        storage.store(inputStream, contentLength, contentType, filePath);
        return storageInfo;
    }

    /**
     * 获取文件
     *
     * @param key      文件key，用于唯一标识文件
     * @param fileName 文件名称，用于设置下载时的文件名
     * @param filePath 文件路径，结合文件key定位文件位置
     * @param fileType 文件类型，用于设置响应的内容类型
     * @param size     文件大小，用于设置响应的内容长度
     * @return ResponseEntity对象，包含文件资源和所有HTTP响应信息
     * <p>
     * 本方法根据给定的文件元数据，如文件key、文件路径等信息，尝试加载文件并返回一个包含文件资源和HTTP响应信息的对象。
     * 如果文件不存在，返回一个404 Not Found响应；如果文件存在，则构建响应实体，设置内容类型、内容长度和内容处置等HTTP头信息，
     * 以实现文件的在线查看或强制下载。
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
     * 构建文件的唯一键名
     * 根据原始文件名、唯一标识符和内容类型来生成一个唯一的键名，用于在存储或检索文件时使用
     *
     * @param originalFilename 文件的原始名称，用于提取文件扩展名
     * @param id               唯一标识符，用于生成键名的主要部分
     * @param contentType      文件的内容类型，用于确定文件的默认扩展名
     * @return 返回生成的唯一文件键名
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
     * <p>
     * 本方法根据文件路径和键值对生成文件的输入流，以便于文件的读取或处理
     * 它封装了通过指定路径获取输入流的逻辑，提供了更高级别的抽象
     * </p>
     *
     * @param filePath 文件路径
     *                 文件在文件系统中的路径，用于定位文件位置
     * @param key      文件key
     *                 用于生成完整文件路径的键值，可能结合filePath形成唯一的文件标识
     * @return 返回文件对应的输入流，以便调用者可以读取文件内容
     * 如果文件不存在或无法访问，将返回null
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
     * @param filePath  文件的路径，用于识别特定文件
     * @param isPrivate 是否私有访问的标志，决定生成的链接类型
     * @return 返回生成的文件访问链接
     */
    public String buildFileUrl(String filePath, Integer isPrivate) {
        return storage.buildUrl(filePath, isPrivate);
    }

}
