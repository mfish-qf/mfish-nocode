package cn.com.mfish.project.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @description: 批量更新评审结果请求参数
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Schema(description = "批量更新评审结果请求参数")
public class ReqBatchReview {
    @Schema(description = "需求ID列表")
    @NotEmpty(message = "需求ID列表不能为空")
    private List<String> ids;
    @Schema(description = "评审结果(取值来自字典 pm_review_result: 评审通过/补充修改/暂缓实施/未通过)")
    private String reviewResult;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "评审日期(yyyy-MM-dd)")
    private Date reviewDate;
    @Schema(description = "评审意见")
    private String reviewNotes;
}
