package ${packageName}.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel("${tableInfo.tableComment}请求参数")
public class Req${entityName} {

}
