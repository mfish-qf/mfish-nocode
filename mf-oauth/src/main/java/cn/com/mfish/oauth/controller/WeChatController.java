package cn.com.mfish.oauth.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.com.mfish.common.core.enums.DeviceType;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.AccessToken;
import cn.com.mfish.common.oauth.entity.WeChatToken;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.common.oauth.validator.WeChatTokenValidator;
import cn.com.mfish.oauth.cache.redis.UserTokenCache;
import cn.com.mfish.oauth.oltu.common.OAuth;
import cn.com.mfish.oauth.service.LoginService;
import cn.com.mfish.oauth.service.WeChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: mfish
 * @description: 小程序请求接口
 * @date: 2021/12/14 9:35
 */
@Tag(name = "小程序接口")
@RestController
@RequestMapping("/wx")
@Slf4j
public class WeChatController {
    @Resource
    WeChatService weChatService;
    @Resource
    LoginService loginService;
    @Resource
    SsoUserService ssoUserService;
    @Resource
    WeChatTokenValidator weChatTokenValidator;
    @Resource
    WxMaService wxMaService;
    @Resource
    UserTokenCache userTokenCache;

    @GetMapping("/bind/check")
    @Operation(summary = "检查微信是否绑定 如果已绑定返回accessToken")
    @Parameters({
            @Parameter(name = SerConstant.QR_CODE, description = "微信认证code", required = true)
    })
    public AccessToken checkBind(String code) {
        WxMaJscode2SessionResult session = getSession(code);
        String openid = session.getOpenid();
        String userId = weChatService.getUserIdByOpenId(openid);
        if (!StringUtils.isEmpty(userId)) {
            AccessToken token = new AccessToken(weChatService.buildWeChatToken(openid, session.getSessionKey(), userId));
            userTokenCache.addUserTokenCache(DeviceType.WX, openid, userId, token.getAccess_token());
            return token;
        }
        log.error("微信:" + openid + "未绑定成功");
        throw new OAuthValidateException("错误:微信未绑定");
    }

