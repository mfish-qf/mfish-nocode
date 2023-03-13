package cn.com.mfish.storage.handler;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.Data;
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
@Data
public class AliYunStorage implements Storage {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

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
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        OSS oss = getOSSClient();
        try {
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentLength);
            objectMetadata.setContentType(contentType);
            // 对象键（Key）是对象在存储桶中的唯一标识。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata);
            PutObjectResult putObjectResult = oss.putObject(putObjectRequest);
            System.out.println(putObjectResult);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            oss.shutdown();
        }
    }

    @Override
    public Resource loadAsResource(String filePath) {
        OSS ossClient = getOSSClient();
        try {
            // 设置URL过期时间为1小时
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            GeneratePresignedUrlRequest generatePresignedUrlRequest= new GeneratePresignedUrlRequest(bucketName, filePath);
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
        } finally {
            oss.shutdown();
        }
    }

    @Override
    public String generateUrl(String filePath) {
        return getBaseUrl() + filePath;
    }
}
