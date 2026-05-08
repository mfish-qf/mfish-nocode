package cn.com.mfish.common.oauth.validator;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.SerConstant;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

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

    /**
     * 统一token校验入口，根据token前缀自动选择Web或微信校验器
     *
     * @param request 请求对象
     * @param <R>     请求类型
     * @return 校验结果
     */
    public <R> Result<?> validator(R request) {
        String accessToken = AuthInfoUtils.getAccessToken(request);
        Result<?> result;
        if (!StringUtils.isEmpty(accessToken) && accessToken.startsWith(SerConstant.WX_PREFIX)) {
            result = weChatTokenValidator.validate(accessToken);
        } else {
            result = webTokenValidator.validate(accessToken);
        }
        return result;
    }
}
