package cn.com.mfish.common.file.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 缓存属性
 * @author: mfish
 * @date: 2023/1/5 16:41
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "storage")
@RefreshScope
public class StorageProperties {
    private String active;
    private Local local;
    private AliYun aliyun;
    /**
     * 后端服务文件接口地址
     */
    private String address;

    public static class Local {
        private String storagePath;

        public String getStoragePath() {
            return storagePath;
        }

        public void setStoragePath(String storagePath) {
            this.storagePath = storagePath;
        }
    }

    public static class AliYun {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }

}
