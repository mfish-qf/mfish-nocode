package cn.com.mfish.oauth.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qiufeng
 * @date 2020/3/5 14:38
 */
@ApiModel("二维码属性")
@Data
public class QRCode implements Serializable {
    @ApiModelProperty("二维码值")
    private String code;
    @ApiModelProperty("扫码状态")
    private String status;
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("确认密钥")
    private String secret;
}
