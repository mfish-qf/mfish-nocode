package cn.com.mfish.common.file.handler;

import cn.com.mfish.common.core.utils.FileUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * @description: 七牛云缓存
 * @author: mfish
 * @date: 2023/11/7
 */
@Slf4j
public class QiNiuStorage extends AbstractStorage {
    public QiNiuStorage(String address) {
        super(address);
    }

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;

    public QiNiuStorage setAccessKey(String accessKeyId) {
        this.accessKey = accessKeyId;
        return this;
    }

    public QiNiuStorage setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public QiNiuStorage setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public QiNiuStorage setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    private Auth auth;
    private UploadManager uploadManager;
    private BucketManager bucketManager;

    public void init() {
        auth = Auth.create(accessKey, secretKey);
        Configuration c = new Configuration();
        uploadManager = new UploadManager(c);
        bucketManager = new BucketManager(auth, c);
    }

    public String getUpToken(String key) {

        //<bucket>:<key>，表示只允许用户上传指定key的文件。在这种格式下文件默认允许“修改”，已存在同名资源则会被本次覆盖。
        //如果希望只能上传指定key的文件，并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1。
        return auth.uploadToken(bucket, key);
//        return auth.uploadToken(bucket, key, 3600, new StringMap().put("insertOnly", 1));
    }

    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String filePath) {
        try {
            uploadManager.put(inputStream, contentLength, filePath, getUpToken(filePath), null, contentType, false);
        } catch (QiniuException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Resource loadAsResource(String filePath) {
        String encodedFileName = FileUtils.encodeFileName(filePath);
        String publicUrl = String.format("%s/%s", domain, encodedFileName);
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        String downloadRUL = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        try {
            Resource resource = new UrlResource(downloadRUL);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void delete(String filePath) {
        try {
            bucketManager.delete(bucket, filePath);
        } catch (QiniuException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String buildUrl(String filePath, Integer isPrivate) {
        //公有文件前端直接访问七牛云oss文件地址
        if (isPrivate == 0) {
            return domain + "/" + filePath;
        }
        //私有文件访问文件路径为本地后台地址
        return super.buildUrl(filePath, isPrivate);
    }

}
