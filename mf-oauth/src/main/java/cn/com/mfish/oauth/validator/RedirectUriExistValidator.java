package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.OAuthClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/16 17:57
 */
@Component
public class RedirectUriExistValidator extends AbstractClientValidator {

    @Override
    public Result<OAuthClient> validate(HttpServletRequest request, Result<OAuthClient> result) {
        Result<OAuthClient> result1 = getOAuthClient(request, result);
        if (!result1.isSuccess()) {
            return result1;
        }
        String uri = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
        if (checkUri(uri, result1.getData())) {
            return result1;
        }
        return result1.setSuccess(false).setMsg("错误:回调地址不正确!");
    }

    /**
     * 模糊匹配Uri 满足正则和多个链接逗号隔开的均允许访问
     * @param call_back_url
     * @param client
     * @return
     */
    private boolean checkUri(String call_back_url, OAuthClient client) {
        if (StringUtils.isEmpty(call_back_url) || client == null
                || StringUtils.isEmpty(client.getWebServerRedirectUri())) {
            return false;
        }
        String[] urls = client.getWebServerRedirectUri().split(",");
        for (String url : urls) {
            url = url.replace("?", "\\?");
            String s = call_back_url.trim().replaceAll(url, "");
            if (StringUtils.isEmpty(s)) {
                return true;
            }
        }
        return false;
    }
}
