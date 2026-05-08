package cn.com.mfish.common.workflow.api.req;

import cn.com.mfish.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @description: 流程查询参数
 * @author: mfish
 * @date: 2025/9/25
 */
@Data
@Schema(description = "流程查询参数")
@Accessors(chain = true)
public class ReqProcess {
    @Schema(description = "流程定义key")
    private String flowKey;
    @Schema(description = "业务id")
    private String businessKey;
    @Schema(description = "流程实例id列表")
    private List<String> processInstanceIds;

    /**
     * 设置流程实例ID列表（逗号分隔的字符串）
     *
     * @param ids 逗号分隔的流程实例ID字符串
     * @return 当前对象
     */
    public ReqProcess setProcessInstanceIds(String ids) {
        if (StringUtils.isNotEmpty(ids.trim())) {
            processInstanceIds = Arrays.asList(ids.trim().split(","));
        } else {
            processInstanceIds = new ArrayList<>();
        }
        return this;
    }

    /**
     * 设置流程实例ID列表
     *
     * @param ids 流程实例ID列表，为null时设为空列表
     * @return 当前对象
     */
    public ReqProcess setProcessInstanceIds(List<String> ids) {
        this.processInstanceIds = Objects.requireNonNullElseGet(ids, ArrayList::new);
        return this;
    }

}
