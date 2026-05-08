package cn.com.mfish.oauth.service;

import cn.com.mfish.oauth.entity.RedisQrCode;

/**
 * @description: 二维码服务接口，提供扫码登录相关的二维码存储、查询和更新功能
 * @author: mfish
 * @date: 2020/3/5 14:57
 */
public interface QRCodeService {
    /**
     * 保存二维码信息到缓存
     *
     * @param qrCode 二维码对象
     */
    void saveQRCode(RedisQrCode qrCode);

    /**
     * 检查二维码状态
     *
     * @param code 二维码code值
     * @return 二维码对象，不存在返回null
     */
    RedisQrCode checkQRCode(String code);

    /**
     * 更新二维码状态信息
     *
     * @param qrCode 二维码对象
     */
    void updateQRCode(RedisQrCode qrCode);
}
