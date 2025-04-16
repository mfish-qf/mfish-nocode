package cn.com.mfish.sys.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 系统日志
 * @Author: mfish
 * @date: 2022-09-02
 * @version: V2.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_log")
@Accessors(chain = true)
@Schema(description = "sys_log对象 系统日志")
public class SysLog extends BaseEntity<Integer> {
    @TableId(type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Integer id;
    @Schema(description = "中文标题")
    private String title;
    @Schema(description = "方法")
    private String method;
    @Schema(description = "请求类型")
    private String reqType;
    @Schema(description = "请求路径")
    private String reqUri;
    @Schema(description = "请求参数")
    private String reqParam;
    @Schema(description = "请求来源（0其它 1管理端 2手机端）")
    private Integer reqSource;
    @Schema(description = "操作类型（0其它 1查询 2新增 3修改 4删除 5授权 6导入 7导出 8登录...）")
    private String operType;
    @Schema(description = "操作IP")
    private String operIp;
    @Schema(description = "操作状态（0正常 1异常）")
    private Integer operStatus;
    @Schema(description = "返回信息")
    private String remark;
}
