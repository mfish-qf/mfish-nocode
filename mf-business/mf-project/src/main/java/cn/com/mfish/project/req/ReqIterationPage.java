package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description: 迭代版本分页查询参数（版本管理弹窗后端过滤）
 * @author: mfish
 * @date: 2026-11-25
 * @version: V2.4.1
 */
@Data
@Schema(description = "迭代版本分页查询参数")
public class ReqIterationPage {
    @Schema(description = "页码(默认1)")
    private Integer pageNum = 1;

    @Schema(description = "每页条数(默认10)")
    private Integer pageSize = 10;

    @Schema(description = "状态精确过滤(规划中/进行中/测试中/已发布)")
    private String status;

    @Schema(description = "版本代号/名称模糊关键字")
    private String keyword;

    @Schema(description = "研发开始日期起(YYYY-MM-DD，含)")
    private String startDateBegin;

    @Schema(description = "研发开始日期止(YYYY-MM-DD，含)")
    private String startDateEnd;

    @Schema(description = "仅看归档(已发布)；为 null 不过滤")
    private Boolean archived;
}
