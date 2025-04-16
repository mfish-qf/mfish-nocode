package cn.com.mfish.common.file.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 文件缓存
 * @author: mfish
 * @date: 2023-01-05
 * @version: V2.0.0
 */
public interface StorageService extends IService<StorageInfo> {

    Result<StorageInfo> queryByKey(String fileKey);

    Result<Boolean> logicDelete(String id);
}
