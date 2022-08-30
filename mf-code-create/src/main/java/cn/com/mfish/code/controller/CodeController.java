package cn.com.mfish.code.controller;

import cn.com.mfish.code.common.FreemarkerUtils;
import cn.com.mfish.code.entity.CodeInfo;
import cn.com.mfish.common.core.web.AjaxTResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

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
    @PostMapping
    public AjaxTResult<Map<String, String>> getCode(CodeInfo codeInfo) {
        try {
            return AjaxTResult.ok(freemarkerUtils.getCode(codeInfo), "生成代码成功");
        } catch (Exception ex) {
            return AjaxTResult.fail("错误:生成代码失败");
        }
    }
}
