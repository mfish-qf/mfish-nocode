package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.web.page.PageResult;
import cn.com.mfish.common.web.page.ReqPage;
import cn.com.mfish.sys.entity.DictItem;
import cn.com.mfish.sys.req.ReqDictItem;
import cn.com.mfish.sys.service.DictItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Description: 字典项
 * @Author: mfish
 * @Date: 2023-01-03
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "字典项")
@RestController
@RequestMapping("/dictItem")
public class DictItemController {
    @Resource
    private DictItemService dictItemService;

    /**
     * 分页列表查询
     *
     * @param reqDictItem
     * @return
     */
    @ApiOperation(value = "字典项-分页列表查询", notes = "字典项-分页列表查询")
    @GetMapping
    public Result<PageResult<DictItem>> queryPageList(ReqDictItem reqDictItem, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<DictItem>()
                .eq(reqDictItem.getDictCode() != null, DictItem::getDictCode, reqDictItem.getDictCode())
                .like(reqDictItem.getDictLabel() != null, DictItem::getDictLabel, reqDictItem.getDictLabel())
                .like(reqDictItem.getDictValue() != null, DictItem::getDictValue, reqDictItem.getDictValue())
                .orderBy(true, true, DictItem::getDictSort);
        return Result.ok(new PageResult<>(dictItemService.list(queryWrapper)), "字典项-查询成功!");
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
    public Result<DictItem> add(@RequestBody DictItem dictItem) {
        if (dictItemService.save(dictItem)) {
            return Result.ok(dictItem, "字典项-添加成功!");
        }
        return Result.fail("错误:字典项-添加失败!");
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
    public Result<DictItem> edit(@RequestBody DictItem dictItem) {
        if (dictItemService.updateById(dictItem)) {
            return Result.ok(dictItem, "字典项-编辑成功!");
        }
        return Result.fail("错误:字典项-编辑失败!");
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
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (dictItemService.removeById(id)) {
            return Result.ok(true, "字典项-删除成功!");
        }
        return Result.fail(false, "错误:字典项-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @Log(title = "字典项-批量删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "字典项-批量删除", notes = "字典项-批量删除")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.dictItemService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "字典项-批量删除成功!");
        }
        return Result.fail(false, "错误:字典项-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "字典项-通过id查询", notes = "字典项-通过id查询")
    @GetMapping("/{id}")
    public Result<DictItem> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        DictItem dictItem = dictItemService.getById(id);
        return Result.ok(dictItem, "字典项-查询成功!");
    }
}
