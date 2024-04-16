package cn.com.mfish.sys.api.remote;

import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.api.fallback.RemoteDictFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @description: 远程字典服务
 * @author: mfish
 * @date: 2023/5/18 18:15
 */
@FeignClient(contextId = "remoteDictService", value = ServiceConstants.SYS_SERVICE, fallbackFactory = RemoteDictFallback.class)
public interface RemoteDictService {
    @GetMapping("/dictItem/{dictCode}")
    Result<List<DictItem>> queryByCode(@PathVariable("dictCode") String dictCode);
}
