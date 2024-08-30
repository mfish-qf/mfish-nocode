package cn.com.mfish.oauth.service;

import cn.com.mfish.common.oauth.entity.AccessToken;
import cn.com.mfish.common.oauth.entity.WeChatToken;

/**
 * @author: mfish
 * @description: 微信接口服务
 * @date: 2021/12/14 9:37
 */
public interface WeChatService {

    /**
     * 通过openid获取用户id,检查微信绑定状态
     *
     * @param openId openId
     * @return 返回用户id
     */
    String getUserIdByOpenId(String openId);

    /**
     * 微信用户绑定
     *
     * @param openId openId
     * @param userId 用户id
     * @return 返回是否绑定成功
     */
    boolean bindWeChat(String openId, String userId);

    /**
     * 微信用户绑定
     *
     * @param openId   openId
     * @param userId   用户id
     * @param nickname 昵称
     * @return 返回是否绑定成功
     */
    boolean bindWeChat(String openId, String userId, String nickname);

    /**
     * 构建微信token
     *
     * @param openId     openId
     * @param sessionKey session
     * @return 微信token
     */
    WeChatToken buildWeChatToken(String openId, String sessionKey, String userId);

    /**
     * 将微信token转化成通用accessToken
     *
     * @param weChatToken 微信token
     * @return 通用token
     */
    AccessToken convertToken(WeChatToken weChatToken);
}
