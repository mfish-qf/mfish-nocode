package cn.com.mfish.storage.service.impl;

import cn.com.mfish.storage.entity.StorageInfo;
import cn.com.mfish.storage.mapper.StorageMapper;
import cn.com.mfish.storage.service.StorageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @description: 文件缓存
 * @author: mfish
 * @date: 2023-01-05
 * @version: V1.0.0
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, StorageInfo> implements StorageService {

}
