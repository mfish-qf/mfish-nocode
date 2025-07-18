package cn.com.mfish.storage.controller;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.file.handler.StorageHandler;
import cn.com.mfish.common.file.service.StorageService;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.storage.api.entity.StorageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @description: 文件缓存
 * @author: mfish
 * @date: 2023/1/5 15:42
 */
@Tag(name = "文件缓存操作")
@RestController
@RequestMapping("/file")
@Slf4j
public class StorageController {
    @Resource
    StorageHandler storageHandler;
    @Resource
    StorageService storageService;

    @Operation(summary = "文件新增")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresPermissions("sys:file:upload")
    public Result<StorageInfo> upload(@RequestPart("file") MultipartFile file
            , @Schema(description = "文件名称 默认为空字符串", defaultValue = "") @RequestPart(value = "fileName", required = false) String fileName
            , @Schema(description = "定义特殊文件路径 默认为空字符串", defaultValue = "") @RequestPart(value = "path", required = false) String path
            , @Schema(description = "是否私有文件，私有文件需要带token才允许访问 1是 0否 默认是", defaultValue = "1") @RequestPart(value = "isPrivate", required = false) String isPrivate) throws IOException {
        String originalFilename = fileName;
        if (StringUtils.isEmpty(originalFilename)) {
            originalFilename = file.getOriginalFilename();
        }
        StorageInfo storageInfo = new StorageInfo().setId(Utils.uuid32());
        StorageInfo info = storageHandler.store(storageInfo, file.getInputStream(), file.getSize(), file.getContentType(), originalFilename, path, isPrivate);
        if (storageService.save(info)) {
            return Result.ok(info, "文件新增成功");
        }
        return Result.fail(info, "错误:文件新增失败");
    }

    @Operation(summary = "文件更新")
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresPermissions("sys:file:upload")
    public Result<StorageInfo> upload(@RequestPart("file") MultipartFile file
            , @Schema(description = "文件key") @RequestPart(value = "fileKey") String fileKey
            , @Schema(description = "文件名称 默认为空字符串", defaultValue = "") @RequestPart(value = "fileName", required = false) String fileName
            , @Schema(description = "定义特殊文件路径 默认为空字符串", defaultValue = "") @RequestPart(value = "path", required = false) String path
            , @Schema(description = "是否私有文件，私有文件需要带token才允许访问 1是 0否 默认是", defaultValue = "1") @RequestPart(value = "isPrivate", required = false) String isPrivate) throws IOException {
        String originalFilename = fileName;
        if (StringUtils.isEmpty(originalFilename)) {
            originalFilename = file.getOriginalFilename();
        }
        Result<StorageInfo> result = storageService.queryByKey(fileKey);
        if (!result.isSuccess()) {
            return result;
        }
        StorageInfo storageInfo = result.getData();
        StorageInfo info = storageHandler.store(storageInfo, file.getInputStream(), file.getSize(), file.getContentType(), originalFilename, path, isPrivate);
        if (storageService.updateById(info)) {
            return Result.ok(info, "文件更新成功");
        }
        return Result.fail(info, "错误:文件更新失败");
    }

    @Operation(summary = "文件获取")
    @GetMapping("/{key:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> fetch(@Parameter(name = "key", description = "文件key") @PathVariable String key) {
        return storageService.fetch(key);
    }

}