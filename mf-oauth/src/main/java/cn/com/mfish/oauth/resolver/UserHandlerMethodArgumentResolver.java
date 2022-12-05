package cn.com.mfish.oauth.resolver;

import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.utils.AuthUtils;
import cn.com.mfish.oauth.aspect.CurUserId;
import cn.com.mfish.oauth.entity.RedisAccessToken;
import cn.com.mfish.oauth.exception.UserValidateException;
import cn.com.mfish.oauth.service.impl.WebTokenServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;

/**
 * @author ：qiufeng
 * @description：获取用户信息参数方法
 * @date ：2021/12/9 11:23
 */
public class UserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Resource
    WebTokenServiceImpl webTokenService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(String.class)
                && parameter.hasParameterAnnotation(CurUserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = AuthUtils.getAccessToken(webRequest);
        if (StringUtils.isEmpty(token)) {
            throw new UserValidateException("token不允许为空");
        }
        RedisAccessToken redisAccessToken = webTokenService.getToken(token);
        if (redisAccessToken == null) {
            throw new OAuthValidateException("错误:token不存在或已失效");
        }
        String userId = redisAccessToken.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new UserValidateException("错误:用户校验失败!");
        }
        return userId;
    }
}
