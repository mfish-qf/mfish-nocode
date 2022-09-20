package cn.com.mfish.oauth.controller;

import cn.com.mfish.oauth.annotation.SSOLogAnnotation;
import cn.com.mfish.oauth.entity.AuthorizationCode;
import cn.com.mfish.oauth.service.LoginService;
import cn.com.mfish.oauth.service.OAuth2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author qiufeng
 * @date 2020/2/11 11:42
 */
@Api(tags = "认证code获取")
@Controller
@RequestMapping
public class AuthorizeController {
    @Resource
    LoginService loginService;
    @Resource
    OAuth2Service oAuth2Service;

    @ApiOperation("认证接口")
    @GetMapping("/authorize")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.OAUTH_RESPONSE_TYPE, value = "返回类型", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_CLIENT_SECRET, value = "客户端ID", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_REDIRECT_URI, value = "回调地址", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_STATE, value = "状态", paramType = "query")
    })
    public Object getAuthorize(Model model, HttpServletRequest request)
            throws OAuthProblemException, OAuthSystemException, URISyntaxException {
        OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            loginService.getLogin(model, request);
            return "login";
        }
        return buildCodeResponse(request, oauthRequest);
    }

    @ApiOperation("认证接口")
    @PostMapping("/authorize")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.OAUTH_RESPONSE_TYPE, value = "返回类型", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_CLIENT_SECRET, value = "客户端ID", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_REDIRECT_URI, value = "回调地址", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_STATE, value = "状态", paramType = "query"),
            @ApiImplicitParam(name = OAuth.OAUTH_USERNAME, value = "账号，手机，email", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_PASSWORD, value = "密码", paramType = "query", required = true)
    })
    @SSOLogAnnotation("getCode")
    public Object authorize(Model model, HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException, OAuthProblemException {
        OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            if (!loginService.postLogin(model, request)) {
                //登录失败时跳转到登陆页面
                return "login";
            }
        }
        return buildCodeResponse(request, oauthRequest);
    }

    private ResponseEntity<Object> buildCodeResponse(HttpServletRequest request, OAuthAuthzRequest oauthRequest) throws OAuthSystemException, URISyntaxException {
        AuthorizationCode code = oAuth2Service.buildCode(oauthRequest);
        // 进行OAuth响应构建
        OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_MOVED_TEMPORARILY);
        // 设置授权码
        builder.setCode(code.getCode());
        String state = oauthRequest.getParam(OAuth.OAUTH_STATE);
        if (!StringUtils.isEmpty(state)) {
            builder.setParam(OAuth.OAUTH_STATE, state);
        }
        // 构建响应
        OAuthResponse response = builder.location(code.getRedirectUri()).buildQueryMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(response.getLocationUri()));
        return new ResponseEntity<>(headers, HttpStatus.valueOf(response.getResponseStatus()));
    }
}
