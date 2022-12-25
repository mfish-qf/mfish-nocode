import { BasicColumn } from "/@/components/Table";
import { FormSchema } from "/@/components/Table";

/**
 * @Description: ${tableInfo.tableComment}
 * @Author: mfish
 * @Date: ${.now?string["yyyy-MM-dd"]}
 * @Version: V1.0
 */
export const columns: BasicColumn[] = [
<#list tableInfo.columns as fieldInfo>
  <#if fieldInfo.fieldName != 'id'>
  {
    title: "${fieldInfo.comment}",
    dataIndex: "${fieldInfo.fieldName}",
    width: 120
  },
  </#if>
</#list>
];
//todo 查询条件暂时用来装样子，后面增加配置条件后修改模版
export const searchFormSchema: FormSchema[] = [
<#assign x=0 />
<#list tableInfo.columns as fieldInfo>
  <#if fieldInfo.fieldName != 'id'>
    <#assign x=x+1 />
  {
    field: "${fieldInfo.fieldName}",
    label: "${fieldInfo.comment}",
    component: "Input",
    colProps: { span: 4 }
  },
  </#if>
  <#if x == 3><#break></#if>
</#list>
];
export const ${entityName?uncap_first}FormSchema: FormSchema[] = [
<#list tableInfo.columns as fieldInfo>
<#if fieldInfo.fieldName == 'id'>
  {
    field: "id",
    label: "唯一ID",
    component: "Input",
    show: false
  },
<#else>
  {
    field: "${fieldInfo.fieldName}",
    label: "${fieldInfo.comment}",
    component: "Input",
  <#if !fieldInfo.nullAble>
    required: true
  </#if>
  },
</#if>
</#list>
];
