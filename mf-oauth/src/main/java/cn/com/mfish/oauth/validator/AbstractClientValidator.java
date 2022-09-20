package cn.com.mfish.oauth.validator;

import cn.com.mfish.oauth.cache.temp.ClientTempCache;
import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.entity.OAuthClient;
import org.apache.oltu.oauth2.common.OAuth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/16 18:10
 */
public abstract class AbstractClientValidator implements IBaseValidator<OAuthClient> {

    @Resource
    ClientTempCache clientTempCache;

    /**
     * 获取客户端信息
     * 如果参数中已传入客户端信息直接返回，如果未传入从redis或数据库中获取
     *
     * @param request
     * @param result
     * @return
     */
    public CheckWithResult<OAuthClient> getOAuthClient(HttpServletRequest request, CheckWithResult<OAuthClient> result) {
        OAuthClient client;
        if (result == null || result.getResult() == null) {
            result = new CheckWithResult<>();
            String clientId = request.getParameter(OAuth.OAUTH_CLIENT_ID);
            client = clientTempCache.getCacheInfo(clientId);
            if (client == null) {
                return result.setSuccess(false).setMsg("错误:校验客户端ID失败!");
            }
            result.setResult(client);
        }
        return result;
    }
}
