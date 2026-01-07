package cn.com.mfish.common.prom.req;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @description: Prometheus范围查询参数
 * @author: mfish
 * @date: 2025/12/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Prometheus 范围查询参数")
@Accessors(chain = true)
public class ReqPromQueryRange extends ReqPromBase {
    @Schema(description = "查询时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date start;
    @Schema(description = "查询结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date end;
    @Schema(description = "查询步长", requiredMode = Schema.RequiredMode.REQUIRED)
    private PromDuration step;

    @Override
    public String toString() {
        if (start == null) {
            throw new MyRuntimeException("查询开始时间不能为空");
        }
        if (end == null) {
            throw new MyRuntimeException("查询结束时间不能为空");
        }
        if (step == null) {
            throw new MyRuntimeException("查询步长不能为空");
        }
        return "/api/v1/query_range?" + super.toString() +
                "&start=" + start.toInstant().getEpochSecond() +
                "&end=" + end.toInstant().getEpochSecond() +
                "&step=" + step;
    }
}
