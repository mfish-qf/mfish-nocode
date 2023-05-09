package cn.com.mfish.code.controller;

import cn.com.mfish.code.common.FreemarkerUtils;
import cn.com.mfish.common.code.api.req.ReqCode;
import cn.com.mfish.common.code.api.vo.CodeVo;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: mfish
 * @description: 代码生成控制器
 * @date: 2022/8/18 16:45
 */
@Api(tags = "代码生成")
@RestController
@RequestMapping("/code")
@Slf4j
public class CodeController {
    @Resource
    FreemarkerUtils freemarkerUtils;

    @Log(title = "获取代码", operateType = OperateType.QUERY)
    @ApiOperation("获取代码")
    @PostMapping
    public Result<List<CodeVo>> getCode(@RequestBody ReqCode reqCode) {
        try {
            List<CodeVo> list = freemarkerUtils.getCode(reqCode);
            return Result.ok(list, "生成代码成功");
        } catch (MyRuntimeException ex) {
            return Result.fail(null, ex.getMessage());
        }
    }

    @Log(title = "下载代码", operateType = OperateType.EXPORT)
    @ApiOperation("下载代码")
    @PostMapping("/download")
    public Result<byte[]> downloadCode(@RequestBody ReqCode reqCode) {
        try {
            return Result.ok(freemarkerUtils.downloadCode(reqCode));
        } catch (MyRuntimeException ex) {
            return Result.fail(null, ex.getMessage());
        }
    }

    @Log(title = "代码生成并保存到本地", operateType = OperateType.INSERT)
    @ApiOperation("代码生成并保存到本地")
    @PostMapping("/save")
    public Result<String> saveCode(@RequestBody ReqCode reqCode) {
        try {
            List<CodeVo> list = freemarkerUtils.getCode(reqCode);
            if (freemarkerUtils.saveCode(list)) {
                return Result.ok("代码生成成功");
            }
            return Result.fail("错误:代码生成失败");
        } catch (MyRuntimeException exception) {
            return Result.fail(exception.getMessage());
        }
    }
}
