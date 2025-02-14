package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.utils.http.OkHttpUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.oauth.service.LoginService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 码云认证
 * @author: mfish
 * @date: 2025/2/7
 */
@Tag(name = "token获取")
@RestController
@RequestMapping("/gitee")
public class GiteeController {
    @Resource
    LoginService loginService;
    @Value("${gitee.clientId}")
    private String clientId;
    @Value("${gitee.clientSecret}")
    private String clientSecret;
    @Value("${gitee.redirectUri}")
    private String redirectUri;

    @GetMapping("/token/{code}")
    @Operation(summary = "获取gitee token")
    @Parameters({
            @Parameter(name = SerConstant.QR_CODE, description = "gitee认证Code", required = true)
    })
    public Result<Integer> getToken(@PathVariable String code) throws IOException {
        Map<String, String> param = new HashMap<>();
        param.put("client_secret", clientSecret);
        Result<String> result = OkHttpUtils.postJson("https://gitee.com/oauth/token?grant_type=authorization_code" +
                "&code=" + code + "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri, JSON.toJSONString(param), null);
        JSONObject jsonObject;
        if (!result.isSuccess()) {
            return Result.fail("错误：请求码云token失败");
        }

        jsonObject = JSON.parseObject(result.getData());
        String token = jsonObject.getString("access_token");
        if (!isStarred(token)) {
            return Result.ok(1, "警告：未关注码云仓库");
        }
        result = OkHttpUtils.get("https://gitee.com/api/v5/user?access_token=" + token);
        if (!result.isSuccess()) {
            return Result.fail("错误：请求码云用户信息失败");
        }
        result = loginService.login(result.getData(), token, SerConstant.LoginType.Gitee, "system", "false");
        if (!result.isSuccess()) {
            return Result.fail(result.getMsg());
        }
        return Result.ok(0, "登录成功");

    }

    @GetMapping("/url")
    @Operation(summary = "获取gitee url")
    public Result<String> getUrl() {
        return Result.ok("https://gitee.com/oauth/authorize?client_id=" + clientId
                + "&redirect_uri=" + redirectUri + "&response_type=code", "获取地址成功");
    }

    /**
     * 判断是否关注仓库
     *
     * @param token 请求token
     * @return 是否
     */
    private boolean isStarred(String token) {
        try {
            OkHttpUtils.get("https://gitee.com/api/v5/user/starred/qiufeng9862/mfish-nocode?access_token=" + token);
            return true;
        } catch (Exception ignored) {
        }
        try {
            OkHttpUtils.get("https://gitee.com/api/v5/user/starred/qiufeng9862/mfish-nocode-view?access_token=" + token);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

}
