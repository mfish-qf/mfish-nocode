package cn.com.mfish.common.core.exception;

/**
 * @author: mfish
 * @date: 2020/2/17 16:32
 */
public class OAuthValidateException extends RuntimeException {
    public OAuthValidateException(String msg){
        super(msg);
    }
}
