package cn.com.mfish.common.core.entity;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "基础对象")
public class BaseEntity<T> {
    @ExcelIgnore
    @Schema(description = "ID")
    @Accessors(chain = true)
    private T id;
    @ExcelProperty("创建用户")
    @Schema(description = "创建用户")
    private String createBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("创建时间")
    @Schema(description = "创建时间")
    private Date createTime;
    @ExcelProperty("更新用户")
    @Schema(description = "更新用户")
    private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("更新时间")
    @Schema(description = "更新时间")
    private Date updateTime;
}
