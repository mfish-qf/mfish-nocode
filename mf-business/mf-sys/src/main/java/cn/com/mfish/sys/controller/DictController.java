package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.sys.entity.Dict;
import cn.com.mfish.sys.req.ReqDict;
import cn.com.mfish.sys.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.2.1
 */
@Slf4j
@Api(tags = "字典")
@RestController
@RequestMapping("/dict")
public class DictController {
    @Resource
    private DictService dictService;

    /**
     * 分页列表查询
     *
     * @param reqDict
     * @return
     */
    @ApiOperation(value = "字典-分页列表查询", notes = "字典-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:dict:query")
    public Result<PageResult<Dict>> queryPageList(ReqDict reqDict, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDict, reqPage)), "字典-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqDict
     * @param reqPage
     * @return
     */
    private List<Dict> queryList(ReqDict reqDict, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<Dict>()
                .like(reqDict.getDictCode() != null, Dict::getDictCode, reqDict.getDictCode())
                .like(reqDict.getDictName() != null, Dict::getDictName, reqDict.getDictName())
                .eq(reqDict.getStatus() != null, Dict::getStatus, reqDict.getStatus())
                .orderByDesc(true, Dict::getCreateTime);
        return dictService.list(queryWrapper);
    }

    /**
     * 添加
     *
     * @param dict
     * @return
     */
    @Log(title = "字典-添加", operateType = OperateType.INSERT)
    @ApiOperation(value = "字典-添加", notes = "字典-添加")
    @PostMapping
    @RequiresPermissions("sys:dict:insert")
    public Result<Dict> add(@RequestBody Dict dict) {
        Result<Dict> result = verifyDict(dict);
        if (!result.isSuccess()) {
            return result;
        }
        if (dictService.save(dict)) {
            return Result.ok(dict, "字典-添加成功!");
        }
        return Result.fail(dict, "错误:字典-添加失败!");
    }

    /**
     * 编辑
     *
     * @param dict
     * @return
     */
    @Log(title = "字典-编辑", operateType = OperateType.UPDATE)
    @ApiOperation(value = "字典-编辑", notes = "字典-编辑")
    @PutMapping
    @RequiresPermissions("sys:dict:update")
    public Result<Dict> edit(@RequestBody Dict dict) {
        Result<Dict> result = verifyDict(dict);
        if (!result.isSuccess()) {
            return result;
        }
        return dictService.updateDict(dict);
    }

    /**
     * 字典入参校验
     *
     * @param dict
     * @return
     */
    private Result<Dict> verifyDict(Dict dict) {
        if (StringUtils.isEmpty(dict.getDictCode())) {
            return Result.fail("错误:字典编码不允许为空!");
        }
        if (StringUtils.isEmpty(dict.getDictName())) {
            return Result.fail("错误:字典名称不允许为空!");
        }
        if (dictService.isDictCodeExist(dict.getId(), dict.getDictCode())) {
            return Result.fail("错误:字典编码已存在!");
        }
        return Result.ok(dict, "校验成功");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "字典-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "字典-通过id删除", notes = "字典-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:dict:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        return dictService.deleteDict(id);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "字典-通过id查询", notes = "字典-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:dict:query")
    public Result<Dict> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        Dict dict = dictService.getById(id);
        return Result.ok(dict, "字典-查询成功!");
    }

    /**
     * 导出
     * @param reqDict
     * @param reqPage
     * @throws IOException
     */
    @ApiOperation(value = "导出字典", notes = "导出字典")
    @GetMapping("/export")
    @RequiresPermissions("sys:dict:query")
    public void export(ReqDict reqDict, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        ExcelUtils.write("字典", queryList(reqDict, reqPage));
    }
}
