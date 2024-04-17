package cn.com.mfish.sys.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 系统日志
 * @Author: mfish
 * @date: 2022-09-02
 * @Version: V1.2.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_log")
@Accessors(chain = true)
@ApiModel(value = "sys_log对象", description = "系统日志")
public class SysLog extends BaseEntity<Integer> {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "日志ID")
    private Integer id;
    @ApiModelProperty(value = "中文标题")
    private String title;
    @ApiModelProperty(value = "方法")
    private String method;
    @ApiModelProperty(value = "请求类型")
    private String reqType;
    @ApiModelProperty(value = "请求路径")
    private String reqUri;
    @ApiModelProperty(value = "请求参数")
    private String reqParam;
    @ApiModelProperty(value = "请求来源（0其它 1管理端 2手机端）")
    private Integer reqSource;
    @ApiModelProperty(value = "操作类型（0其它 1查询 2新增 3修改 4删除 5授权 6导入 7导出 8登录...）")
    private String operType;
    @ApiModelProperty(value = "操作IP")
    private String operIp;
    @ApiModelProperty(value = "操作状态（0正常 1异常）")
    private Integer operStatus;
    @ApiModelProperty(value = "返回信息")
    private String remark;
}
