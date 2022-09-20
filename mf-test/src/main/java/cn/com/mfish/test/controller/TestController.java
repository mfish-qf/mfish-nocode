package cn.com.mfish.test.controller;


import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.enums.ReqSource;
import cn.com.mfish.common.core.utils.AuthUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.oauth.entity.UserInfo;
import cn.com.mfish.oauth.remote.RemoteUserService;
import cn.com.mfish.test.entity.TestParam;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ：qiufeng
 * @description：摸鱼测试中心
 * @date ：2021/12/3 17:12
 */
@RestController
@Api(tags = "测试接口")
public class TestController {
    @Resource
    RemoteUserService remoteUserService;

    @GetMapping("/user")
    public Result<UserInfo> getUserInfo(HttpServletRequest request) {
        String token = AuthUtils.getAccessToken(request);
        return remoteUserService.getUserInfo(CredentialConstants.INNER, token);
    }

    @GetMapping("/curUser")
    public Result<UserInfo> getCurUserInfo() {
        return remoteUserService.getUserInfo(CredentialConstants.INNER);
    }

    @Log(title = "测试参数", operateType = OperateType.QUERY, reqSource = ReqSource.MANAGER)
    @PostMapping("/testParam")
    public Result<TestParam> testParam(@RequestBody TestParam param) {
        return Result.ok(param);
    }
}
