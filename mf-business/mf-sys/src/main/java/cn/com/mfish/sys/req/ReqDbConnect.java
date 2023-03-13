package cn.com.mfish.sys.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel("数据库连接请求参数")
public class ReqDbConnect {

}
