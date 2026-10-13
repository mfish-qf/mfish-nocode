package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @description: 迭代版本表
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@TableName("pm_iteration")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_iteration对象 迭代版本")
public class PmIteration extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_ID)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "版本号/代号(如 v1.1、2026-09B)")
    private String code;
    @Schema(description = "迭代名称")
    private String name;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "迭代开始日期")
    private Date startDate;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "迭代结束日期")
    private Date endDate;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "研发确认发版日期")
    private Date releaseDate;
    @Schema(description = "迭代状态(规划中/进行中/测试中/已发布)")
    private String status;
    @Schema(description = "迭代目标与说明")
    private String description;
    @Schema(description = "删除标志(0正常 1已删除)")
    @TableLogic
    private Integer delFlag;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "自动计算跨越的所有自然月份列表(如 [2026-09, 2026-10])")
    @TableField(exist = false)
    private List<String> spannedMonths;
    @Schema(description = "版本中包含的多节点转测列表")
    @TableField(exist = false)
    private List<PmIterationTestNode> testNodes;
}
