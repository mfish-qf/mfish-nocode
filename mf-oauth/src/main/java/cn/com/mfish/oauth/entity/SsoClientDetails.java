package cn.com.mfish.oauth.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 客户端信息
 * @author: mfish
 * @date: 2023-05-12
 * @version: V2.1.1
 */
@Data
@TableName("sso_client_details")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sso_client_details对象 客户端信息")
public class SsoClientDetails extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("客户端ID")
    @Schema(description = "客户端ID")
    private String clientId;
    @ExcelProperty("客户端名称")
    @Schema(description = "客户端名称")
    private String clientName;
    @ExcelProperty("客户端能访问的资源集合")
    @Schema(description = "客户端能访问的资源集合")
    private String resourceIds;
    @ExcelProperty("客户端密钥")
    @Schema(description = "客户端密钥")
    private String clientSecret;
    @ExcelProperty("指定客户端权限范围")
    @Schema(description = "指定客户端权限范围")
    private String scope;
    @ExcelProperty("认证方式 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token")
    @Schema(description = "认证方式 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token")
    private String grantTypes;
    @ExcelProperty("客户端重定向url，authorization_code认证回调地址")
    @Schema(description = "客户端重定向url，authorization_code认证回调地址")
    private String redirectUrl;
    @ExcelProperty("指定用户的权限范围")
    @Schema(description = "指定用户的权限范围")
    private String authorities;
    @ExcelProperty("跳过授权页,默认true,适用于authorization_code模式，该属性暂未使用")
    @Schema(description = "跳过授权页,默认true,适用于authorization_code模式")
    private Integer autoApprove;
    @Schema(description = "删除标记")
    private Integer delFlag = 0;
}
