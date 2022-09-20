package cn.com.mfish.oauth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SSOLog {
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
    @ApiModelProperty("调用接口名称")
    private String interfaceName;
    @ApiModelProperty("接口调用状态 0成功 1失败")
    private int state;
    @ApiModelProperty("描述信息")
    private String remark;
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime = new Date();
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime = new Date();
}
