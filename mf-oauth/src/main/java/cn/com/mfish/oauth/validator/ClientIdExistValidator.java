package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.OAuthClient;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @description: 客户端ID存在性校验器，验证请求中的客户端ID是否有效
 * @author: mfish
 * @date: 2020/2/13 13:58
 */
@Component
public class ClientIdExistValidator extends AbstractClientValidator {

    @Override
    public Result<OAuthClient> validate(HttpServletRequest request, Result<OAuthClient> result) {
        return getOAuthClient(request, result);
    }
}
