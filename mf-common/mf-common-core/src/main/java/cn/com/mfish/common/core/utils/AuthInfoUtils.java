package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.http.WebRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: mfish
 * @description: 认证相关信息，此类中方法不支持异步调用获取
 * @date: 2020/2/12 13:47
 */
@Slf4j
public class AuthInfoUtils {
    /**
     * 从请求中获取token值
     * token通过access_token=****直接赋值
     * 或者token放到head中 以Authorization=Bearer******方式传入
     *
     * @param t 请求类型
     * @return
     */
    public static <T> String getAccessToken(T t) {
        // 头部的Authorization值以Bearer开头
        String auth = WebRequest.getHeader(t, Constants.AUTHENTICATION);
        String accessToken = null;
        if (StringUtils.isNotEmpty(auth) && auth.startsWith(Constants.OAUTH_HEADER_NAME)) {
            accessToken = auth.replaceFirst(Constants.OAUTH_HEADER_NAME, StringUtils.EMPTY).trim();
        }
        // 请求参数中包含access_token参数
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = WebRequest.getParameter(t, Constants.ACCESS_TOKEN);
        }
        return accessToken;
    }

    /**
     * 获取当前accessToken
     *
     * @return
     */
    public static String getAccessToken() {
        return getAccessToken(ServletUtils.getRequest());
    }

    /**
     * 获取当前用户ID(该方法要在主线程运行)
     *
     * @return
     */
    public static String getCurrentUserId() {
        String userId = ServletUtils.getHeader(RPCConstants.REQ_USER_ID);
        return StringUtils.isEmpty(userId) ? null : userId;
    }

    /**
     * 获取当前帐号(该方法要在主线程运行)
     *
     * @return
     */
    public static String getCurrentAccount() {
        String account = ServletUtils.getHeader(RPCConstants.REQ_ACCOUNT);
        return StringUtils.isEmpty(account) ? null : account;
    }

    /**
     * 获取当前客户端ID(该方法要在主线程运行)
     *
     * @return
     */
    public static String getCurrentClientId() {
        String clientId = ServletUtils.getHeader(RPCConstants.REQ_CLIENT_ID);
        return StringUtils.isEmpty(clientId) ? null : clientId;
    }

}
