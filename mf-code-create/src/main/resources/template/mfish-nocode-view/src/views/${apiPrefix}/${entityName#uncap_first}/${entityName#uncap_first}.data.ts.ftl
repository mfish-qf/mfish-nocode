import { BasicColumn } from "/@/components/Table";
import { FormSchema } from "/@/components/Table";

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V1.0.0
 */
export const columns: BasicColumn[] = [
<#list tableInfo.columns as fieldInfo>
  {
    title: "${fieldInfo.comment}",
    dataIndex: "${fieldInfo.fieldName}",
    width: 120
  },
</#list>
];
//todo 查询条件暂时用来装样子，后面增加配置条件后修改模版
export const searchFormSchema: FormSchema[] = [
<#assign x=0 />
<#list tableInfo.columns as fieldInfo>
  <#assign x=x+1 />
  {
    field: "${fieldInfo.fieldName}",
    label: "${fieldInfo.comment}",
    component: "Input",
    colProps: { span: 4 }
  },
  <#if x == 3><#break></#if>
</#list>
];
export const ${entityName?uncap_first}FormSchema: FormSchema[] = [
  {
    field: "id",
    label: "唯一ID",
    component: "Input",
    show: false
  },
<#list tableInfo.columns as fieldInfo>
  {
    field: "${fieldInfo.fieldName}",
    label: "${fieldInfo.comment}",
    component: "Input",
  <#if !fieldInfo.nullAble>
    required: true
  </#if>
  },
</#list>
];
