package ${packageName}.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: ${tableInfo.tableDesc}
 * @Author: mfish
 * @Date: ${.now?string["yyyy-MM-dd"]}
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@ApiModel("${tableInfo.tableDesc}请求参数")
public class Req${entityName} {
    

}
