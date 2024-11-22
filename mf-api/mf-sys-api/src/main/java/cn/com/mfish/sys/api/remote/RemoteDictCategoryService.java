package cn.com.mfish.sys.api.remote;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.DictCategory;
import cn.com.mfish.sys.api.fallback.RemoteDictCategoryFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: 远程树形字典服务
 * @author: mfish
 * @date: 2024/11/19
 */
@FeignClient(contextId = "remoteDictCategoryService", value = ServiceConstants.SYS_SERVICE, fallbackFactory = RemoteDictCategoryFallback.class)
public interface RemoteDictCategoryService {
    @GetMapping("/dictCategory/{ids}")
    Result<List<DictCategory>> queryByIds(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("ids") String ids);

    @GetMapping("/dictCategory/tree/{code}")
    Result<List<DictCategory>> queryTreeByCode(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("code") String code, @RequestParam("direction") String direction);

    @GetMapping("/dictCategory/list/{code}")
    Result<List<DictCategory>> queryListByCode(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("code") String code, @RequestParam("direction") String direction);

    @GetMapping("/dictCategory/tree/id/{id}")
    Result<List<DictCategory>> queryTreeById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id, @RequestParam("direction") String direction);

    @GetMapping("/dictCategory/list/id/{id}")
    Result<List<DictCategory>> queryListById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id, @RequestParam("direction") String direction);
}
