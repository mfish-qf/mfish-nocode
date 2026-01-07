package cn.com.mfish.common.prom.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @description: 请求参数
 * @author: mfish
 * @date: 2025/12/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(description = "Prometheus query请求参数")
public class ReqPromQuery extends ReqPromBase {
    @Schema(description = "查询时间")
    private Date time;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/api/v1/query?").append(super.toString());
        if (time != null) {
            stringBuilder.append("&time=").append(time.toInstant().getEpochSecond());
        }
        return stringBuilder.toString();
    }
}
