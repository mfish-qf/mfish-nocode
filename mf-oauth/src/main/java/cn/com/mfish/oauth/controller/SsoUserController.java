package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.oauth.annotation.InnerUser;
import cn.com.mfish.oauth.annotation.SSOLogAnnotation;
import cn.com.mfish.oauth.api.entity.UserInfo;
import cn.com.mfish.oauth.cache.redis.UserTokenCache;
import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.common.SerConstant;
import cn.com.mfish.oauth.entity.RedisAccessToken;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.service.OAuth2Service;
import cn.com.mfish.oauth.service.SsoUserService;
import cn.com.mfish.oauth.validator.AccessTokenValidator;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

/**
 * @author qiufeng
 * @date 2020/2/17 18:49
 */
@Api(tags = "用户信息")
@RestController
@RequestMapping("/user")
@Slf4j
public class SsoUserController {

    @Resource
    AccessTokenValidator accessTokenValidator;
    @Resource
    OAuth2Service oAuth2Service;
    @Resource
    UserTokenCache userTokenCache;
    @Resource
    SsoUserService ssoUserService;

    @InnerUser
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query")
    })
    @SSOLogAnnotation("getUser")
    public Result<UserInfo> getUserInfo(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        CheckWithResult<RedisAccessToken> result = accessTokenValidator.validate(request, null);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return Result.ok(oAuth2Service.getUserInfo(result.getResult().getUserId()));
    }

    @InnerUser
    @ApiOperation("获取当前用户信息")
    @GetMapping("/current")
    public Result<UserInfo> getCurUserInfo() throws InvocationTargetException, IllegalAccessException {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {
            return null;
        }
        String userId = (String) subject.getPrincipal();
        return Result.ok(oAuth2Service.getUserInfo(userId));
    }

    @ApiOperation("用户登出")
    @GetMapping("/revoke")
    public Result<String> revoke() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {
            String error = "未获取到用户登录状态,无需登出";
            return Result.ok(error);
        }
        String userId = (String) subject.getPrincipal();
        userTokenCache.delUserDevice(SerConstant.DeviceType.Web, userId);
        subject.logout();
        return Result.ok("成功登出");
    }

    @Log(title = "用户信息-添加", operateType = OperateType.INSERT)
    @ApiOperation(value = "用户信息-添加", notes = "用户信息-添加")
    @PostMapping
    public Result<SsoUser> add(@RequestBody SsoUser ssoUser) {
        if (ssoUserService.save(ssoUser)) {
            return Result.ok(ssoUser, "用户信息-添加成功!");
        }
        return Result.fail("错误:用户信息-添加失败!");
    }

    /**
     * 编辑
     *
     * @param ssoUser
     * @return
     */
    @Log(title = "用户信息-编辑", operateType = OperateType.UPDATE)
    @ApiOperation(value = "用户信息-编辑", notes = "用户信息-编辑")
    @PutMapping
    public Result<SsoUser> edit(@RequestBody SsoUser ssoUser) {
        if (ssoUserService.updateById(ssoUser)){
            return Result.ok(ssoUser, "用户信息-编辑成功!");
        }
        return Result.fail("错误:用户信息-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "用户信息-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "用户信息-通过id删除", notes = "用户信息-通过id删除")
    @DeleteMapping("/{id}")
    public Result<?> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (ssoUserService.removeById(id)){
            return Result.ok("用户信息-删除成功!");
        }
        return Result.fail("错误:用户信息-删除失败!");
    }
}
