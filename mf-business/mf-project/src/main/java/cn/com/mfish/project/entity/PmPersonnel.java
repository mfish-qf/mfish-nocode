package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 团队人员与岗位档案表
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@TableName("pm_personnel")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_personnel对象 团队人员与岗位档案")
public class PmPersonnel extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_ID)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "姓名")
    private String name;
    @Schema(description = "显示全称(如 产品部-林晓敏 或 李博文(PMO))")
    private String displayName;
    @Schema(description = "岗位角色编码(frontend/backend/qa/product/owner)")
    private String role;
    @Schema(description = "中文岗位名(如 前端开发、后端研发、测试工程师、产品经理、技术负责人)")
    private String roleName;
    @Schema(description = "部门(如 大前端平台组、云原生中间件组、质量保证QA部、产品规划部)")
    private String department;
    @Schema(description = "职称头衔(如 高级前端专家、后端资深架构师、测试组长)")
    private String title;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "在职状态(在职/忙碌/请假)")
    private String status;
    @Schema(description = "删除标志(0正常 1已删除)")
    @TableLogic
    private Integer delFlag;
    @Schema(description = "备注")
    private String remark;
}
