package cn.com.mfish.oauth.validator;


import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.WeChatToken;
import cn.com.mfish.oauth.service.WeChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: mfish
 * @description: 微信请求token校验
 * @date: 2021/12/14 9:33
 */
@Component
public class WeChatTokenValidator implements IBaseValidator<WeChatToken> {
    @Resource
    WeChatService weChatService;

    public Result<WeChatToken> validate(HttpServletRequest request) {
        return validate(request, null);
    }

    @Override
    public Result<WeChatToken> validate(HttpServletRequest request, Result<WeChatToken> result) {
        WeChatToken weChatToken;
        if (result == null || result.getData() == null) {
            result = new Result<>();
            String accessToken = AuthInfoUtils.getAccessToken(request);
            if (StringUtils.isEmpty(accessToken)) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            weChatToken = weChatService.getToken(accessToken);
            if (weChatToken == null) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            return result.setData(weChatToken);
        }
        return result;
    }
}
