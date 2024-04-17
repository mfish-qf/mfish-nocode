package cn.com.mfish.sys.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 界面配置
 * @author: mfish
 * @date: 2023-03-07
 * @version: V1.2.1
 */
@Data
@Accessors(chain = true)
@ApiModel("界面配置请求参数")
public class ReqSysConfig {
    @ApiModelProperty(value = "用户ID")
    private String userId;
}
