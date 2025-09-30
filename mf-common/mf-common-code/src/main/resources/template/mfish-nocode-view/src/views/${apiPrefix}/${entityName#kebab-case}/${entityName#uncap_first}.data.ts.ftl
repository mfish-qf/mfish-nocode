import { BasicColumn, FormSchema } from "@mfish/core/components/Table";
import { DescItem } from "@mfish/core/components/Description";
<#assign dictIndex = 0>
<#list searchList as search>
<#if search.component??>
<#list search.component as com>
<#if com_index == 1&&dictIndex==0>
import { buildDictTag, getDictProps } from "@mfish/core/components/DictTag";
<#assign dictIndex = 1>
</#if>
</#list>
</#if>
</#list>

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V2.2.0
 */
export const columns: BasicColumn[] = [
<#list tableInfo.fieldExpands as fieldExpands>
  {
  <#if fieldExpands.dictComponent??>
  <#list fieldExpands.dictComponent as com>
  <#if com_index == 1>
    customRender: ({ record }) => {
      return buildDictTag("${com}", record.${fieldExpands.fieldInfo.fieldName});
    },
  </#if>
  </#list>
  </#if>
    title: "${fieldExpands.fieldInfo.comment}",
    dataIndex: "${fieldExpands.fieldInfo.fieldName}",
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
    colProps: { xl: 5, md: 6 }
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
      showTime: { format: "HH:mm:ss" },
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

export class ${entityName}Desc {
  viewSchema: DescItem[] = [
    {
      label: "id",
      field: "id",
      show: () => false
    },
<#list tableInfo.fieldExpands as fieldExpands>
    {
    <#if fieldExpands.dictComponent??>
    <#list fieldExpands.dictComponent as com>
    <#if com_index == 0>
    <#elseif com_index == 1>
      render: (val) => {
        if (val === undefined) return;
        return buildDictTag("${com}", val);
      },
    </#if>
    </#list>
    </#if>
      field: "${fieldExpands.fieldInfo.fieldName}",
      label: "${fieldExpands.fieldInfo.comment}"
    },
</#list>
  ];
}