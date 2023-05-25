import { BaseEntity, PageResult } from "/@/api/model/BaseModel";

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V1.0.1
 */
export interface ${entityName} extends BaseEntity<<#if tableInfo.idType==''||tableInfo.idType=='String'>string<#else>number</#if>> {
<#list tableInfo.columns as fieldInfo>
  ${fieldInfo.fieldName}<#if fieldInfo.nullAble>?</#if>: <#if fieldInfo.type=='String'||fieldInfo.type='Date'>string<#elseif fieldInfo.type =='Boolean'>boolean<#else>number</#if>;
</#list>
}

export interface Req${entityName} {
<#list searchList as search>
 ${search.fieldInfo.fieldName}: <#if search.fieldInfo.type=='String'||search.fieldInfo.type='Date'>string<#elseif search.fieldInfo.type =='Boolean'>boolean<#else>number</#if>;
</#list>
}

export type ${entityName}PageModel = PageResult<${entityName}>;