package cn.com.mfish.storage.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.file.handler.StorageHandler;
import cn.com.mfish.common.file.service.StorageService;
import cn.com.mfish.common.log.annotation.Log;
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

    /**
     * 文件上传新增
     *
     * @param file      上传的文件
     * @param fileName  文件名称，默认为空字符串时使用原始文件名
     * @param path      自定义文件存储路径，默认为空字符串
     * @param isPrivate 是否私有文件，私有文件需要带token才允许访问，1是 0否，默认是1
     * @return 返回文件存储信息
     * @throws IOException 文件读写异常
     */
    @Operation(summary = "文件新增")
    @Log(title = "文件新增", operateType = OperateType.IMPORT)
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

    /**
     * 文件更新上传，根据fileKey更新已有文件
     *
     * @param file      上传的新文件
     * @param fileKey   原始文件的唯一标识key
     * @param fileName  文件名称，默认为空字符串时使用原始文件名
     * @param path      自定义文件存储路径，默认为空字符串
     * @param isPrivate 是否私有文件，私有文件需要带token才允许访问，1是 0否，默认是1
     * @return 返回更新后的文件存储信息
     * @throws IOException 文件读写异常
     */
    @Operation(summary = "文件更新")
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Log(title = "文件更新", operateType = OperateType.IMPORT)
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

    /**
     * 根据文件key获取文件内容
     *
     * @param key 文件唯一标识key
     * @return 返回文件资源响应实体
     */
    @Operation(summary = "文件获取")
    @GetMapping("/{key:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> fetch(@Parameter(name = "key", description = "文件key") @PathVariable String key) {
        return storageService.fetch(key);
    }

}