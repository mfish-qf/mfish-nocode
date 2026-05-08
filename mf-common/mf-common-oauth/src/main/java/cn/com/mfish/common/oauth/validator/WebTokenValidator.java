package cn.com.mfish.common.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.service.impl.WebTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @description: Web端Token校验器
 * @author: mfish
 * @date: 2020/2/17 19:06
 */
@Component
public class WebTokenValidator extends AbstractTokenValidator<RedisAccessToken> {

    /**
     * 构造方法，注入WebToken服务
     *
     * @param webTokenService Web端Token服务实现
     */
    public WebTokenValidator(@Autowired WebTokenServiceImpl webTokenService) {
        super(webTokenService);
    }

    /**
     * 校验ServerHttpRequest请求中的Token
     *
     * @param request 响应式请求对象
     * @return 校验结果
     */
    public Result<RedisAccessToken> validate(ServerHttpRequest request) {
        return validateT(request);
    }

    /**
     * 校验HttpServletRequest请求中的Token
     *
     * @param request Servlet请求对象
     * @return 校验结果
     */
    public Result<RedisAccessToken> validate(HttpServletRequest request) {
        return validateT(request);
    }

    @Override
    public Result<RedisAccessToken> validate(HttpServletRequest request, Result<RedisAccessToken> result) {
        return validateT(request);
    }

}
