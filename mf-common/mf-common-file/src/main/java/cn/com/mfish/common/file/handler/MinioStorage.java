package cn.com.mfish.common.file.handler;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.FileUtils;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;


/**
 * MinIO 文件存储实现，负责文件上传、读取、删除和访问地址构建。
 */
@Slf4j
public class MinioStorage extends AbstractStorage {

    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucket;
    private MinioClient minioClient;

    public MinioStorage(String address) {
        super(address);
    }

    /**
     * 设置 MinIO 访问密钥。
     */
    public MinioStorage setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    /**
     * 设置 MinIO 访问密钥对应的密文。
     */
    public MinioStorage setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    /**
     * 设置 MinIO 服务地址。
     */
    public MinioStorage setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    /**
     * 设置 MinIO 存储桶名称。
     */
    public MinioStorage setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    /**
     * 通过配置绑定属性
     */
    @PostConstruct
    public void init() {
        try {
            // 初始化 MinIO 客户端
            minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();

            // 检查存储桶是否存在，不存在则创建
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("存储桶 [{}] 创建成功", bucket);
            }
        } catch (Exception e) {
            log.error("MinIO 初始化失败", e);
            throw new MyRuntimeException("MinIO 初始化失败", e);
        }
    }

    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String filePath) {
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(filePath).stream(inputStream, contentLength, 5 * 1024 * 1024) // 5MB 分片
                    .contentType(contentType).build());
            log.info("文件上传成功，文件路径为 {}", filePath);
        } catch (Exception e) {
            log.error("文件上传失败，文件路径为 {}, 异常信息: {}", filePath, e.getMessage(), e);
            throw new MyRuntimeException("文件上传失败", e);
        }
    }

    @Override
    public Resource loadAsResource(String filePath) {
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(filePath).build());
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            log.error("加载文件失败: {}", filePath, e);
            throw new MyRuntimeException("加载文件失败", e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(filePath).build());
            log.info("文件删除成功: {}", filePath);
        } catch (Exception e) {
            log.error("文件删除失败: {}", filePath, e);
            throw new MyRuntimeException("文件删除失败", e);
        }
    }

    @Override
    public String buildUrl(String filePath, Integer isPrivate) {
        int index = filePath.lastIndexOf("/");
        String keyName = (index > 0) ? filePath.substring(index + 1) : filePath;
        return FileUtils.formatFilePath(endpoint) + "/" + keyName;
    }
}
