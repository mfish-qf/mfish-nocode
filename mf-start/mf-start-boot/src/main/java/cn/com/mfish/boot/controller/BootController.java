package cn.com.mfish.boot.controller;

import cn.com.mfish.common.captcha.service.CheckCodeService;
import cn.com.mfish.common.core.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description: 单实例补充接口
 * @author: mfish
 * @date: 2024/1/26
 */
@Slf4j
@Api(tags = "单实例补充接口")
@RestController
public class BootController {
    @Resource
    CheckCodeService checkCodeService;

    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @GetMapping("/captcha")
    public Result<Map<String, Object>> getCaptcha() {
        return checkCodeService.createCaptcha();
    }

    @ApiOperation(value = "未授权页面", notes = "未授权页面")
    @GetMapping("/404")
    public Result<String> noAuth() {
        return Result.fail("错误:未授权，请联系管理员");
    }
}
