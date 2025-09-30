package cn.com.mfish.storage.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.file.handler.StorageHandler;
import cn.com.mfish.common.file.service.StorageService;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import cn.com.mfish.storage.req.ReqSysFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 文件存储
 * @author: mfish
 * @date: 2023-03-02
 * @version: V2.2.0
 */
@Slf4j
@Tag(name = "文件存储")
@RestController
@RequestMapping("/sysFile")
public class SysFileController {
    @Resource
    private StorageService storageService;
    @Resource
    private StorageHandler storageHandler;

    /**
     * 分页列表查询
     *
     * @param reqSysFile 文件存储请求参数
     * @return 返回文件存储-分页列表
     */
    @Operation(summary = "文件存储-分页列表查询", description = "文件存储-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:file:query")
    public Result<PageResult<StorageInfo>> queryPageList(ReqSysFile reqSysFile, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<StorageInfo> queryWrapper = new LambdaQueryWrapper<StorageInfo>()
                .eq(reqSysFile.getDelFlag() != null, StorageInfo::getDelFlag, reqSysFile.getDelFlag())
                .like(reqSysFile.getFileName() != null, StorageInfo::getFileName, reqSysFile.getFileName())
                .like(reqSysFile.getFileType() != null, StorageInfo::getFileType, reqSysFile.getFileType())
                .orderByDesc(StorageInfo::getCreateTime);
        return Result.ok(new PageResult<>(storageService.list(queryWrapper)), "文件存储-查询成功!");
    }

    @Log(title = "设置文件状态", operateType = OperateType.UPDATE)
    @Operation(summary = "设置文件状态 0 公开 1 私密")
    @PutMapping("/status")
    @RequiresPermissions("sys:file:status")
    public Result<StorageInfo> setStatus(@RequestBody StorageInfo storageInfo) {
        StorageInfo oldFile = storageService.getById(storageInfo.getId());
        if (oldFile == null) {
            return Result.fail(storageInfo, "错误:未找到文件!");
        }
        if (storageService.updateById(new StorageInfo().setId(storageInfo.getId())
                .setIsPrivate(storageInfo.getIsPrivate()).setFileUrl(storageHandler
                        .buildFileUrl(oldFile.getFilePath() + "/" + oldFile.getFileKey(), storageInfo.getIsPrivate())))) {
            return Result.ok(storageInfo, "文件状态设置成功!");
        }
        return Result.fail(storageInfo, "错误:文件状态设置失败!");
    }

    @Log(title = "逻辑删除文件恢复", operateType = OperateType.UPDATE)
    @Operation(summary = "逻辑删除文件恢复")
    @PutMapping("/restore/{id}")
    @RequiresPermissions("sys:file:delete")
    public Result<Boolean> restore(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (storageService.updateById(new StorageInfo().setId(id).setDelFlag(0))) {
            return Result.ok(true, "文件存储-逻辑删除成功!");
        }
        return Result.fail(false, "错误:文件存储-逻辑删除失败!");
    }

    /**
     * 通过id逻辑删除
     *
     * @param id 唯一ID
     * @return 返回文件存储-删除结果
     */
    @Log(title = "文件存储-通过id逻辑删除", operateType = OperateType.DELETE)
    @Operation(summary = "文件存储-通过id逻辑删除")
    @DeleteMapping("/logic/{id}")
    @RequiresPermissions("sys:file:delete")
    public Result<Boolean> logicDelete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (storageService.updateById(new StorageInfo().setId(id).setDelFlag(1))) {
            return Result.ok(true, "文件存储-逻辑删除成功!");
        }
        return Result.fail(false, "错误:文件存储-逻辑删除失败!");
    }

    @Log(title = "文件存储-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "文件存储-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:file:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        StorageInfo storageInfo = storageService.getById(id);
        if (storageInfo == null) {
            return Result.fail(false, "错误:未找到文件!");
        }
        if (storageService.removeById(new StorageInfo().setId(id))) {
            try {
                storageHandler.delete(storageInfo.getFilePath() + "/" + storageInfo.getFileKey());
            } catch (MyRuntimeException ex) {
                log.error("文件key:" + storageInfo.getFileKey() + ex.getMessage());
            }
            return Result.ok(true, "文件存储-删除成功!");
        }
        return Result.fail(false, "错误:文件存储-删除失败!");
    }

    /**
     * 通过fileKey查询文件信息
     *
     * @param fileKey 文件key
     * @return 返回文件信息
     */
    @Operation(summary = "获取文件信息-通过fileKey查询")
    @GetMapping("/{fileKey}")
    public Result<StorageInfo> queryByKey(@Parameter(name = "fileKey", description = "文件key") @PathVariable String fileKey) {
        StorageInfo storageInfo = storageService.getOne(new LambdaQueryWrapper<StorageInfo>().eq(StorageInfo::getFileKey, fileKey));
        if (null == storageInfo) {
            return Result.fail("错误:获取文件信息失败!");
        }
        return Result.ok(storageInfo, "获取文件信息-查询成功!");
    }
}
