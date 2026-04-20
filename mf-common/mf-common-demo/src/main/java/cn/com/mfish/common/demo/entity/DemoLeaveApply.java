package cn.com.mfish.common.demo.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelProperty;
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
 * @description: leave apply demo
 * @author: mfish
 * @date: 2026-04-19
 * @version: V2.3.1
 */
@Data
@TableName("demo_leave_apply")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "demo_leave_apply object")
public class DemoLeaveApply extends BaseEntity<String> {
    @ExcelProperty("Unique ID")
    @Schema(description = "Unique ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;

    @ExcelProperty("Title")
    @Schema(description = "Title")
    private String title;

    @ExcelProperty("Leave Type")
    @Schema(description = "Leave Type 1 personal 2 sick 3 annual")
    private Integer leaveType;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("Start Time")
    @Schema(description = "Start Time")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("End Time")
    @Schema(description = "End Time")
    private Date endTime;

    @ExcelProperty("Leave Days")
    @Schema(description = "Leave Days")
    private BigDecimal leaveDays;

    @ExcelProperty("Reason")
    @Schema(description = "Reason")
    private String reason;

    @ExcelProperty("Audit State")
    @Schema(description = "Audit State -1 draft 0 pending 1 approved 2 rejected")
    private Integer auditState;
}
