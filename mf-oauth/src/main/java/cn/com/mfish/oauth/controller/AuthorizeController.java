package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.AuthorizationCode;
import cn.com.mfish.oauth.service.LoginService;
import cn.com.mfish.oauth.service.OAuth2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import cn.com.mfish.oauth.oltu.as.request.OAuthAuthzRequest;
import cn.com.mfish.oauth.oltu.as.response.OAuthASResponse;
import cn.com.mfish.oauth.oltu.common.OAuth;
import cn.com.mfish.oauth.oltu.common.message.OAuthResponse;
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
import java.text.MessageFormat;
import java.util.function.BiFunction;

/**
 * @author: mfish
 * @date: 2020/2/11 11:42
 */
@Api(tags = "认证code获取")
@Controller
@RequestMapping
@Slf4j
public class AuthorizeController {
    private static final String LOGIN_PATH = "login";
    private static final String FORCE_LOGIN = "force_login";
    @Resource
    LoginService loginService;
    @Resource
    OAuth2Service oAuth2Service;

    @ApiOperation("认证接口")
    @GetMapping("/authorize")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.OAUTH_RESPONSE_TYPE, value = "返回类型", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_CLIENT_ID, value = "客户端ID", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_REDIRECT_URI, value = "回调地址", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_STATE, value = "状态", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = FORCE_LOGIN, value = "强制登录 值为1时强行返回登录界面", paramType = "query", dataTypeClass = String.class)
    })
    public Object getAuthorize(Model model, HttpServletRequest request) {
        String force = request.getParameter(FORCE_LOGIN);
        boolean forceLogin = false;
        if (!StringUtils.isEmpty(force) && "1".equals(force)) {
            forceLogin = true;
        }
        return authorize(model, request, (m, r) -> loginService.getLogin(m, r), forceLogin);
    }

    @ApiOperation("认证接口")
    @PostMapping("/authorize")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.OAUTH_RESPONSE_TYPE, value = "返回类型", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_CLIENT_ID, value = "客户端ID", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_CLIENT_SECRET, value = "客户端密钥", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_REDIRECT_URI, value = "回调地址", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_STATE, value = "状态", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_USERNAME, value = "账号，手机，email", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = OAuth.OAUTH_PASSWORD, value = "密码", paramType = "query", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = SerConstant.REMEMBER_ME, value = "记住我", paramType = "query", dataTypeClass = String.class)
    })
    @Log(title = "code认证接口", operateType = OperateType.QUERY)
    public Object authorize(Model model, HttpServletRequest request) {
        return authorize(model, request, (m, r) -> loginService.postLogin(m, r), false);
    }

    /**
     * 认证方法
     *
     * @param model    model信息
     * @param request  请求
     * @param function 处理方法get post
     * @return
     */
    private Object authorize(Model model, HttpServletRequest request, BiFunction<Model, HttpServletRequest, Boolean> function, boolean forceLogin) {
        OAuthAuthzRequest oauthRequest;
        try {
            oauthRequest = new OAuthAuthzRequest(request);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new MyRuntimeException("错误:请求参数校验出错");
        }
        log.info(MessageFormat.format("用户:{0}登录状态:{1}", SecurityUtils.getSubject().getPrincipal(), SecurityUtils.getSubject().isAuthenticated()));
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            if (!function.apply(model, request)) {
                //登录失败时跳转到登陆页面
                return LOGIN_PATH;
            }
        }
        //如果是强制登录，当前登录状态登出直接返回登录页面
        if (forceLogin) {
            SecurityUtils.getSubject().logout();
            return LOGIN_PATH;
        }
        return buildCodeResponse(request, oauthRequest);

    }

    private ResponseEntity<Object> buildCodeResponse(HttpServletRequest request, OAuthAuthzRequest oauthRequest) {
        AuthorizationCode code = oAuth2Service.buildCode(oauthRequest);
        // 进行OAuth响应构建
        OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_MOVED_TEMPORARILY);
        // 设置授权码
        builder.setCode(code.getCode());
        String state = oauthRequest.getParam(OAuth.OAUTH_STATE);
        if (!StringUtils.isEmpty(state)) {
            builder.setParam(OAuth.OAUTH_STATE, state);
        }
        try {
            // 构建响应
            OAuthResponse response = builder.location(code.getRedirectUri()).buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity<>(headers, HttpStatus.valueOf(response.getResponseStatus()));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new MyRuntimeException("错误:构建code返回异常");
        }
    }
}
