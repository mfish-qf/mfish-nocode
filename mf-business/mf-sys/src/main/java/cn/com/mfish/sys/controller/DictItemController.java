package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.sys.req.ReqDictItem;
import cn.com.mfish.common.sys.service.DictItemService;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.cache.DictCache;
import cn.com.mfish.sys.entity.Dict;
import cn.com.mfish.sys.mapper.DictMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 字典项
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.2.0
 */
@Slf4j
@Api(tags = "字典项")
@RestController
@RequestMapping("/dictItem")
public class DictItemController {
    @Resource
    private DictItemService dictItemService;
    @Resource
    private DictMapper dictMapper;
    @Resource
    private DictCache dictCache;

    /**
     * 分页列表查询
     *
     * @param reqDictItem
     * @return
     */
    @ApiOperation(value = "字典项-分页列表查询", notes = "字典项-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:dict:query")
    public Result<PageResult<DictItem>> queryPageList(ReqDictItem reqDictItem, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return Result.ok(new PageResult<>(dictItemService.getDictItems(reqDictItem)), "字典项-查询成功!");
    }

    @ApiOperation("根据字典编码获取字典项(值根据类型设置进行转换)")
    @GetMapping("/{dictCode}")
    public Result<List<DictItem>> queryByCode(@ApiParam(name = "dictCode", value = "字典编码") @PathVariable String dictCode) {
        return dictItemService.queryByCode(dictCode);
    }


    /**
     * 添加
     *
     * @param dictItem
     * @return
     */
    @Log(title = "字典项-添加", operateType = OperateType.INSERT)
    @ApiOperation(value = "字典项-添加", notes = "字典项-添加")
    @PostMapping
    @RequiresPermissions("sys:dict:insert")
    public Result<DictItem> add(@RequestBody DictItem dictItem) {
        Result<DictItem> result = verifyDict(dictItem);
        if (!result.isSuccess()) {
            return result;
        }
        Dict dict = dictMapper.selectOne(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, dictItem.getDictCode()));
        if (dict != null) {
            dictItem.setDictId(dict.getId());
            if (dictItemService.save(dictItem)) {
                dictCache.removeOneCache(dictItem.getDictCode());
                return Result.ok(dictItem, "字典项-添加成功!");
            }
        }
        return Result.fail(dictItem, "错误:字典项-添加失败!");
    }

    /**
     * 字典入参校验
     *
     * @param dictItem
     * @return
     */
    private Result<DictItem> verifyDict(DictItem dictItem) {
        if (StringUtils.isEmpty(dictItem.getDictCode())) {
            return Result.fail("错误:字典编码不允许为空!");
        }
        if (StringUtils.isEmpty(dictItem.getDictLabel())) {
            return Result.fail("错误:字典标签不允许为空!");
        }
        if (dictItem.getDictValue() == null || StringUtils.isEmpty(dictItem.getDictValue().toString())) {
            return Result.fail("错误:字典键值不允许为空!");
        }
        return Result.ok(dictItem, "校验成功");
    }

    /**
     * 编辑
     *
     * @param dictItem
     * @return
     */
    @Log(title = "字典项-编辑", operateType = OperateType.UPDATE)
    @ApiOperation(value = "字典项-编辑", notes = "字典项-编辑")
    @PutMapping
    @RequiresPermissions("sys:dict:update")
    public Result<DictItem> edit(@RequestBody DictItem dictItem) {
        Result result = verifyDict(dictItem);
        if (!result.isSuccess()) {
            return result;
        }
        if (dictItemService.updateById(dictItem)) {
            dictCache.removeOneCache(dictItem.getDictCode());
            return Result.ok(dictItem, "字典项-编辑成功!");
        }
        return Result.fail(dictItem, "错误:字典项-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "字典项-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "字典项-通过id删除", notes = "字典项-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:dict:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        DictItem dictItem = dictItemService.getById(id);
        if (dictItem != null && dictItemService.removeById(id)) {
            dictCache.removeOneCache(dictItem.getDictCode());
            return Result.ok(true, "字典项-删除成功!");
        }
        return Result.fail(false, "错误:字典项-删除失败!");
    }
}
