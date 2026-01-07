package cn.com.mfish.common.prom.req;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @description: Prometheus 基础查询参数
 * @author: mfish
 * @date: 2025/12/19
 */
@Data
@Accessors(chain = true)
@Schema(description = "Prometheus 基础查询参数")
public class ReqPromBase {
    @Schema(description = "PromQL 查询语句", requiredMode = Schema.RequiredMode.REQUIRED)
    private String query;
    @Schema(description = "查询超时时间")
    private PromDuration timeout;
    @Schema(description = "查询限制数量")
    private Long limit;
    @Schema(description = "回溯时间")
    private PromDuration lookBackDelta;

    @Override
    public String toString() {
        if (StringUtils.isBlank(query)) {
            throw new MyRuntimeException("PromQL 查询语句不能为空");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("query=").append(URLEncoder.encode(getQuery(), StandardCharsets.UTF_8));
        if (timeout != null) {
            stringBuilder.append("&timeout=").append(timeout);
        }
        if (limit != null) {
            stringBuilder.append("&limit=").append(limit);
        }
        if (lookBackDelta != null) {
            stringBuilder.append("&lookback_delta=").append(lookBackDelta);
        }
        return stringBuilder.toString();
    }
}
