package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.service.impl.WebTokenServiceImpl;
import cn.com.mfish.common.oauth.validator.IBaseValidator;
import cn.com.mfish.oauth.oltu.common.OAuth;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 刷新令牌校验器基类，提供从缓存中获取刷新令牌信息的通用方法
 * @author: mfish
 * @date: 2020/2/18 18:53
 */
public abstract class AbstractRefreshTokenValidator implements IBaseValidator<RedisAccessToken> {
    @Resource
    WebTokenServiceImpl webTokenService;

    /**
     * 获取刷新令牌信息
     * 如果参数中已传入令牌信息直接返回，如果未传入从缓存中获取
     *
     * @param request HTTP请求对象
     * @param result  上次校验结果
     * @return 返回刷新令牌信息
     */
    public Result<RedisAccessToken> getRefreshToken(HttpServletRequest request, Result<RedisAccessToken> result) {
        RedisAccessToken token;
        if (result == null || result.getData() == null) {
            result = new Result<>();
            String refreshToken = request.getParameter(OAuth.OAUTH_REFRESH_TOKEN);
            if (StringUtils.isEmpty(refreshToken)) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            token = webTokenService.getRefreshToken(refreshToken);
            if (token == null) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            return result.setData(token);
        }
        return result;
    }
}
