package cn.com.mfish.common.code.entity;

import cn.com.mfish.sys.api.entity.FieldInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 字段拓展
 * @author: mfish
 * @date: 2024/4/19
 */
@Data
@Accessors(chain = true)
public class FieldExpand {
    @Schema(description = "字段信息")
    private FieldInfo fieldInfo;
    @Schema(description = "字典组件")
    private List<String> dictComponent;
}
