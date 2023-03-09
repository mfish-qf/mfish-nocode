package cn.com.mfish.common.oauth.validator;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.SerConstant;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description: 统一token校验
 * @author: mfish
 * @date: 2023/1/29 23:28
 */
@Component
public class TokenValidator {
    @Resource
    private WebTokenValidator webTokenValidator;
    @Resource
    private WeChatTokenValidator weChatTokenValidator;

    public <R> Result<?> validator(R request) {
        String accessToken = AuthInfoUtils.getAccessToken(request);
        if (StringUtils.isEmpty(accessToken)) {
            return Result.fail("错误:令牌token不允许为空");
        }
        Result<?> result;
        if (accessToken.startsWith(SerConstant.WX_PREFIX)) {
            result = weChatTokenValidator.validate(accessToken);
        } else {
            result = webTokenValidator.validate(accessToken);
        }
        return result;
    }
}
