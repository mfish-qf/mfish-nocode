package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.AuthorizationCode;
import cn.com.mfish.common.oauth.validator.IBaseValidator;
import cn.com.mfish.oauth.oltu.common.OAuth;
import cn.com.mfish.oauth.service.OAuth2Service;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

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
     * @param request HTTP请求对象，用于获取授权码
     * @param result 前一次校验结果对象，如果为null，则需要在方法内部新建
     * @return 返回封装了AuthorizationCode的Result对象，如果授权码无效，则返回错误信息
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
