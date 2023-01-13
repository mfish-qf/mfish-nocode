package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.oauth.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

/**
 * @author: mfish
 * @date: 2020/2/11 11:13
 */
@Api(tags = "登录")
@Controller
@Slf4j
public class LoginController {
    @Resource
    LoginService loginService;

    @ApiOperation("跳转登录页面")
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 短信发送接口（没有短信网关，短信代码需自己实现）
     *
     * @return
     */
    @ApiOperation("发送短信")
    @PostMapping("/sendMsg")
    @ResponseBody
    @ApiImplicitParam(name = "phone", value = "手机号", paramType = "query", required = true)
    public Result<String> sendMsg(String phone) throws NoSuchAlgorithmException {
        if (StringUtils.isEmpty(phone)) {
            return Result.fail("错误:手机号不允许为空");
        }
        Long codeTime = loginService.getSmsCodeTime(phone);
        //一分钟内返回code倒计时剩余时间
        if (codeTime > 0) {
            return Result.fail(codeTime.toString(), "一分钟内不允许重复发送");
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
