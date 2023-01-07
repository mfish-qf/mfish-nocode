package cn.com.mfish.common.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.service.impl.WebTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: mfish
 * @date: 2020/2/17 19:06
 */
@Component
public class AccessTokenValidator extends AbstractTokenValidator<RedisAccessToken> {

    public AccessTokenValidator(@Autowired WebTokenServiceImpl webTokenService) {
        super(webTokenService);
    }

    public Result<RedisAccessToken> validate(ServerHttpRequest request) {
        return validateT(request, null);
    }

    public Result<RedisAccessToken> validate(HttpServletRequest request) {
        return validateT(request, null);
    }

    @Override
    public Result<RedisAccessToken> validate(HttpServletRequest request, Result<RedisAccessToken> result) {
        return validateT(request, result);
    }

}
