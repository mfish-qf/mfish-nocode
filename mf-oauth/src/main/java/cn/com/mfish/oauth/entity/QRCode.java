package cn.com.mfish.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: mfish
 * @date: 2020/3/5 14:38
 */
@Schema(description = "二维码属性")
@Data
public class QRCode implements Serializable {
    @Schema(description = "二维码值")
    private String code;
    @Schema(description = "扫码状态")
    private String status;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "确认密钥")
    private String secret;
}
