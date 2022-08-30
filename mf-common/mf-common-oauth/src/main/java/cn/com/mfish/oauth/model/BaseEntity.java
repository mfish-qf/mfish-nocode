package cn.com.mfish.oauth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qiufeng
 * @date 2020/2/13 17:35
 */
@ApiModel("通用信息")
@Data
public abstract class BaseEntity<ID> implements Serializable {
    @ApiModelProperty("唯一ID")
    private ID id;
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime = new Date();
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime = new Date();
}
