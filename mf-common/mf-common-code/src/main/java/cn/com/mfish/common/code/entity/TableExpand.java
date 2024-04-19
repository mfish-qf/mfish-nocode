package cn.com.mfish.common.code.entity;

import cn.com.mfish.sys.api.entity.TableInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 表拓展
 * @author: mfish
 * @date: 2024/4/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class TableExpand extends TableInfo {
    @Schema(description = "列信息")
    private List<FieldExpand> fieldExpands;
}
