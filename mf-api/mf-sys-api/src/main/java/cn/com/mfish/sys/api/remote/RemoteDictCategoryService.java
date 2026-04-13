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
    /**
     * 根据ID列表查询字典分类
     *
     * @param origin 来源
     * @param ids    分类ID列表，逗号分隔
     * @return 字典分类列表
     */
    @GetMapping("/dictCategory/{ids}")
    Result<List<DictCategory>> queryByIds(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("ids") String ids);

    /**
     * 根据编码查询字典分类树
     *
     * @param origin    来源
     * @param code      分类编码
     * @param direction 查询方向
     * @return 字典分类树列表
     */
    @GetMapping("/dictCategory/tree/{code}")
    Result<List<DictCategory>> queryTreeByCode(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("code") String code, @RequestParam("direction") String direction);

    /**
     * 根据编码查询字典分类列表
     *
     * @param origin    来源
     * @param code      分类编码
     * @param direction 查询方向
     * @return 字典分类列表
     */
    @GetMapping("/dictCategory/list/{code}")
    Result<List<DictCategory>> queryListByCode(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("code") String code, @RequestParam("direction") String direction);

    /**
     * 根据ID查询字典分类树
     *
     * @param origin    来源
     * @param id        分类ID
     * @param direction 查询方向
     * @return 字典分类树列表
     */
    @GetMapping("/dictCategory/tree/id/{id}")
    Result<List<DictCategory>> queryTreeById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id, @RequestParam("direction") String direction);

    /**
     * 根据ID查询字典分类列表
     *
     * @param origin    来源
     * @param id        分类ID
     * @param direction 查询方向
     * @return 字典分类列表
     */
    @GetMapping("/dictCategory/list/id/{id}")
    Result<List<DictCategory>> queryListById(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id, @RequestParam("direction") String direction);
}
