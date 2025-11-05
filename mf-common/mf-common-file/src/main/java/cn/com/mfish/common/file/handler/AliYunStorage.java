package cn.com.mfish.common.file.handler;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * @description: 阿里云缓存
 * @author: mfish
 * @date: 2023/1/5 15:46
 */
@Slf4j
public class AliYunStorage extends AbstractStorage {

    public AliYunStorage(String address) {
        super(address);
    }

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public AliYunStorage setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public AliYunStorage setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public AliYunStorage setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public AliYunStorage setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    private OSS getOSSClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    private String getBaseUrl() {
        return "https://" + bucketName + "." + endpoint + "/";
    }

    /**
     * 阿里云OSS对象存储简单上传实现
     */
    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String filePath) {
        OSS oss = getOSSClient();
        try {
            // 文件上传适用于小文件上传, 建议 20M以下的文件使用该接口
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentLength);
            objectMetadata.setContentType(contentType);
            // 对象键（Key）是对象在存储桶中的唯一标识，filePath为文件全路径会在阿里云创建相应目录
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filePath, inputStream, objectMetadata);
            oss.putObject(putObjectRequest);
            log.info("文件上传成功，文件路径为：{}", filePath);
        } catch (Exception ex) {
            log.error("错误:文件存储异常，文件路径为：{}，异常信息：{}", filePath, ex.getMessage(), ex);
            throw new MyRuntimeException("错误:文件存储异常");
        } finally {
            oss.shutdown();
        }
    }

    @Override
    public Resource loadAsResource(String filePath) {
        OSS ossClient = getOSSClient();
        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filePath);
            // 设置URL过期时间为1小时
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            generatePresignedUrlRequest.setExpiration(expiration);
            URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
            Resource resource = new UrlResource(url);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void delete(String filePath) {
        OSS oss = getOSSClient();
        try {
            oss.deleteObject(bucketName, filePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MyRuntimeException("错误:文件删除异常");
        } finally {
            oss.shutdown();
        }
    }

    @Override
    public String buildUrl(String filePath, Integer isPrivate) {
        //公有文件前端直接访问阿里云oss文件地址
        if (isPrivate == 0) {
            return getBaseUrl() + filePath;
        }
        //私有文件访问文件路径为本地后台地址
        return super.buildUrl(filePath, isPrivate);
    }
}
