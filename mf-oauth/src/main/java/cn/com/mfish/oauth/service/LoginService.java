package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.SerConstant;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;


/**
 * @author: mfish
 * @date: 2020/2/15 16:09
 */
public interface LoginService {
    /**
     * 请求登录
     *
     * @param model   页面数据
     * @param request 请求信息
     * @return 是否成功
     */
    boolean getLogin(Model model, HttpServletRequest request);

    /**
     * 提交登录数据
     *
     * @param model 页面数据
     * @param request 请求信息
     * @return 返回结果
     */
    boolean postLogin(Model model, HttpServletRequest request);

    /**
     * 登录
     *
     * @param request 请求信息
     * @return 返回结果
     */
    Result<String> login(HttpServletRequest request);

    /**
     * 登录
     *
     * @param username 账号
     * @param password 密码
     * @param loginType 登陆类型
     * @return 返回登陆结果
     */
    Result<String> login(String username, String password, SerConstant.LoginType loginType, String clientId, String rememberMe);

    /**
     * 登录重试计数
     *
     * @param userId 用户id
     * @param matches 是否验证通过
     * @return 是否
     */
    boolean retryLimit(String userId, boolean matches);

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @param msg 短信内容
     */
    void sendMsg(String phone, String msg);

    /**
     * 保存短信验证码
     *
     * @param phone 手机号
     * @param code 验证码
     */
    void saveSmsCode(String phone, String code);

    /**
     * 删除短信验证码
     *
     * @param phone 手机号
     */
    void delSmsCode(String phone);

    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     * @return 返回验证码
     */
    String getSmsCode(String phone);

    /**
     * 保存短信倒计时信息
     *
     * @param phone 手机号
     */
    void saveSmsCodeTime(String phone);

    /**
     * 删除短信倒计时信息
     *
     * @param phone 手机号
     */
    void delSmsCodeTime(String phone);

    /**
     * 获取短信验证码倒计时时间
     *
     * @param phone 手机号
     * @return 返回倒计时时间
     */
    Long getSmsCodeTime(String phone);

    /**
     * 临时缓存sessionKey
     *
     * @param sessionKey sessionKey
     * @param openId openId
     */
    void sessionKeyTempCache(String sessionKey, String openId);

    /**
     * 通过sessionKey获取openId
     *
     * @param sessionKey sessionKey
     * @return 返回openId
     */
    String getOpenIdBySessionKey(String sessionKey);
}
