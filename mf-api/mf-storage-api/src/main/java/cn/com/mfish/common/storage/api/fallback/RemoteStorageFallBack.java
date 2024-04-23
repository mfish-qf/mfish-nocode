package cn.com.mfish.common.storage.api.fallback;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import cn.com.mfish.common.storage.api.remote.RemoteStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @description: 文件接口降级处理
 * @author: mfish
 * @date: 2023/12/18
 */
@Component
@Slf4j
public class RemoteStorageFallBack implements FallbackFactory<RemoteStorageService> {
    @Override
    public RemoteStorageService create(Throwable cause) {
        log.error("错误:文件接口调用异常", cause);
        return new RemoteStorageService() {
            @Override
            public Result<StorageInfo> queryByKey(String origin, String fileKey) {
                return Result.fail("错误:根据ID获取组织列表失败" + cause.getMessage());
            }

            @Override
            public Result<Boolean> logicDelete(String origin, String id) {
                return Result.fail("错误:逻辑删除文件失败" + cause.getMessage());
            }
        };

    }
}
