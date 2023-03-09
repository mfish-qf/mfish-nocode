package cn.com.mfish.storage.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.web.page.ReqPage;
import cn.com.mfish.storage.entity.SysFile;
import cn.com.mfish.storage.req.ReqSysFile;
import cn.com.mfish.storage.service.SysFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description: 文件存储
 * @author: mfish
 * @date: 2023-03-02
 * @version: V1.0.0
 */
@Slf4j
@Api(tags = "文件存储")
@RestController
@RequestMapping("/sysFile")
public class SysFileController {
    @Resource
    private SysFileService sysFileService;

    /**
     * 分页列表查询
     *
     * @param reqSysFile 文件存储请求参数
     * @return 返回文件存储-分页列表
     */
    @ApiOperation(value = "文件存储-分页列表查询", notes = "文件存储-分页列表查询")
    @GetMapping
    public Result<PageResult<SysFile>> queryPageList(ReqSysFile reqSysFile, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getDelFlag, 0)
                .like(reqSysFile.getFileName() != null, SysFile::getFileName, reqSysFile.getFileName())
                .like(reqSysFile.getFileType() != null, SysFile::getFileType, reqSysFile.getFileType())
                .orderByDesc(SysFile::getCreateTime);
        return Result.ok(new PageResult<>(sysFileService.list(queryWrapper)), "文件存储-查询成功!");
    }

    /**
     * 添加
     *
     * @param sysFile 文件存储对象
     * @return 返回文件存储-添加结果
     */
    @Log(title = "文件存储-添加", operateType = OperateType.INSERT)
    @ApiOperation("文件存储-添加")
    @PostMapping
    public Result<SysFile> add(@RequestBody SysFile sysFile) {
        if (sysFileService.save(sysFile)) {
            return Result.ok(sysFile, "文件存储-添加成功!");
        }
        return Result.fail(sysFile, "错误:文件存储-添加失败!");
    }

    /**
     * 编辑
     *
     * @param sysFile 文件存储对象
     * @return 返回文件存储-编辑结果
     */
    @Log(title = "文件存储-编辑", operateType = OperateType.UPDATE)
    @ApiOperation("文件存储-编辑")
    @PutMapping
    public Result<SysFile> edit(@RequestBody SysFile sysFile) {
        if (sysFileService.updateById(sysFile)) {
            return Result.ok(sysFile, "文件存储-编辑成功!");
        }
        return Result.fail(sysFile, "错误:文件存储-编辑失败!");
    }

    @Log(title = "设置文件状态", operateType = OperateType.UPDATE)
    @ApiOperation("设置文件状态 0 公开 1 私密")
    @PutMapping("/status")
    public Result<SysFile> setStatus(@RequestBody SysFile sysFile) {
        if (sysFileService.updateById(new SysFile().setId(sysFile.getId()).setIsPrivate(sysFile.getIsPrivate()))) {
            return Result.ok(sysFile, "文件状态设置成功!");
        }
        return Result.fail(sysFile, "错误:文件状态设置失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回文件存储-删除结果
     */
    @Log(title = "文件存储-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation("文件存储-通过id删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (sysFileService.updateById(new SysFile().setId(id).setDelFlag(1))) {
            return Result.ok(true, "文件存储-删除成功!");
        }
        return Result.fail(false, "错误:文件存储-删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回文件存储对象
     */
    @ApiOperation("文件存储-通过id查询")
    @GetMapping("/{id}")
    public Result<SysFile> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        SysFile sysFile = sysFileService.getById(id);
        return Result.ok(sysFile, "文件存储-查询成功!");
    }
}
