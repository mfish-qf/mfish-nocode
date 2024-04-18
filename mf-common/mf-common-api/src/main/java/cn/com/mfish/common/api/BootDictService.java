package cn.com.mfish.common.api;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.sys.service.DictItemService;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.api.remote.RemoteDictService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @description: 远程字典服务单实例实现
 * @author: mfish
 * @date: 2024/4/16
 */
@Service("remoteDictService")
public class BootDictService implements RemoteDictService {

    @Resource
    DictItemService dictItemService;

    @Override
    public Result<List<DictItem>> queryByCode(String dictCode) {
        return dictItemService.queryByCode(dictCode);
    }
}
