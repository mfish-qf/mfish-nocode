package cn.com.mfish.oauth.filter;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.entity.WeChatToken;
import cn.com.mfish.common.oauth.validator.TokenValidator;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @description: token过滤器
 * @author: mfish
 * @date: 2024/1/30
 */
public class TokenFilter extends UserFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        TokenValidator tokenValidator = SpringBeanFactory.getBean(TokenValidator.class);
        Result<?> result = tokenValidator.validator(request);
        if (!result.isSuccess()) {
            return false;
        }
        //验证通过后请求中补充用户相关信息，单实例服务用户属性放在attribute中传递
        if (result.getData() instanceof WeChatToken) {
            WeChatToken token = (WeChatToken) result.getData();
            request.setAttribute(RPCConstants.REQ_ACCOUNT, token.getAccount());
            request.setAttribute(RPCConstants.REQ_USER_ID, token.getUserId());
            request.setAttribute(RPCConstants.REQ_TENANT_ID, token.getTenantId());
        } else {
            RedisAccessToken token = (RedisAccessToken) result.getData();
            request.setAttribute(RPCConstants.REQ_ACCOUNT, token.getAccount());
            request.setAttribute(RPCConstants.REQ_USER_ID, token.getUserId());
            request.setAttribute(RPCConstants.REQ_TENANT_ID, token.getTenantId());
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

}
