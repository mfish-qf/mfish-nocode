package cn.com.mfish.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: mfish
 * @date: 2020/3/5 18:23
 */
@Data
@Schema(description = "二维码返回结果带图片")
@EqualsAndHashCode(callSuper = true)
public class QRCodeImg extends QRCode {
    @Schema(description = "二维码")
    private String img;
}
