package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.utils.html.WebRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @description: 认证相关信息
 * @date 2020/2/12 13:47
 */
@Slf4j
public class AuthInfoUtils {
    /**
     * 获取accessToken
     *
     * @param request servletRequest
     * @return
     */
    public static String getAccessToken(HttpServletRequest request) {
        return getAccessToken(new WebRequest(request));
    }

    /**
     * 获取accessToken
     *
     * @param nativeWebRequest nativeWebRequest
     * @return
     */
    public static String getAccessToken(NativeWebRequest nativeWebRequest) {
        return getAccessToken(new WebRequest<>(nativeWebRequest));

    }

    /**
     * 从请求中获取token值
     * token通过access_token=****直接赋值
     * 或者token放到head中 以Authorization=Bearer******方式传入
     *
     * @param request
     * @return
     */
    public static String getAccessToken(WebRequest request) {
        // 头部的Authorization值以Bearer开头
        String auth = request.getHeader(Constants.AUTHENTICATION);
        String accessToken = null;
        if (auth != null && auth.startsWith(Constants.OAUTH_HEADER_NAME)) {
            accessToken = auth.replace(Constants.OAUTH_HEADER_NAME, "").trim();
        }
        // 请求参数中包含access_token参数
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = request.getParameter(Constants.ACCESS_TOKEN);
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
     * 获取当前用户ID
     *
     * @return
     */
    public static String getCurrentUserId() {
        String userId = ServletUtils.getRequest().getHeader(CredentialConstants.REQ_USER_ID);
        return cn.com.mfish.common.core.utils.StringUtils.isEmpty(userId) ? null : userId;
    }

    /**
     * 获取当前帐号
     *
     * @return
     */
    public static String getCurrentAccount() {
        String account = ServletUtils.getRequest().getHeader(CredentialConstants.REQ_ACCOUNT);
        return cn.com.mfish.common.core.utils.StringUtils.isEmpty(account) ? null : account;
    }

    /**
     * 获取当前客户端ID
     *
     * @return
     */
    public static String getCurrentClientId() {
        String clientId = ServletUtils.getRequest().getHeader(CredentialConstants.REQ_CLIENT_ID);
        return cn.com.mfish.common.core.utils.StringUtils.isEmpty(clientId) ? null : clientId;
    }

}
