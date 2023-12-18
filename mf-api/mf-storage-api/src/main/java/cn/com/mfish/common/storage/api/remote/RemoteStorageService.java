package cn.com.mfish.common.storage.api.remote;

import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import cn.com.mfish.common.storage.api.fallback.RemoteStorageFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @description: 远程缓存接口
 * @author: mfish
 * @date: 2023/12/18
 */
@FeignClient(contextId = "remoteStorageService", value = ServiceConstants.STORAGE_SERVICE, fallbackFactory = RemoteStorageFallBack.class)
public interface RemoteStorageService {
    @GetMapping("/sysFile/{fileKey}")
    Result<StorageInfo> queryByKey(@PathVariable("fileKey") String fileKey);
}
