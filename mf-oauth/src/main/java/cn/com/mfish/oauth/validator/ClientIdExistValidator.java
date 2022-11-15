package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.OAuthClient;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/13 13:58
 */
@Component
public class ClientIdExistValidator extends AbstractClientValidator {

    @Override
    public Result<OAuthClient> validate(HttpServletRequest request, Result<OAuthClient> result) {
        return getOAuthClient(request, result);
    }
}