    /**
     * 获取微信session
     *
     * @param code 微信认证code
     * @return
     */
    private WxMaJscode2SessionResult getSession(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new OAuthValidateException("错误:认证code不允许为空");
        }
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            if (session == null) {
                throw new OAuthValidateException("错误:未获取到用户session");
            }
            String openid = session.getOpenid();
            if (StringUtils.isEmpty(openid)) {
                throw new OAuthValidateException("错误:未获取openid");
            }
            if (StringUtils.isEmpty(session.getSessionKey())) {
                throw new OAuthValidateException("错误:未获取sessionKey");
            }
            return session;
        } catch (WxErrorException e) {
            throw new OAuthValidateException(e.getMessage());
        }
    }

    @PostMapping("/bind")
    @Operation(summary = "绑定账号和微信")
    @Parameters({
            @Parameter(name = OAuth.OAUTH_CODE, description = "微信认证code", required = true),
            @Parameter(name = OAuth.OAUTH_USERNAME, description = "账号，手机，email grant_type=password时必须", required = true),
            @Parameter(name = OAuth.OAUTH_PASSWORD, description = "密码 grant_type=password时必须", required = true)
    })
    @Log(title = "绑定微信", operateType = OperateType.LOGIN)
    public AccessToken bindWeChat(HttpServletRequest request) {
        String code = request.getParameter(OAuth.OAUTH_CODE);
        WxMaJscode2SessionResult session = getSession(code);
        String openid = session.getOpenid();
        if (StringUtils.isEmpty(openid)) {
            throw new OAuthValidateException("错误:未获取openid");
        }
        Result<String> loginResult = loginService.login(request);
        if (loginResult.isSuccess() && weChatService.bindWeChat(openid, loginResult.getData())) {
            WeChatToken weChatToken = weChatService.buildWeChatToken(openid, session.getSessionKey(), loginResult.getData());
            AccessToken token = weChatService.convertToken(weChatToken);
            userTokenCache.addUserTokenCache(DeviceType.WX, openid, weChatToken.getUserId(), token.getAccess_token());
            return token;
        }
        throw new OAuthValidateException("错误:微信账号绑定失败");
    }


    @Operation(summary = "获取用户信息")
    @GetMapping("/userInfo")
    @Parameters({
            @Parameter(name = OAuth.HeaderType.AUTHORIZATION, description = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", in = ParameterIn.HEADER),
            @Parameter(name = OAuth.OAUTH_ACCESS_TOKEN, description = "token值 header和access_token参数两种方式任意一种即可")
    })
    @Log(title = "微信获取用户信息", operateType = OperateType.QUERY)
    public Result<UserInfo> getUserInfo(HttpServletRequest request) {
        Result<WeChatToken> result = weChatTokenValidator.validate(request);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return Result.ok(ssoUserService.getUserInfo(result.getData().getUserId()));
    }

    @PostMapping("/login/pwd")
    @Operation(summary = "通过密码绑定账号")
    @Parameters({
            @Parameter(name = SerConstant.QR_CODE, description = "微信认证code", required = true),
            @Parameter(name = SerConstant.NICKNAME, description = "微信昵称"),
            @Parameter(name = SerConstant.PHONE, description = "手机", required = true),
            @Parameter(name = SerConstant.PASSWORD, description = "短信验证码", required = true)
    })
    @Log(title = "微信登录", operateType = OperateType.LOGIN)
    public AccessToken pwdLogin(String code, String nickname, String phone, String password) {
        WxMaJscode2SessionResult session = getSession(code);
        String openid = session.getOpenid();
        String userId = weChatService.getUserIdByOpenId(openid);
        if (!StringUtils.isEmpty(userId)) {
            return new AccessToken(weChatService
                    .buildWeChatToken(openid, session.getSessionKey(), userId));
        }
        //TODO 此处clientId暂时写死后期可能需要修改
        Result<String> loginResult = loginService.login(phone, password, SerConstant.LoginType.短信登录, "system", "false");
        if (StringUtils.isEmpty(nickname)) {
            nickname = Utils.phoneMasking(phone);
        }
        if (loginResult.isSuccess() && weChatService.bindWeChat(openid, loginResult.getData(), nickname)) {
            AccessToken token = new AccessToken(weChatService
                    .buildWeChatToken(openid, session.getSessionKey(), loginResult.getData()));
            userTokenCache.addUserTokenCache(DeviceType.WX, openid, loginResult.getData(), token.getAccess_token());
            return token;
        }
        throw new OAuthValidateException("错误:绑定微信失败");
    }


    @Operation(summary = "获取sessionKey 与手机登录联合使用")
    @GetMapping("/session")
    @Parameters({
            @Parameter(name = SerConstant.QR_CODE, description = "微信认证code", required = true)
    })
    public Map<String, String> getSessionKey(String code) {
        WxMaJscode2SessionResult session = getSession(code);
        loginService.sessionKeyTempCache(session.getSessionKey(), session.getOpenid());
        Map<String, String> map = new HashMap<>();
        map.put("sessionKey", session.getSessionKey());
        return map;
    }

    @Operation(summary = "通过手机号绑定账号 先调用/sessionKey方法")
    @PostMapping("/login/phone")
    @Parameters({
            @Parameter(name = "sessionKey", description = "微信登录返回的sessionKey", required = true),
            @Parameter(name = "nickname", description = "微信昵称"),
            @Parameter(name = SerConstant.QR_CODE, description = "微信认证code", required = true)
    })
    public AccessToken phoneLogin(String sessionKey, String nickname, String code) throws WxErrorException {
        String openid = loginService.getOpenIdBySessionKey(sessionKey);
        if (StringUtils.isEmpty(openid)) {
            log.error("sessionKey:" + sessionKey + ",手机号绑定失败");
            throw new OAuthValidateException("错误:sessionKey有误");
        }
        String userId = weChatService.getUserIdByOpenId(openid);
        if (!StringUtils.isEmpty(userId)) {
            return new AccessToken(weChatService.buildWeChatToken(openid, sessionKey, userId));
        }
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNumber(code);
        String phone = phoneNoInfo.getPhoneNumber();
        //TODO 此处clientId暂时写死后期可能需要修改
        Result<String> loginResult = loginService.login(phone, sessionKey, SerConstant.LoginType.微信登录, "system", "false");
        if (StringUtils.isEmpty(nickname)) {
            nickname = Utils.phoneMasking(phone);
        }
        if (loginResult.isSuccess() && weChatService.bindWeChat(openid, loginResult.getData(), nickname)) {
            AccessToken token = new AccessToken(weChatService.buildWeChatToken(openid, sessionKey, loginResult.getData()));
            userTokenCache.addUserTokenCache(DeviceType.WX, openid, loginResult.getData(), token.getAccess_token());
            return token;
        }
        throw new OAuthValidateException("错误:绑定微信失败");
    }
}