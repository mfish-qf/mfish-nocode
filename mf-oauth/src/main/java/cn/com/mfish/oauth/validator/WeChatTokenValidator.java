package cn.com.mfish.oauth.validator;


import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.common.core.utils.AuthUtils;
import cn.com.mfish.oauth.entity.WeChatToken;
import cn.com.mfish.oauth.service.WeChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ：qiufeng
 * @description：微信请求token校验
 * @date ：2021/12/14 9:33
 */
@Component
public class WeChatTokenValidator implements IBaseValidator<WeChatToken> {
    @Resource
    WeChatService weChatService;

    public CheckWithResult<WeChatToken> validate(HttpServletRequest request) {
        return validate(request, null);
    }

    @Override
    public CheckWithResult<WeChatToken> validate(HttpServletRequest request, CheckWithResult<WeChatToken> result) {
        WeChatToken weChatToken;
        if (result == null || result.getResult() == null) {
            result = new CheckWithResult<>();
            String accessToken = AuthUtils.getAccessToken(request);
            if (StringUtils.isEmpty(accessToken)) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            weChatToken = weChatService.getToken(accessToken);
            if (weChatToken == null) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            return result.setResult(weChatToken);
        }
        return result;
    }
}
