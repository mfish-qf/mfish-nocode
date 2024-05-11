import { BasicColumn } from "/@/components/general/Table";
import { FormSchema } from "/@/components/general/Table";
<#assign dictIndex = 0>
<#list searchList as search>
<#if search.component??>
<#list search.component as com>
<#if com_index == 1&&dictIndex==0>
import { getDictProps } from "/@/utils/DictUtils";
<#assign dictIndex = 1>
</#if>
</#list>
</#if>
</#list>

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V1.3.0
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
export const searchFormSchema: FormSchema[] = [
<#list searchList as search>
  {
    field: "${search.fieldInfo.fieldName}",
    label: "${search.fieldInfo.comment}",
    <#if search.component??>
    <#list search.component as com>
    <#if com_index == 0>
    component: "${com}",
    <#elseif com_index == 1>
    componentProps: getDictProps("${com}"),
    </#if>
    </#list>
    <#else>
    component: "Input",
    </#if>
    colProps: { lg: 4, md: 5 }
  },
</#list>
];
export const ${entityName?uncap_first}FormSchema: FormSchema[] = [
  {
    field: "id",
    label: "唯一ID",
    component: "Input",
    show: false
  },
<#list tableInfo.fieldExpands as fieldExpands>
  {
    field: "${fieldExpands.fieldInfo.fieldName}",
    label: "${fieldExpands.fieldInfo.comment}",
  <#if fieldExpands.dictComponent??>
  <#list fieldExpands.dictComponent as com>
  <#if com_index == 0>
    component: "${com}",
  <#elseif com_index == 1>
    componentProps: getDictProps("${com}"),
  </#if>
  </#list>
  <#elseif fieldExpands.fieldInfo.type=='Date'>
    component: "DatePicker",
    componentProps: {
      <#if fieldExpands.fieldInfo.dbType =='DATE' || fieldExpands.fieldInfo.dbType =='date'>
      valueFormat: "YYYY-MM-DD",
      format: "YYYY-MM-DD",
      <#elseif fieldExpands.fieldInfo.dbType =='DATETIME' || fieldExpands.fieldInfo.dbType =='datetime'>
      valueFormat: "YYYY-MM-DD HH:mm:ss",
      format: "YYYY-MM-DD HH:mm:ss",
      </#if>
      getPopupContainer: () => document.body
    },
  <#elseif fieldExpands.fieldInfo.type=='Integer'||fieldExpands.fieldInfo.type=='Short'||fieldExpands.fieldInfo.type=='Long'||fieldExpands.fieldInfo.type=='Double'||fieldExpands.fieldInfo.type=='BigDecimal'>
    component: "InputNumber",
  <#else>
    component: "Input",
  </#if>
  <#if !fieldExpands.fieldInfo.nullAble>
    required: true
  </#if>
  },
</#list>
];
