import { PageResult } from "/@/api/model/BaseModel";

/**
 * @Description: ${tableInfo.tableComment}
 * @Author: mfish
 * @Date: ${.now?string["yyyy-MM-dd"]}
 * @Version: V1.0.0
 */
export interface ${entityName} {
<#list tableInfo.columns as fieldInfo>
    ${fieldInfo.fieldName}: <#if fieldInfo.type=='String'||fieldInfo.type='Date'>string<#elseif fieldInfo.type =='Boolean'>boolean<#else>number</#if>
</#list>
}

export interface Req${entityName} {

}

export type ${entityName}PageModel = PageResult<${entityName}>;