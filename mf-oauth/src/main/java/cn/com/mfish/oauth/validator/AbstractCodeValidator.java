package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.AuthorizationCode;
import cn.com.mfish.oauth.service.OAuth2Service;
import org.apache.oltu.oauth2.common.OAuth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: mfish
 * @date: 2020/2/17 15:39
 */
public abstract class AbstractCodeValidator implements IBaseValidator<AuthorizationCode> {
    @Resource
    OAuth2Service oAuth2Service;

    /**
     * 获取authCode信息
     *
     * @param request
     * @param result
     * @return
     */
    public Result<AuthorizationCode> getAuthCode(HttpServletRequest request, Result<AuthorizationCode> result) {
        AuthorizationCode authCode;
        if (result == null || result.getData() == null) {
            result = new Result<>();
            String code = request.getParameter(OAuth.OAUTH_CODE);
            authCode = oAuth2Service.getCode(code);
            if (authCode == null) {
                return result.setSuccess(false).setMsg("错误:code错误或失效!");
            }
            result.setData(authCode);
        }
        return result;
    }
}
