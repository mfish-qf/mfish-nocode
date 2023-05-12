package cn.com.mfish.common.core.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: mfish
 * @description: 基础对象类
 * @date: 2022/11/11 17:31
 */
@Data
@Accessors(chain = true)
@ApiModel("基础对象")
public class BaseEntity<T> {
    @ExcelIgnore
    @ApiModelProperty(value = "ID")
    private T id;
    @ExcelProperty("创建用户")
    @ApiModelProperty(value = "创建用户")
    private String createBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ExcelProperty("更新用户")
    @ApiModelProperty(value = "更新用户")
    private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
