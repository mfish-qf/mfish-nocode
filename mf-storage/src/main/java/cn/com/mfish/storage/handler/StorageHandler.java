package cn.com.mfish.storage.handler;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.storage.common.StorageUtils;
import cn.com.mfish.storage.entity.StorageInfo;
import cn.com.mfish.storage.enums.SuffixType;
import cn.com.mfish.storage.service.StorageService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;

/**
 * @description: 缓存处理
 * @author: mfish
 * @date: 2023/1/5 15:56
 */
@Component
public class StorageHandler {
    @Resource
    Storage storage;
    @Resource
    StorageService storageService;

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
        String id = Utils.uuid32();
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setId(id);
        storageInfo.setFileName(fileName);
        storageInfo.setFileSize((int) contentLength);
        String formatPath = StorageUtils.formatFilePath(path);
        if (StringUtils.isEmpty(formatPath)) {
            storageInfo.setFilePath(DateFormatUtils.format(new Date(), "yyyy/MM/dd"));
        } else {
            storageInfo.setFilePath(formatPath + "/" + DateFormatUtils.format(new Date(), "yyyy/MM/dd"));
        }
        storageInfo.setFileType(contentType);
        String key = generateKey(fileName, id, contentType);
        storageInfo.setFileKey(key);
        String filePath = storageInfo.getFilePath() + "/" + key;
        String url = storage.generateUrl(filePath);
        storageInfo.setFileUrl(url);
        storageInfo.setIsPrivate(isPrivate);
        storage.store(inputStream, contentLength, contentType, filePath);
        storageService.save(storageInfo);
        return storageInfo;
    }

    private String generateKey(String originalFilename, String id, String contentType) {
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

    public void delete(String filePath) {
        storage.delete(filePath);
    }
}
