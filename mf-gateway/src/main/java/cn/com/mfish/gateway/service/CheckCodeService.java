package cn.com.mfish.gateway.service;

import cn.com.mfish.common.core.exception.CaptchaException;
import cn.com.mfish.common.core.web.Result;

import java.io.IOException;
import java.util.Map;

/**
 * @author: mfish
 * @date: 2021/8/12 11:06
 */
public interface CheckCodeService {

    /**
     * 创建验证码
     * @return
     * @throws IOException
     * @throws CaptchaException
     */
    Result<Map<String,Object>> createCaptcha();

    /**
     * 校验验证码
     * @param key key
     * @param value 值
     * @throws CaptchaException
     */
    void checkCaptcha(String key, String value) throws CaptchaException;
}
