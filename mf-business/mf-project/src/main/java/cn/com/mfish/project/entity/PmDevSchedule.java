package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 研发排期明细表(一行 = 需求 × 人员 × 岗位)
 * 由自动排期引擎生成，支持一键重算；单行可钉住(is_pinned)，重算时保持不动；
 * 拖拽调期后自动钉住，避免被后续重算覆盖
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.1
 */
@Data
@TableName("pm_dev_schedule")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_dev_schedule对象 研发排期明细(需求×人员×岗位)")
public class PmDevSchedule extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "需求ID")
    private String reqId;
    @Schema(description = "需求序号(冗余, 便于排序展示)")
    private Integer reqNo;
    @Schema(description = "归属迭代ID")
    private String iterationId;
    @Schema(description = "开发人员姓名")
    private String personName;
    @Schema(description = "岗位: frontend前端 / backend后端 / fullstack全栈(未拆分工时)")
    private String devRole;
    @Schema(description = "该人员在本需求上的工时(人/天)")
    private BigDecimal workload;
    @Schema(description = "同一人员任务顺序(升序即执行顺序, 重算后按开始日重排)")
    private Integer seq;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "任务开始日(有效工作日)")
    private Date devStartDate;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "任务完成日(按工时与日历跳过规则推导)")
    private Date devEndDate;
    @Schema(description = "是否钉住: true=一键重算时保持不动")
    private Boolean isPinned;
    @Schema(description = "钉住原因/备注(拖拽调期时自动记录)")
    private String pinReason;
    @Schema(description = "备注")
    private String remark;
}
