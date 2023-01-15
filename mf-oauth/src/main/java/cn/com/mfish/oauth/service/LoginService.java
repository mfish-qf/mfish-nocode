package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.SerConstant;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;


/**
 * @author: mfish
 * @date: 2020/2/15 16:09
 */
public interface LoginService {
    /**
     * 请求登录
     *
     * @param model
     * @param request
     * @return
     */
    boolean getLogin(Model model, HttpServletRequest request);

    /**
     * 提交登录数据
     *
     * @param mode
     * @param request
     * @return
     */
    boolean postLogin(Model mode, HttpServletRequest request);

    /**
     * 登录
     *
     * @param request
     * @return
     */
    Result<String> login(HttpServletRequest request);

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param loginType
     * @return
     */
    Result<String> login(String username, String password, SerConstant.LoginType loginType,String clientId ,String rememberMe);

    /**
     * 登录重试计数
     *
     * @param userId
     * @param matches
     * @return
     */
    boolean retryLimit(String userId, boolean matches);

    /**
     * 发送短信
     *
     * @param phone
     * @param msg
     */
    void sendMsg(String phone, String msg);

    /**
     * 保存短信验证码
     *
     * @param phone
     * @param code
     */
    void saveSmsCode(String phone, String code);

    /**
     * 删除短信验证码
     *
     * @param phone
     */
    void delSmsCode(String phone);

    /**
     * 获取短信验证码
     *
     * @param phone
     * @return
     */
    String getSmsCode(String phone);

    /**
     * 保存短信倒计时信息
     *
     * @param phone
     */
    void saveSmsCodeTime(String phone);

    /**
     * 删除短信倒计时信息
     *
     * @param phone
     */
    void delSmsCodeTime(String phone);

    /**
     * 获取短信验证码倒计时时间
     *
     * @param phone
     * @return
     */
    long getSmsCodeTime(String phone);

    /**
     * 临时缓存sessionKey
     *
     * @param sessionKey
     * @param openId
     */
    void sessionKeyTempCache(String sessionKey, String openId);

    /**
     * 通过sessionKey获取openId
     *
     * @param sessionKey
     * @return
     */
    String getOpenIdBySessionKey(String sessionKey);
}
