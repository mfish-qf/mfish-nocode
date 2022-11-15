package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.OAuthClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/17 15:26
 */
@Component
public class ClientSecretExistValidator extends AbstractClientValidator {
    @Override
    public Result<OAuthClient> validate(HttpServletRequest request, Result<OAuthClient> result) {
        Result<OAuthClient> result1 = getOAuthClient(request, result);
        if (!result1.isSuccess()) {
            return result1;
        }
        String secret = request.getParameter(OAuth.OAUTH_CLIENT_SECRET);
        if (!StringUtils.isEmpty(secret) && secret.equals(result1.getData().getClientSecret())) {
            return result1;
        }
        return result1.setSuccess(false).setMsg("错误:客户端密钥错误!");
    }
}
