package cn.com.mfish.common.prom.req;

import cn.com.mfish.common.prom.enums.StepUnit;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 时间步长
 * @author: mfish
 * @date: 2025/12/19
 */
@Data
@Accessors(chain = true)
public class PromDuration {
    public PromDuration(Long duration, StepUnit unit) {
        this.duration = duration;
        this.unit = unit;
    }
    private Long duration;
    private StepUnit unit;

    @Override
    public String toString() {
        return duration + unit.getValue();
    }
}
