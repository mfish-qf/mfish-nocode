package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.utils.html.WebRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author qiufeng
 * @date 2020/2/12 13:47
 */
@Slf4j
public class AuthUtils {
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

    /**
     * 获取请求用户IP
     *
     * @param request
     * @return
     */
    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            log.info(ip);
            if (ip.indexOf(',') > 0) {
                ip = ip.substring(0, ip.indexOf(','));
            }
        }
        return ip;
    }

    /**
     * 生成6位数验证码
     *
     * @return
     */
    public static String buildCode() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        return String.valueOf((int) (random.nextDouble() * 900000 + 100000));
    }

    /**
     * 手机号脱敏
     *
     * @param value
     * @return
     */
    public static String phoneMasking(String value) {
        if (StringUtils.isEmpty(value) || value.length() != 11) {
            return value;
        }
        return value.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
