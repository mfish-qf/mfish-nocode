package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.OAuthClient;
import cn.com.mfish.oauth.oltu.common.OAuth;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @description: 客户端密钥校验器，验证请求中的客户端密钥是否正确
 * @author: mfish
 * @date: 2020/2/17 15:26
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
