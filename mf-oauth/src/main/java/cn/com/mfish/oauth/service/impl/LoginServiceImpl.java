package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.CaptchaException;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.oauth.entity.OAuthClient;
import cn.com.mfish.oauth.oltu.common.OAuth;
import cn.com.mfish.oauth.security.MfishAuthenticationToken;
import cn.com.mfish.oauth.service.LoginService;
import cn.com.mfish.oauth.validator.GetCodeValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description: 登录服务实现类，处理用户登录认证、短信验证码管理、登录重试限制等逻辑
 * @author: mfish
 * @date: 2020/2/15 16:10
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    private static final int DEFAULT_SESSION_TIMEOUT_SECONDS = 30 * 60;
    private static final int REMEMBER_ME_SESSION_TIMEOUT_SECONDS = 7 * 24 * 3600;

    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    GetCodeValidator getCodeValidator;
    @Resource
    AuthenticationManager authenticationManager;
    @Resource
    private SecurityContextRepository securityContextRepository;
    @Override
    public boolean getLogin(Model model, HttpServletRequest request) {
        //校验当前请求code相关参数是否正确
        validateCode(model, request);
        //get的登录请求，始终返回登录页
        return false;
    }

    @Override
    public boolean postLogin(Model model, HttpServletRequest request) {
        String captchaEx = request.getHeader(RPCConstants.REQ_CHECK_CAPTCHA_EXCEPTION);
        if (!StringUtils.isEmpty(captchaEx)) {
            model.addAttribute(SerConstant.ERROR_MSG, CaptchaException.Info.getExceptionInfo(captchaEx).toString());
            return false;
        }
        if (!validateCode(model, request)) {
            return false;
        }
        return login(model, request);
    }

    /**
     * 请求code参数校验
     * <p>
     * 本方法主要用于校验授权码（code）的有效性通过HTTP请求参数进行校验如果校验失败，
     * 会将错误信息添加到模型中并返回false否则，返回true表示校验成功
     *
     * @param model   用于存储属性值，在发生错误时用于存储错误信息
     * @param request HTTP请求对象，用于获取请求参数
     * @return boolean 校验是否成功
     */
    private boolean validateCode(Model model, HttpServletRequest request) {
        Result<OAuthClient> result = getCodeValidator.validateClient(request, null);
        if (!result.isSuccess()) {
            model.addAttribute(SerConstant.ERROR_MSG, result.getMsg());
            return false;
        }
        return true;
    }

    /**
     * web请求登录 构建model返回值
     *
     * @param model   用于存储属性值，在发生错误时用于存储错误信息
     * @param request HTTP请求对象，用于获取请求参数
     * @return boolean 校验是否成功
     */
    public boolean login(Model model, HttpServletRequest request) {
        Result<String> result = login(request);
        for (Map.Entry<String, String> entry : result.getParam().entrySet()) {
            model.addAttribute(entry.getKey(), entry.getValue());
        }
        return result.isSuccess();
    }

    /**
     * 登录用户验证逻辑
     *
     * @param request HTTP请求对象，用于获取请求参数
     * @return 返回登陆结果
     */
    @Override
    public Result<String> login(HttpServletRequest request) {
        String username = request.getParameter(OAuth.OAUTH_USERNAME);
        String password = request.getParameter(OAuth.OAUTH_PASSWORD);
        SerConstant.LoginType loginType = SerConstant.LoginType.getLoginType(request.getParameter(SerConstant.LOGIN_TYPE));
        String rememberMe = request.getParameter(SerConstant.REMEMBER_ME);
        String clientId = request.getParameter(SerConstant.CLIENT_ID);
        return login(username, password, loginType, clientId, rememberMe);

    }


    /**
     * 登录用户验证逻辑
     *
     * @param username  账号
     * @param password  密码
     * @param loginType 登陆类型
     * @return 返回登陆结果
     */
    public Result<String> login(String username, String password, SerConstant.LoginType loginType, String clientId, String rememberMe) {
        boolean remember = false;
        if (!StringUtils.isEmpty(rememberMe)) {
            remember = Boolean.parseBoolean(rememberMe);
        }
        Result<String> result = new Result<>();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            result.setSuccess(false).setMsg(SerConstant.INVALID_USER_SECRET_DESCRIPTION)
                    .getParam().put(SerConstant.ERROR_MSG, SerConstant.INVALID_USER_SECRET_DESCRIPTION);
            return result;
        }
        MfishAuthenticationToken authToken = new MfishAuthenticationToken(username, password, loginType, clientId);
        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            HttpServletRequest req = Objects.requireNonNull(ServletUtils.getRequest());
            req.getSession().setMaxInactiveInterval(remember
                    ? REMEMBER_ME_SESSION_TIMEOUT_SECONDS
                    : DEFAULT_SESSION_TIMEOUT_SECONDS);
            // 关键：保存到 Session
            securityContextRepository.saveContext(
                    context,
                    req,
                    Objects.requireNonNull(ServletUtils.getResponse())
            );
            return result;
        } catch (BadCredentialsException ex) {
            //错误凭证错误信息
            result.setSuccess(false).setMsg(ex.getMessage()).getParam().put(SerConstant.ERROR_MSG, ex.getMessage());
            log.info("用户:{}登录客户端:{}凭证错误{}", username, clientId, ex.getMessage(), ex);
            return result;
        } catch (Exception ex) {
            //其他异常错误信息
            result.setSuccess(false).setMsg(ex.getMessage()).getParam().put(SerConstant.ERROR_MSG, ex.getMessage());
            log.info("用户:{}登录客户端:{}异常{}", username, clientId, ex.getMessage(), ex);
            return result;
        } finally {
            result.setData(authToken.getUserInfo() != null ? authToken.getUserInfo().getId() : null);
            result.getParam().put(OAuth.OAUTH_USERNAME, username);
            result.getParam().put(SerConstant.LOGIN_TYPE, loginType.toString());
        }
    }

    @Override
    public void sendMsg(String phone, String msg) {
        //TODO 根据具体短信网关实现
    }

    @Override
    public void saveSmsCode(String phone, String code) {
        redisTemplate.opsForValue().set(RedisPrefix.buildSMSCodeKey(phone), code, 5, TimeUnit.MINUTES);
    }

    @Override
    public void saveSmsCodeTime(String phone) {
        redisTemplate.opsForValue().set(RedisPrefix.buildSMSCodeTimeKey(phone), "", 1, TimeUnit.MINUTES);
    }

    @Override
    public void delSmsCodeTime(String phone) {
        redisTemplate.delete(RedisPrefix.buildSMSCodeTimeKey(phone));
    }

    @Override
    public Long getSmsCodeTime(String phone) {
        return redisTemplate.getExpire(RedisPrefix.buildSMSCodeTimeKey(phone));
    }

    @Override
    public void sessionKeyTempCache(String sessionKey, String openId) {
        redisTemplate.opsForValue().set(RedisPrefix.buildSessionKey(sessionKey), openId, 5, TimeUnit.MINUTES);
    }
}
