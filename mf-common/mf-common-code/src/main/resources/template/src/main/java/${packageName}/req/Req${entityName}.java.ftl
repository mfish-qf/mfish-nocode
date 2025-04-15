package ${packageName}.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
<#assign dateIndex = 0>
<#assign decimalIndex = 0>
<#list searchList as search>
<#if search.fieldInfo.type=='Date'&&dateIndex==0>
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
<#assign dateIndex = dateIndex+1>
</#if>
<#if search.fieldInfo.type=='BigDecimal'&&decimalIndex==0>
import java.math.BigDecimal;
<#assign decimalIndex = decimalIndex+1>
</#if>
</#list>

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V2.0.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "${tableInfo.tableComment}请求参数")
public class Req${entityName} {
<#list searchList as search>
 <#if !search.fieldInfo.isPrimary>
  <#if search.fieldInfo.type =='Date'>
   <#if search.fieldInfo.dbType =='DATE'>
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
   <#elseif search.fieldInfo.dbType =='DATETIME'>
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   </#if>
  </#if>
 </#if>
    @Schema(description = "${search.fieldInfo.comment}")
    private <#if search.fieldInfo.type=='java.sql.Blob'>byte[]<#else>${search.fieldInfo.type}</#if> ${search.fieldInfo.fieldName};
</#list>
}
