package cn.com.mfish.common.file.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/**
 * @description: 文件缓存
 * @author: mfish
 * @date: 2023-01-05
 * @version: V2.1.0
 */
public interface StorageService extends IService<StorageInfo> {

    /**
     * 通过文件key查询文件信息
     * @param fileKey 文件key
     * @return 文件信息
     */
    Result<StorageInfo> queryByKey(String fileKey);

    /**
     * 逻辑删除文件
     * @param id 文件id
     * @return 删除结果
     */
    Result<Boolean> logicDelete(String id);

    /**
     * 通过文件key获取文件
     * @param key 文件key
     * @return 返回文件资源
     */
    ResponseEntity<Resource> fetch(String key);
}
