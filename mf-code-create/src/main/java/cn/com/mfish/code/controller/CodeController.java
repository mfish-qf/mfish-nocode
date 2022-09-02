package cn.com.mfish.code.controller;

import cn.com.mfish.code.common.FreemarkerUtils;
import cn.com.mfish.code.vo.CodeVo;
import cn.com.mfish.common.core.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：qiufeng
 * @description：代码生成控制器
 * @date ：2022/8/18 16:45
 */
@Api(tags = "代码生成")
@RestController
@RequestMapping("/code")
public class CodeController {
    @Resource
    FreemarkerUtils freemarkerUtils;

    @ApiOperation("代码生成")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schema", value = "库名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "packageName", value = "项目包名", paramType = "query"),
    })
    public Result<List<CodeVo>> getCode(String schema, String tableName, String packageName) {
        try {
            return Result.ok(freemarkerUtils.getCode(schema, tableName, packageName), "生成代码成功");
        } catch (Exception ex) {
            return Result.fail("错误:生成代码失败");
        }
    }

    @ApiOperation("代码生成并保存到本地")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schema", value = "库名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "packageName", value = "项目包名", paramType = "query"),
    })
    public Result<String> saveCode(String schema, String tableName, String packageName) {
        try {
            List<CodeVo> list = freemarkerUtils.getCode(schema, tableName, packageName);
            if (freemarkerUtils.saveCode(list)) {
                return Result.ok("保存成功");
            }
            return Result.fail("保存失败");
        } catch (Exception ex) {
            return Result.fail("错误:生成代码失败");
        }
    }
}
