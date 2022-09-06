package cn.com.mfish.oauth.validator;

import cn.com.mfish.oauth.service.impl.WebTokenServiceImpl;
import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.common.core.utils.AuthUtils;
import cn.com.mfish.oauth.model.RedisAccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/17 19:06
 */
@Component
public class AccessTokenValidator implements IBaseValidator<RedisAccessToken> {
    @Resource
    WebTokenServiceImpl webTokenService;

    @Override
    public CheckWithResult<RedisAccessToken> validate(HttpServletRequest request, CheckWithResult<RedisAccessToken> result) {
        RedisAccessToken token;
        if (result == null || result.getResult() == null) {
            result = new CheckWithResult<>();
            String accessToken = AuthUtils.getAccessToken(request);
            if (StringUtils.isEmpty(accessToken)) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            token = webTokenService.getToken(accessToken);
            if (token == null) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            return result.setResult(token);
        }
        return result;
    }
}
