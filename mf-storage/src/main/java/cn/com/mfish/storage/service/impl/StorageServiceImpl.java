package cn.com.mfish.storage.service.impl;

import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.file.handler.StorageHandler;
import cn.com.mfish.common.file.service.StorageService;
import cn.com.mfish.common.oauth.validator.TokenValidator;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import cn.com.mfish.storage.mapper.StorageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @description: 文件缓存
 * @author: mfish
 * @date: 2023-01-05
 * @version: V2.2.0
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, StorageInfo> implements StorageService {
    @Resource
    StorageHandler storageHandler;
    @Resource
    TokenValidator tokenValidator;

    @Override
    public Result<StorageInfo> queryByKey(String fileKey) {
        StorageInfo storageInfo = baseMapper.selectOne(new LambdaQueryWrapper<StorageInfo>().eq(StorageInfo::getFileKey, fileKey));
        if (null == storageInfo) {
            return Result.fail("错误:获取文件信息失败!");
        }
        return Result.ok(storageInfo, "获取文件信息-查询成功!");
    }

    @Override
    public Result<Boolean> logicDelete(String id) {
        if (baseMapper.updateById(new StorageInfo().setId(id).setDelFlag(1)) > 0) {
            return Result.ok(true, "文件存储-逻辑删除成功!");
        }
        return Result.fail(false, "错误:文件存储-逻辑删除失败!");
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> fetch(String key) {
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        if (key.contains("../")) {
            return ResponseEntity.badRequest().build();
        }
        StorageInfo storageInfo = getOne(new LambdaQueryWrapper<StorageInfo>().eq(StorageInfo::getFileKey, key));
        if (storageInfo == null || (storageInfo.getDelFlag() != null && storageInfo.getDelFlag().equals(1))) {
            return ResponseEntity.notFound().build();
        }
        //如果文件是私有文件需要校验token后访问
        if (storageInfo.getIsPrivate() != null && storageInfo.getIsPrivate().equals(1)) {
            Result<?> result = tokenValidator.validator(ServletUtils.getRequest());
            if (!result.isSuccess()) {
                throw new OAuthValidateException(result.getMsg());
            }
        }
        return storageHandler.fetch(key, storageInfo.getFileName(), storageInfo.getFilePath(), storageInfo.getFileType(), storageInfo.getFileSize());
    }
}
