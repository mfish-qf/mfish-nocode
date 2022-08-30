package cn.com.mfish.common.core.exception;

/**
 * @author qiufeng
 * @date 2021/8/12 11:32
 */
public class CaptchaException extends RuntimeException {
    public CaptchaException(String msg) {
        super(msg);
    }

    public CaptchaException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaptchaException(Throwable cause) {
        super(cause);
    }
}
