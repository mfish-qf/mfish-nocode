package cn.com.mfish.oauth.service;

import cn.com.mfish.oauth.model.RedisQrCode;

/**
 * @author qiufeng
 * @date 2020/3/5 14:57
 */
public interface QRCodeService {
    void saveQRCode(RedisQrCode qrCode);
    RedisQrCode checkQRCode(String code);
    void updateQRCode(RedisQrCode qrCode);
}
