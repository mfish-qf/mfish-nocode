package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 批量应用智能负载均衡调配方案请求参数
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Schema(description = "批量应用智能负载均衡调配方案请求参数")
public class ReqBalancingPlan {
    @Schema(description = "调配计划列表")
    @Valid
    private List<BalancingPlanItem> plan = new ArrayList<>();
}
