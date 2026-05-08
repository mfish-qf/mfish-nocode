package cn.com.mfish.common.core.exception;

/**
 * @description: OAuth验证异常
 * @author: mfish
 * @date: 2020/2/17 16:32
 */
public class OAuthValidateException extends RuntimeException {
    public OAuthValidateException(String msg) {
        super(msg);
    }
}
