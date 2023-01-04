package cn.com.mfish.common.web.aspect;

import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.annotation.GlobalException;
import cn.com.mfish.common.web.annotation.InnerUser;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.service.impl.WebTokenServiceImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author: mfish
 * @description：内部用户校验切面
 * @date: 2021/12/3 11:28
 */
@Aspect
@Component
@GlobalException
public class InnerUserAspect {
    @Resource
    WebTokenServiceImpl webTokenService;

    @Around("@annotation(innerUser)")
    public Object innerAround(ProceedingJoinPoint point, InnerUser innerUser) throws Throwable {
        HttpServletRequest request = ServletUtils.getRequest();
        String source = request.getHeader(CredentialConstants.REQ_ORIGIN);
        // 内部请求验证
        if (CredentialConstants.INNER.equals(source) && !innerUser.validateUser()) {
            return point.proceed();
        }
        String token = AuthInfoUtils.getAccessToken(request);
        if (StringUtils.isEmpty(token)) {
            throw new OAuthValidateException("错误:token不允许为空");
        }
        RedisAccessToken redisAccessToken = webTokenService.getToken(token);
        if (redisAccessToken == null) {
            throw new OAuthValidateException("错误:token不存在或已过期");
        }
        return point.proceed();
    }
}
