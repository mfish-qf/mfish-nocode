package cn.com.mfish.common.storage.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import cn.com.mfish.common.storage.api.fallback.RemoteStorageFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @description: 远程缓存接口
 * @author: mfish
 * @date: 2023/12/18
 */
@FeignClient(contextId = "remoteStorageService", value = ServiceConstants.STORAGE_SERVICE, fallbackFactory = RemoteStorageFallBack.class)
public interface RemoteStorageService {
    /**
     * 根据文件key查询存储信息
     *
     * @param origin  来源
     * @param fileKey 文件key
     * @return 存储信息
     */
    @GetMapping("/sysFile/{fileKey}")
    Result<StorageInfo> queryByKey(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("fileKey") String fileKey);

    /**
     * 逻辑删除文件
     *
     * @param origin 来源
     * @param id     文件ID
     * @return 删除结果
     */
    @DeleteMapping("/sysFile/logic/{id}")
    Result<Boolean> logicDelete(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id);

    /**
     * 获取文件资源
     *
     * @param origin 来源
     * @param key    文件key
     * @return 文件资源响应
     */
    @GetMapping("/file/{key:.+}")
    ResponseEntity<Resource> fetch(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("key") String key);
}
