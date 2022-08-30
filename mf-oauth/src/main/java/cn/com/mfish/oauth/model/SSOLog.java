package cn.com.mfish.oauth.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author qiufeng
 * @date 2020/2/22 11:30
 */
@ApiModel("登录日志记录")
@Data
public class SSOLog extends BaseEntity<String> {
    @ApiModelProperty("唯一ID  uuid")
    private String id;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("客户端id")
    private String clientId;
    @ApiModelProperty("请求ip地址")
    private String ip;
    @ApiModelProperty("请求sessionId")
    private String sessionId;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("调用接口名称")
    private String interfaceName;
    @ApiModelProperty("接口调用状态 0成功 1失败")
    private int state;
    @ApiModelProperty("描述信息")
    private String remark;
}
