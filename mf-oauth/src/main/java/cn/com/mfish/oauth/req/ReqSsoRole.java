package cn.com.mfish.oauth.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@ApiModel("角色信息表请求参数")
public class ReqSsoRole {
    @ApiModelProperty(value = "客户端ID")
    private String clientId;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("开始日期")
    @TableField(exist = false)
    private Date startDate;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("结束日期")
    @TableField(exist = false)
    private Date endDate;
}
