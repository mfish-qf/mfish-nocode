package cn.com.mfish.common.oauth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: mfish
 * @date: 2020/2/17 15:09
 */
@Schema(description = "返回accessToken信息")
@Data
@Accessors(chain = true)
public class AccessToken implements Serializable {
    @Schema(description = "token值")
    private String access_token;
    @Schema(description = "刷新token用于重新获取token")
    private String refresh_token;
    @Schema(description = "有效期")
    private Long expires_in;

    public AccessToken() {
    }

    public AccessToken(String token, String refreshToken, Long expiresIn) {
        this.setAccess_token(token).setRefresh_token(refreshToken).setExpires_in(expiresIn);
    }
}
