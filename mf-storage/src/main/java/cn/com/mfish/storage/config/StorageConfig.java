package cn.com.mfish.storage.config;

import cn.com.mfish.storage.enums.StorageType;
import cn.com.mfish.storage.handler.AliYunStorage;
import cn.com.mfish.storage.handler.LocalStorage;
import cn.com.mfish.storage.handler.Storage;
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
    private StorageProperties storageProperties;

    @Autowired
    public StorageConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Bean
    public Storage getStorage() {
        switch (StorageType.getStorageType(storageProperties.getActive())) {
            case 阿里云:
                AliYunStorage aliyunStorage = new AliYunStorage();
                StorageProperties.AliYun aliYun = this.storageProperties.getAliyun();
                aliyunStorage.setAccessKeyId(aliYun.getAccessKeyId());
                aliyunStorage.setAccessKeySecret(aliYun.getAccessKeySecret());
                aliyunStorage.setBucketName(aliYun.getBucketName());
                aliyunStorage.setEndpoint(aliYun.getEndpoint());
                return aliyunStorage;
            default:
                LocalStorage localStorage = new LocalStorage();
                StorageProperties.Local local = this.storageProperties.getLocal();
                localStorage.setAddress(local.getAddress());
                localStorage.setStoragePath(local.getStoragePath());
                return localStorage;
        }
    }
}
