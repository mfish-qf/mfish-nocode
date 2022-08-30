package cn.com.mfish.oauth.validator;

import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.model.OAuthClient;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/16 18:57
 */
@Component
public class AllowCodeValidator extends AbstractClientValidator {
    @Override
    public CheckWithResult<OAuthClient> validate(HttpServletRequest request, CheckWithResult<OAuthClient> result) {
        CheckWithResult<OAuthClient> result1 = getOAuthClient(request, result);
        if (!result1.isSuccess()) {
            return result1;
        }
        if(result1.getResult().getAuthorizedGrantTypes().indexOf("authorization_code")<0){
            return result1.setSuccess(false).setMsg("错误:该客户端不支持code请求方式！");
        }
        return result1;
    }
}
