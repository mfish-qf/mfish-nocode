package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.oauth.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.NoSuchAlgorithmException;

/**
 * @author: mfish
 * @date: 2020/2/11 11:13
 */
@Tag(name = "登录")
@Controller
@Slf4j
@RefreshScope
public class LoginController {
    @Resource
    LoginService loginService;
    @Resource
    SsoUserService ssoUserService;
    @Value("${oauth2.user.autoCreate}")
    boolean autoCreateUser = false;

    /**
     * 短信发送接口（没有短信网关，短信代码需自己实现）
     *
     * @return
     */
    @Operation(summary = "发送短信")
    @PostMapping("/sendMsg")
    @ResponseBody
    @Parameter(name = "phone", description = "手机号", required = true)
    public Result<String> sendMsg(String phone) throws NoSuchAlgorithmException {
        if (StringUtils.isEmpty(phone)) {
            return Result.fail("错误:手机号不允许为空");
        }
        //不允许自动创建用户情况下，需要判断手机号是否存在，如果不存在不发短信
        if (!autoCreateUser && !ssoUserService.isAccountExist(phone, null)) {
            return Result.fail("错误:手机号不存在");
        }
        long codeTime = loginService.getSmsCodeTime(phone);
        //一分钟内返回code倒计时剩余时间
        if (codeTime > 0) {
            return Result.fail(Long.toString(codeTime), "一分钟内不允许重复发送");
        }
        String code = loginService.getSmsCode(phone);
        //如果5分钟内不重新生成code
        if (StringUtils.isEmpty(code)) {
            code = OauthUtils.buildCode();
            loginService.saveSmsCode(phone, code);
        }
        loginService.saveSmsCodeTime(phone);
        loginService.sendMsg(phone, code);
        //测试环境返回生成验证码值,生产环境需要隐藏短信码返回值
        return Result.ok(code, "生成成功");
    }
}
