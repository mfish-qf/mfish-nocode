package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.http.OkHttpUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.oauth.common.GitLoginInfo;
import cn.com.mfish.oauth.service.LoginService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: github认证
 * @author: mfish
 * @date: 2025/2/10
 */
@Tag(name = "token获取")
@RestController
@RequestMapping("/github")
public class GithubController {
    @Resource
    LoginService loginService;
    @Value("${github.clientId}")
    private String clientId;
    @Value("${github.clientSecret}")
    private String clientSecret;
    @Value("${github.redirectUri}")
    private String redirectUri;
    @Value("${github.bindUri:http://localhost:5186/tenant/info/1?callback=github}")
    private String bindUri;
    @Resource
    SsoUserService ssoUserService;

    @GetMapping("/token/{code}")
    @Operation(summary = "获取github token")
    @Parameters({
            @Parameter(name = SerConstant.QR_CODE, description = "github认证Code", required = true)
    })
    public Result<Integer> getToken(@PathVariable String code) throws IOException {
        Result<GitLoginInfo> result = getGithubAccount(code, redirectUri);
        if (!result.isSuccess()) {
            return Result.fail(result.getMsg());
        }
        String token = result.getData().getToken();
        if (!isStarred(token)) {
            return Result.ok(1, "警告：未关注github仓库");
        }
        Result<String> res = loginService.login(result.getData().getGitInfo(), token, SerConstant.LoginType.Github, "system", "false");
        if (!res.isSuccess()) {
            return Result.fail(result.getMsg());
        }
        return Result.ok(0, "登录成功");
    }

    @GetMapping("/url")
    @Operation(summary = "获取github url")
    public Result<String> getUrl() {
        return Result.ok("https://github.com/login/oauth/authorize?client_id=" + clientId
                + "&redirect_uri=" + redirectUri + "&response_type=code", "获取地址成功");
    }

    @PutMapping("/bind/{code}")
    @Operation(summary = "绑定github账号")
    public Result<Boolean> bindGithub(@Parameter(name = "code", description = "github认证Code", required = true) @PathVariable String code) throws IOException {
        Result<GitLoginInfo> result = getGithubAccount(code, bindUri);
        if (!result.isSuccess()) {
            return Result.fail(result.getMsg());
        }
        String account = result.getData().getAccount();
        return ssoUserService.bindGithub(account);
    }

    /**
     * 获取github账号信息
     *
     * @param code        认证code
     * @param redirectUri 回调地址
     * @return 账号信息
     */
    private Result<GitLoginInfo> getGithubAccount(String code, String redirectUri) throws IOException {
        Map<String, String> param = new HashMap<>();
        param.put("client_id", clientId);
        param.put("client_secret", clientSecret);
        param.put("code", code);
        param.put("redirect_uri", redirectUri);
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "application/json");
        Result<String> result = OkHttpUtils.postJson("https://github.com/login/oauth/access_token", JSON.toJSONString(param), header);
        JSONObject jsonObject;
        if (!result.isSuccess()) {
            return Result.fail("错误：请求github token失败");
        }
        jsonObject = JSON.parseObject(result.getData());
        String token = jsonObject.getString("access_token");
        header.clear();
        header.put("Authorization", "Bearer " + token);
        result = OkHttpUtils.get("https://api.github.com/user", header);
        if (!result.isSuccess()) {
            return Result.fail("错误：请求github用户信息失败");
        }
        jsonObject = JSON.parseObject(result.getData());
        String account = jsonObject.getString("login");
        if (StringUtils.isEmpty(account)) {
            return Result.fail("错误：未获取到账号");
        }
        return Result.ok(new GitLoginInfo(token, account, result.getData()), "获取github账号成功");
    }

    /**
     * 判断是否关注仓库
     *
     * @param token 请求token
     * @return 是否
     */
    private boolean isStarred(String token) {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);
        try {
            OkHttpUtils.get("https://api.github.com/user/starred/mfish-qf/mfish-nocode", header);
            return true;
        } catch (Exception ignored) {
        }
        try {
            OkHttpUtils.get("https://api.github.com/user/starred/mfish-qf/mfish-nocode-view", header);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
