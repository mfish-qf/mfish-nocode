import { BaseEntity, PageResult, ReqPage } from "@/api/model/BaseModel";

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V1.3.1
 */
export interface ${entityName} extends BaseEntity<<#if tableInfo.idType==''||tableInfo.idType=='String'>string<#else>number</#if>> {
<#list tableInfo.columns as fieldInfo>
  //${fieldInfo.comment}
  ${fieldInfo.fieldName}<#if fieldInfo.nullAble>?</#if>: <#if fieldInfo.type=='String'||fieldInfo.type='Date'>string<#elseif fieldInfo.type =='Boolean'>boolean<#else>number</#if>;
</#list>
}

export interface Req${entityName} extends ReqPage {
<#list searchList as search>
  //${search.fieldInfo.comment}
  ${search.fieldInfo.fieldName}?: <#if search.fieldInfo.type=='String'||search.fieldInfo.type='Date'>string<#elseif search.fieldInfo.type =='Boolean'>boolean<#else>number</#if>;
</#list>
}

//分页结果集
export type ${entityName}PageModel = PageResult<${entityName}>;
