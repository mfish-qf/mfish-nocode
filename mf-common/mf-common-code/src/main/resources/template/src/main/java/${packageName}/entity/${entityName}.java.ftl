package ${packageName}.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
<#assign dateIndex = 0>
<#assign decimalIndex = 0>
<#list tableInfo.columns as fieldInfo>
<#if fieldInfo.type=='Date'&&dateIndex==0>
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
<#assign dateIndex = dateIndex+1>
</#if>
<#if fieldInfo.type=='BigDecimal'&&decimalIndex==0>
import java.math.BigDecimal;
<#assign decimalIndex = decimalIndex+1>
</#if>
</#list>

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V2.1.0
 */
@Data
@TableName("${tableInfo.tableName}")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "${tableInfo.tableName}对象 ${tableInfo.tableComment}")
public class ${entityName} extends BaseEntity<<#if tableInfo.idType==''>String<#else>${tableInfo.idType}</#if>> {
    <#if tableInfo.idType=='String'>
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private ${tableInfo.idType} id;
    <#elseif tableInfo.idType!=''>
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.AUTO)
    @Accessors(chain = true)
    private ${tableInfo.idType} id;
    </#if>
    <#list tableInfo.columns as fieldInfo>
	<#if !fieldInfo.isPrimary>
      <#if fieldInfo.type =='Date'>
        <#if fieldInfo.dbType =='DATE' || fieldInfo.dbType =='date'>
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
          <#elseif fieldInfo.dbType =='DATETIME' || fieldInfo.dbType =='datetime'>
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        </#if>
      </#if>
    </#if>
    @ExcelProperty("${fieldInfo.comment}")
    @Schema(description = "${fieldInfo.comment}")
	private <#if fieldInfo.type=='java.sql.Blob'>byte[]<#else>${fieldInfo.type}</#if> ${fieldInfo.fieldName};
	</#list>
}