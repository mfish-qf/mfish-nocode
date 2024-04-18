package ${packageName}.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
<#list tableInfo.columns as fieldInfo>
<#if fieldInfo.type=='Date'>
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
</#if>
<#if fieldInfo.type=='BigDecimal'>
import java.math.BigDecimal;
</#if>
</#list>

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V1.3.0
 */
@Data
@TableName("${tableInfo.tableName}")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "${tableInfo.tableName}对象 ${tableInfo.tableComment}")
public class ${entityName} extends BaseEntity<<#if tableInfo.idType==''>String<#else>${tableInfo.idType}</#if>> {
    <#if tableInfo.idType=='String'>
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private ${tableInfo.idType} id;
    <#elseif tableInfo.idType!=''>
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.AUTO)
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
