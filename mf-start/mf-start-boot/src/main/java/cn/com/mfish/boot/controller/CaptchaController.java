package cn.com.mfish.boot.controller;

import cn.com.mfish.common.captcha.service.CheckCodeService;
import cn.com.mfish.common.core.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description: 验证码
 * @author: mfish
 * @date: 2024/1/26
 */
@Slf4j
@Api(tags = "验证码")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Resource
    CheckCodeService checkCodeService;
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @GetMapping
    public Result<Map<String, Object>> getCaptcha() {
        return checkCodeService.createCaptcha();
    }
}
