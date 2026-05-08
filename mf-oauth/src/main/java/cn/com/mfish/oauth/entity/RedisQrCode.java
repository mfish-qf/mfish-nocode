package cn.com.mfish.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: Redis存储的二维码信息，扩展二维码基础属性增加访问令牌字段
 * @author: mfish
 * @date: 2020/3/5 17:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RedisQrCode extends QRCode {
    /** 扫码确认后的访问令牌 */
    private String accessToken;
}
