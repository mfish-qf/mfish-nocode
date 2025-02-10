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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.3.2
 */
@Slf4j
@Tag(name = "字典")
@RestController
@RequestMapping("/dict")
public class DictController {
    @Resource
    private DictService dictService;

    /**
     * 分页列表查询
     *
     * @param reqDict 请求参数
     * @return 返回字典-分页列表
     */
    @Operation(summary = "字典-分页列表查询", description = "字典-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:dict:query")
    public Result<PageResult<Dict>> queryPageList(ReqDict reqDict, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDict, reqPage)), "字典-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqDict 请求参数
     * @param reqPage 分页参数
     * @return 返回列表
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
     * @param dict 字典
     * @return 返回字典-添加结果
     */
    @Log(title = "字典-添加", operateType = OperateType.INSERT)
    @Operation(summary = "字典-添加", description = "字典-添加")
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
     * @param dict 字典
     * @return 返回字典-编辑结果
     */
    @Log(title = "字典-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "字典-编辑", description = "字典-编辑")
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
     * @param dict 字典
     * @return 返回校验结果
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
     * @param id id
     * @return 返回字典-通过id删除结果
     */
    @Log(title = "字典-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "字典-通过id删除", description = "字典-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:dict:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return dictService.deleteDict(id);
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return 返回字典-通过id查询结果
     */
    @Operation(summary = "字典-通过id查询", description = "字典-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:dict:query")
    public Result<Dict> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        Dict dict = dictService.getById(id);
        return Result.ok(dict, "字典-查询成功!");
    }

    /**
     * 导出
     *
     * @param reqDict 请求参数
     * @param reqPage 分页参数
     * @throws IOException 异常信息
     */
    @Operation(summary = "导出字典", description = "导出字典")
    @GetMapping("/export")
    @RequiresPermissions("sys:dict:query")
    public void export(ReqDict reqDict, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        ExcelUtils.write("字典", queryList(reqDict, reqPage));
    }
}
