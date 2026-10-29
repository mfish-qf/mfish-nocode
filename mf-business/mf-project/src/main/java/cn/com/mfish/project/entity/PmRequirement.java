package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @description: 核心需求表
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@TableName("pm_requirement")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_requirement对象 核心需求")
public class PmRequirement extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "租户ID(多租户隔离)")
    @TableField("tenant_id")
    private String tenantId;
    @Schema(description = "序号(自动递增)")
    private Integer reqNo;
    @Schema(description = "模块")
    private String module;
    @Schema(description = "一级功能点")
    private String level1Feature;
    @Schema(description = "二级功能点")
    private String level2Feature;
    @Schema(description = "三级功能点")
    private String level3Feature;
    @Schema(description = "功能点描述")
    private String featureDescription;
    @Schema(description = "需求设计说明书对应章节")
    private String prdSection;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "需求评审日期")
    private Date reviewDate;
    @Schema(description = "评审结果")
    private String reviewResult;
    @Schema(description = "需求评审记录")
    private String reviewNotes;
    @Schema(description = "涉及厂商")
    private String vendors;
    @Schema(description = "优先级")
    private String priority;
    @Schema(description = "需求提出人")
    private String requester;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "需求提出日期")
    private Date requestDate;
    @Schema(description = "高保真图(URL或图示说明)")
    private String highFidelityUrl;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "需求变更日期")
    private Date changeDate;
    @Schema(description = "需求变更内容")
    private String changeContent;
    @Schema(description = "会前提出疑问/意见")
    private String preMeetingQuestions;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "研发确认发版时间")
    private Date releaseTime;
    @Schema(description = "前端人员")
    private String frontendDev;
    @Schema(description = "前端人员用户ID列表(JSON数组, 与frontendDev按索引对齐; 对应位为null表示未匹配到账号)")
    private String frontendDevIds;
    @Schema(description = "后端人员")
    private String backendDev;
    @Schema(description = "后端人员用户ID列表(JSON数组, 与backendDev按索引对齐; 对应位为null表示未匹配到账号)")
    private String backendDevIds;
    @Schema(description = "测试人员")
    private String qaTester;
    @Schema(description = "测试人员用户ID列表(JSON数组, 与qaTester按索引对齐; 对应位为null表示未匹配到账号)")
    private String qaTesterIds;
    @Schema(description = "备注\\疑问")
    private String remarks;
    @Schema(description = "关联负责人")
    private String owner;
    @Schema(description = "关联负责人用户ID列表(JSON数组, 与owner按索引对齐; 对应位为null表示未匹配到账号)")
    private String ownerIds;
    @Schema(description = "版本号(派生自 iteration_id -> pm_iteration.code, 不落库)")
    @TableField(exist = false)
    private String month;
    @Schema(description = "归属迭代ID")
    private String iterationId;
    @Schema(description = "研发预估总工时(单位:人/天,=前端+后端)")
    private BigDecimal workload;
    @Schema(description = "前端研发工时(单位:人/天)")
    private BigDecimal frontendWorkload;
    @Schema(description = "后端研发工时(单位:人/天)")
    private BigDecimal backendWorkload;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开发开始日期")
    private Date devStartDate;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开发完成日期(根据工时与日历规则自动推导)")
    private Date devEndDate;
    @Schema(description = "开发进度百分比(0-100)")
    private Integer devProgress;
    @Schema(description = "转测阶段/批次标签")
    private String testPhase;
    @Schema(description = "绑定的版本转测节点ID")
    private String testNodeId;
    @Schema(description = "绑定的版本转测节点名称")
    private String testNodeName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "绑定的版本转测时间")
    private Date testNodeDate;
    @Schema(description = "研发状态")
    private String status;
    @Schema(description = "详细需求PRD文档内容(类Word富文本规范与正文)")
    @TableField(select = false, updateStrategy = FieldStrategy.NOT_NULL)
    private String prdDocContent;
    /**
     * 轻量派生字段(不存在于表中)：标记该需求是否已撰写详细PRD文档。
     * 由网格查询 /doc-batch 的 SQL CASE 表达式填充，用于列表"待撰PRD"等指示器，
     * 避免为判断有无文档而返回 prd_doc_content 大文本。
     */
    @Schema(description = "是否已撰写详细PRD文档(派生字段, 表中不存在)")
    @TableField(exist = false)
    private Boolean hasPrdDoc;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "需求详细文档最后修改时间")
    private Date docLastModified;
    @Schema(description = "需求详细文档编写人")
    private String docAuthor;
    @Schema(description = "删除标志(0正常 1已删除)")
    @TableLogic
    private Integer delFlag;
}
