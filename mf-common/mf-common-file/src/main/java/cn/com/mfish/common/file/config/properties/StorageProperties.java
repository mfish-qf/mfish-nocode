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
    private QiNiu qiNiu;
    /**
     * 后端服务文件接口地址
     */
    private String address;

    @Data
    public static class Local {
        private String storagePath;
    }

    @Data
    public static class AliYun {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
    }

    @Data
    public static class QiNiu{
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String domain;
    }

}
