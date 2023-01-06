package cn.com.mfish.storage.controller;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.storage.entity.StorageInfo;
import cn.com.mfish.storage.handler.StorageHandler;
import cn.com.mfish.storage.service.StorageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @description: 文件缓存
 * @author: mfish
 * @date: 2023/1/5 15:42
 */
@Api(tags = "文件缓存操作")
@RestController
@RequestMapping("/file")
public class StorageController {
    @Resource
    StorageHandler storageHandler;
    @Resource
    StorageService storageService;

    @ApiOperation("文件新增")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件", required = true),
            @ApiImplicitParam(name = "path", value = "定义特殊文件路径 默认为空字符串"),
            @ApiImplicitParam(name = "isPrivate", value = "是否私有文件 1是 0否 默认否"),
    })
    public Result<StorageInfo> upload(@RequestParam("file") MultipartFile file, @RequestParam(name = "path", defaultValue = "") String path, @RequestParam(name = "isPrivate", defaultValue = "0") Integer isPrivate) throws IOException {
        String originalFilename = file.getOriginalFilename();
        StorageInfo info = storageHandler.store(file.getInputStream(), file.getSize(), file.getContentType(), originalFilename, path, isPrivate);
        return Result.ok(info, "文件新增成功");
    }

    @ApiOperation("文件删除")
    @DeleteMapping("/{key}")
    public Result<String> delete(@PathVariable String key) {
        if (StringUtils.isEmpty(key)) {
            return Result.fail("错误:键不允许为空");
        }
        if (storageService.remove(new LambdaQueryWrapper<StorageInfo>().eq(true, StorageInfo::getFileKey, key))) {
            storageHandler.delete(key);
            return Result.ok(key, "删除文件成功");
        }
        return Result.fail(key, "错误:删除文件失败");
    }

    @ApiOperation("文件获取")
    @GetMapping("/{key:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> fetch(@PathVariable String key) {
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        if (key.contains("../")) {
            return ResponseEntity.badRequest().build();
        }
        StorageInfo storageInfo = storageService.getOne(new LambdaQueryWrapper<StorageInfo>().eq(true, StorageInfo::getFileKey, key));
        if (storageInfo == null) {
            return ResponseEntity.notFound().build();
        }
        //前端不允许查看私密文件
        if (storageInfo.getIsPrivate() != null && storageInfo.getIsPrivate().equals(1)) {
            return ResponseEntity.notFound().build();
        }
        String type = storageInfo.getFileType();
        MediaType mediaType = MediaType.parseMediaType(type);
        org.springframework.core.io.Resource file = storageHandler.loadAsResource(storageInfo.getFilePath() + "/" + key);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(mediaType).body(file);
    }

}