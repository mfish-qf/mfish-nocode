package cn.com.mfish.common.oauth.exception;

/**
 * @author ：qiufeng
 * @description：用户校验异常
 * @date ：2021/12/9 12:20
 */
public class UserValidateException extends RuntimeException {
    public UserValidateException(String msg) {
        super(msg);
    }
}
