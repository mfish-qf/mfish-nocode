package cn.com.mfish.sys.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 系统日志
 * @author: mfish
 * @date: 2023-01-08
 * @version: V1.2.1
 */
@Data
@Accessors(chain = true)
@ApiModel("系统日志请求参数")
public class ReqSysLog {
    @ApiModelProperty(value = "中文标题")
    private String title;
    @ApiModelProperty(value = "方法")
    private String method;
    @ApiModelProperty(value = "请求类型")
    private String reqType;
    @ApiModelProperty(value = "请求路径")
    private String reqUri;
    @ApiModelProperty(value = "请求来源（0其它 1管理端 2手机端）")
    private Integer reqSource;
    @ApiModelProperty(value = "操作类型（0其它 1查询 2新增 3修改 4删除 5授权 6导入 7导出 8登录...）")
    private String operType;
    @ApiModelProperty(value = "操作IP")
    private String operIp;
    @ApiModelProperty(value = "操作状态（0正常 1异常）")
    private Integer operStatus;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date endTime;
}
