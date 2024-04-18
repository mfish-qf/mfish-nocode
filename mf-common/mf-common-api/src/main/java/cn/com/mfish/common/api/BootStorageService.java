package cn.com.mfish.common.api;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.file.service.StorageService;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import org.springframework.stereotype.Service;
import cn.com.mfish.common.storage.api.remote.RemoteStorageService;

import jakarta.annotation.Resource;

/**
 * @description: 缓存接口单实例实现
 * @author: mfish
 * @date: 2024/4/16
 */
@Service("remoteStorageService")
public class BootStorageService implements RemoteStorageService {
    @Resource
    StorageService storageService;

    @Override
    public Result<StorageInfo> queryByKey(String fileKey) {
        return storageService.queryByKey(fileKey);
    }

    @Override
    public Result<Boolean> logicDelete(String id) {
        return storageService.logicDelete(id);
    }
}
