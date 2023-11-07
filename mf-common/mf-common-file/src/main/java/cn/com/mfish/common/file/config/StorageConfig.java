package cn.com.mfish.common.file.config;

import cn.com.mfish.common.file.config.properties.StorageProperties;
import cn.com.mfish.common.file.enums.StorageType;
import cn.com.mfish.common.file.handler.AliYunStorage;
import cn.com.mfish.common.file.handler.LocalStorage;
import cn.com.mfish.common.file.handler.QiNiuStorage;
import cn.com.mfish.common.file.handler.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 缓存配置
 * @author: mfish
 * @date: 2023/1/5 16:43
 */
@Configuration
public class StorageConfig {
    private final StorageProperties storageProperties;

    @Autowired
    public StorageConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Bean
    public Storage getStorage() {
        switch (StorageType.getStorageType(storageProperties.getActive())) {
            case 阿里云:
                StorageProperties.AliYun aliYun = storageProperties.getAliyun();
                return new AliYunStorage(storageProperties.getAddress())
                        .setAccessKeyId(aliYun.getAccessKeyId())
                        .setAccessKeySecret(aliYun.getAccessKeySecret())
                        .setBucketName(aliYun.getBucketName())
                        .setEndpoint(aliYun.getEndpoint());
            case 七牛云:
                StorageProperties.QiNiu qiNiu = storageProperties.getQiNiu();
                QiNiuStorage storage = new QiNiuStorage(storageProperties.getAddress())
                        .setAccessKey(qiNiu.getAccessKey())
                        .setSecretKey(qiNiu.getSecretKey())
                        .setBucket(qiNiu.getBucket())
                        .setDomain(qiNiu.getDomain());
                storage.init();
                return storage;
            default:
                StorageProperties.Local local = storageProperties.getLocal();
                return new LocalStorage(storageProperties.getAddress())
                        .setStoragePath(local.getStoragePath());
        }
    }
}
