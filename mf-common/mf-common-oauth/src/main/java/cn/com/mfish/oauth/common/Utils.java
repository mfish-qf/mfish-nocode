package cn.com.mfish.oauth.common;

import cn.com.mfish.common.core.constants.Constants;
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
public class Utils {
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
        String accessToken = request.getParameter(Constants.ACCESS_TOKEN);
        // 请求参数中包含access_token参数
        if (StringUtils.isEmpty(accessToken)) {
            // 头部的Authorization值以Bearer开头
            String auth = request.getHeader(Constants.AUTHENTICATION);
            if (auth != null && auth.startsWith(Constants.OAUTH_HEADER_NAME)) {
                accessToken = auth.replace(Constants.OAUTH_HEADER_NAME, "").trim();
            }
        }
        return accessToken;
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
