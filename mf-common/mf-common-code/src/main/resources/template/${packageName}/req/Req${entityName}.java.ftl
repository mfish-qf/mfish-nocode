package ${packageName}.req;

import io.swagger.v3.oas.annotations.media.Schema;
<#if searchList?size!=0>

</#if>
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V1.3.0
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
