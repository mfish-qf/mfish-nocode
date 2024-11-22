package cn.com.mfish.common.api;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.sys.service.DictCategoryService;
import cn.com.mfish.sys.api.entity.DictCategory;
import cn.com.mfish.sys.api.remote.RemoteDictCategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @description: 树形字典单实例实现
 * @author: mfish
 * @date: 2024/11/19
 */
@Service("remoteDictCategoryService")
public class BootDictCategoryService implements RemoteDictCategoryService {
    @Resource
    DictCategoryService dictCategoryService;

    @Override
    public Result<List<DictCategory>> queryByIds(String origin, String ids) {
        return Result.ok(dictCategoryService.listByIds(Arrays.asList(ids.split(","))), "树形分类-查询成功!");
    }

    @Override
    public Result<List<DictCategory>> queryTreeByCode(String origin, String code, String direction) {
        return Result.ok(dictCategoryService.queryCategoryTreeByCode(code, TreeDirection.getDirection(direction)), "分类树-查询成功!");
    }

    @Override
    public Result<List<DictCategory>> queryListByCode(String origin, String code, String direction) {
        return Result.ok(dictCategoryService.queryCategoryListByCode(code, TreeDirection.getDirection(direction)), "分类列表-查询成功!");
    }

    @Override
    public Result<List<DictCategory>> queryTreeById(String origin, String id, String direction) {
        return Result.ok(dictCategoryService.queryCategoryTreeById(id, TreeDirection.getDirection(direction)), "分类树-查询成功!");
    }

    @Override
    public Result<List<DictCategory>> queryListById(String origin, String id, String direction) {
        return Result.ok(dictCategoryService.queryCategoryListById(id, TreeDirection.getDirection(direction)), "分类列表-查询成功!");
    }
}
