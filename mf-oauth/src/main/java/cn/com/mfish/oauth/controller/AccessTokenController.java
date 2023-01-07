package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.DeviceType;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.entity.AuthorizationCode;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.oauth.cache.redis.UserTokenCache;
import cn.com.mfish.oauth.entity.AccessToken;
import cn.com.mfish.oauth.entity.OAuthClient;
import cn.com.mfish.oauth.service.LoginService;
import cn.com.mfish.oauth.service.OAuth2Service;
import cn.com.mfish.oauth.service.SsoUserService;
import cn.com.mfish.oauth.validator.Code2TokenValidator;
import cn.com.mfish.oauth.validator.Refresh2TokenValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;

/**
 * @author: mfish
 * @date: 2020/2/17 14:17
 */
@Api(tags = "token获取")
@RestController
@RequestMapping
public class AccessTokenController {
    @Resource
    Code2TokenValidator code2TokenValidator;
    @Resource
    Refresh2TokenValidator refresh2TokenValidator;
    @Resource
    OAuth2Service oAuth2Service;
    @Resource
    LoginService loginService;
    @Resource
    UserTokenCache userTokenCache;
    @Resource
    SsoUserService ssoUserService;
    //登录是否互斥
    @Value("${oauth2.login.mutex}")
    private boolean loginMutex = true;

    @ApiOperation("token获取")
    @PostMapping(value = "/accessToken")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.CONTENT_TYPE, value = "请求类型,必须使用application/x-www-form-urlencoded类型", required = true, paramType = "header", defaultValue = "application/x-www-form-urlencoded"),
            @ApiImplicitParam(name = OAuth.OAUTH_GRANT_TYPE, value = "token获取类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_CLIENT_ID, value = "客户端ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_CLIENT_SECRET, value = "客户端密钥", required = true, paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_REDIRECT_URI, value = "回调地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_STATE, value = "状态", paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_CODE, value = "认证code grant_type=authorization_code时必须", paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_USERNAME, value = "账号，手机，email grant_type=password时必须", paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_PASSWORD, value = "密码 grant_type=password时必须", paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_REFRESH_TOKEN, value = "密码 grant_type=refresh_token时必须", paramType = "query")
    })
    @Log(title = "获取token", operateType = OperateType.QUERY)
    public Result<AccessToken> token(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException, InvocationTargetException, IllegalAccessException {
        OAuthTokenRequest tokenRequest = new OAuthTokenRequest(request);
        Result<OAuthClient> result = code2TokenValidator.validateClient(request, null);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        GrantType grantType = GrantType.valueOf(request.getParameter(OAuth.OAUTH_GRANT_TYPE).toUpperCase());
        RedisAccessToken token;
        switch (grantType) {
            case AUTHORIZATION_CODE:
                token = code2Token(request, tokenRequest);
                break;
            case REFRESH_TOKEN:
                token = refresh2Token(request);
                break;
            case PASSWORD:
                token = pwd2Token(request, tokenRequest);
                break;
            default:
                throw new OAuthValidateException(result.getMsg());
        }
        if (!ssoUserService.isUserClientExist(token.getUserId(), token.getClientId())) {
            throw new OAuthValidateException("错误:该用户无此客户端权限!");
        }
        //缓存用户角色信息
        CompletableFuture.supplyAsync(() -> oAuth2Service.getUserInfoAndRoles(token.getUserId(), token.getClientId()));
        if (loginMutex) {
            //增加用户登录互斥缓存
            userTokenCache.addUserTokenCache(DeviceType.Web
                    , SecurityUtils.getSubject().getSession().getId().toString()
                    , token.getUserId(), token.getAccessToken());
        }
        return Result.ok(new AccessToken().setAccess_token(token.getAccessToken()).setExpires_in(token.getExpire()).setRefresh_token(token.getRefreshToken()));
    }

    /**
     * 通过code换取token
     *
     * @param request
     * @param tokenRequest
     * @return
     * @throws OAuthSystemException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private RedisAccessToken code2Token(HttpServletRequest request, OAuthTokenRequest tokenRequest) throws OAuthSystemException, IllegalAccessException, InvocationTargetException {
        Result<AuthorizationCode> result = code2TokenValidator.validateCode(request, null);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return oAuth2Service.code2Token(tokenRequest, result.getData());
    }

    /**
     * 通过refreshtoken获取token
     *
     * @param request
     * @return
     * @throws OAuthSystemException
     */
    private RedisAccessToken refresh2Token(HttpServletRequest request) throws OAuthSystemException {
        Result<RedisAccessToken> result = refresh2TokenValidator.validateToken(request, null);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return oAuth2Service.refresh2Token(result.getData());
    }

    /**
     * 用户名密码直接登录获取token
     *
     * @param request
     * @param tokenRequest
     * @return
     * @throws OAuthSystemException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private RedisAccessToken pwd2Token(HttpServletRequest request, OAuthTokenRequest tokenRequest) throws OAuthSystemException, IllegalAccessException, InvocationTargetException {
        Result<String> result = loginService.login(request);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return oAuth2Service.buildToken(tokenRequest);
    }

}
