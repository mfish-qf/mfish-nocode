package cn.com.mfish.code.entity;

import cn.com.mfish.common.code.api.req.ReqSearch;
import cn.com.mfish.sys.api.entity.FieldInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 搜索条件
 * @author: mfish
 * @date: 2023/5/9 22:14
 */
@Data
@Accessors(chain = true)
@ApiModel("搜索条件增加字段信息")
public class SearchInfo extends ReqSearch {
    @ApiModelProperty("字段信息")
    private FieldInfo fieldInfo;
}
