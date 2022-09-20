package cn.com.mfish.oauth.validator;

import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.entity.AuthorizationCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiufeng
 * @date 2020/2/17 14:43
 */
@Component
public class Code2TokenValidator extends MultipleValidator {
    List<Class<? extends IBaseValidator<AuthorizationCode>>> validateCodeList = new ArrayList<>();

    public Code2TokenValidator() {
        this.validateClientList.add(ClientIdExistValidator.class);
        this.validateClientList.add(ClientSecretExistValidator.class);
        this.validateClientList.add(GrantTypeExistValidator.class);
        this.validateClientList.add(RedirectUriExistValidator.class);
        this.validateCodeList.add(ClientIdEqualValidator.class);
        this.validateCodeList.add(UriEqualValidator.class);
    }

    /**
     * code参数相关多个校验组合
     *
     * @param request
     * @param result
     * @return
     */
    public CheckWithResult<AuthorizationCode> validateCode(HttpServletRequest request, CheckWithResult<AuthorizationCode> result) {
        return validate(request, result, validateCodeList);
    }

    /**
     * 校验code换token两次传入的clientId是否一致
     */
    @Component
    public class ClientIdEqualValidator extends AbstractCodeValidator {
        @Override
        public CheckWithResult<AuthorizationCode> validate(HttpServletRequest request, CheckWithResult<AuthorizationCode> result) {
            CheckWithResult<AuthorizationCode> result1 = getAuthCode(request, result);
            if (!result1.isSuccess()) {
                return result1;
            }
            String clientId = request.getParameter(OAuth.OAUTH_CLIENT_ID);
            if(!StringUtils.isEmpty(clientId) && clientId.equals(result1.getResult().getClientId())){
                return result1;
            }
            return result1.setSuccess(false).setMsg("错误:获取code和token两次传入的clientId不一致");
        }
    }

    /**
     * 校验code换token两次传入的uri是否一致
     */
    @Component
    public class UriEqualValidator extends AbstractCodeValidator {
        @Override
        public CheckWithResult<AuthorizationCode> validate(HttpServletRequest request, CheckWithResult<AuthorizationCode> result) {
            CheckWithResult<AuthorizationCode> result1 = getAuthCode(request, result);
            if (!result1.isSuccess()) {
                return result1;
            }
            String uri = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
            if(!StringUtils.isEmpty(uri) && uri.equals(result1.getResult().getRedirectUri())){
                return result1;
            }
            return result1.setSuccess(false).setMsg("错误:获取code和token两次传入的uri不一致");
        }
    }
}
