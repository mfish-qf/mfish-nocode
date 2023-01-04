package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.service.impl.WebTokenServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: mfish
 * @date: 2020/2/17 19:06
 */
@Component
public class AccessTokenValidator implements IBaseValidator<RedisAccessToken> {
    @Resource
    WebTokenServiceImpl webTokenService;

    @Override
    public Result<RedisAccessToken> validate(HttpServletRequest request, Result<RedisAccessToken> result) {
        RedisAccessToken token;
        if (result == null || result.getData() == null) {
            result = new Result<>();
            String accessToken = AuthInfoUtils.getAccessToken(request);
            if (StringUtils.isEmpty(accessToken)) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            token = webTokenService.getToken(accessToken);
            if (token == null) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            return result.setData(token);
        }
        return result;
    }
}
