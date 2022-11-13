package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.OAuthClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/17 14:44
 */
@Component
public class GrantTypeExistValidator extends AbstractClientValidator {
    @Override
    public Result<OAuthClient> validate(HttpServletRequest request, Result<OAuthClient> result) {
        Result<OAuthClient> result1 = getOAuthClient(request, result);
        if (!result1.isSuccess()) {
            return result1;
        }
        String grantType = request.getParameter(OAuth.OAUTH_GRANT_TYPE);
        if (StringUtils.isEmpty(grantType)) {
            return result1.setSuccess(false).setMsg("错误:grant_type为空");
        }
        if (result1.getData().getAuthorizedGrantTypes().indexOf(grantType) < 0) {
            return result1.setSuccess(false).setMsg("错误:该客户端不支持" + grantType + "请求方式！");
        }
        return result1;
    }
}
