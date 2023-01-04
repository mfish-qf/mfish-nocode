package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.service.impl.WebTokenServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: mfish
 * @date: 2020/2/18 18:53
 */
public abstract class AbstractRefreshTokenValidator implements IBaseValidator<RedisAccessToken> {
    @Resource
    WebTokenServiceImpl webTokenService;

    public Result<RedisAccessToken> getRefreshToken(HttpServletRequest request, Result<RedisAccessToken> result) {
        RedisAccessToken token;
        if (result == null || result.getData() == null) {
            result = new Result<>();
            String refreshToken = request.getParameter(OAuth.OAUTH_REFRESH_TOKEN);
            if (StringUtils.isEmpty(refreshToken)) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            token = webTokenService.getRefreshToken(refreshToken);
            if (token == null) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            return result.setData(token);
        }
        return result;
    }
}
