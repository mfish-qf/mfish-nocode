package cn.com.mfish.sys.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 系统日志
 * @author: mfish
 * @date: 2023-01-08
 * @version: V2.0.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "系统日志请求参数")
public class ReqSysLog {
    @Schema(description = "中文标题")
    private String title;
    @Schema(description = "方法")
    private String method;
    @Schema(description = "请求类型")
    private String reqType;
    @Schema(description = "请求路径")
    private String reqUri;
    @Schema(description = "请求来源（0其它 1管理端 2手机端）")
    private Integer reqSource;
    @Schema(description = "操作类型（0其它 1查询 2新增 3修改 4删除 5授权 6导入 7导出 8登录...）")
    private String operType;
    @Schema(description = "操作IP")
    private String operIp;
    @Schema(description = "操作状态（0正常 1异常）")
    private Integer operStatus;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date endTime;
}
