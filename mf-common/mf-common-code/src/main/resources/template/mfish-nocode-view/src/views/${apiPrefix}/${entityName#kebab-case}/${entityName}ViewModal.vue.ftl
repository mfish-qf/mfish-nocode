<!--
 @description: ${tableInfo.tableComment}查看
 @author: mfish
 @date: ${.now?string["yyyy-MM-dd"]}
 @version: V1.3.2
-->
<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="${tableInfo.tableComment}信息">
    <Description @register="registerDesc" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { BasicModal, useModalInner } from "@/components/general/Modal";
  import { Description, useDescription } from "@/components/general/Description";
  import { ref } from "vue";
  import { ${entityName}Desc } from "./${entityName?uncap_first}.data";

  defineOptions({ name: "${entityName}ViewModal" });
  const ${entityName?uncap_first}Data = ref();
  const ${entityName?uncap_first}Desc = new ${entityName}Desc();
  const [registerModal, { setModalProps }] = useModalInner(async (data) => {
    setModalProps({
      confirmLoading: false,
      width: "800px",
      cancelText: "关闭",
      showOkBtn: false
    });
    ${entityName?uncap_first}Data.value = data.record;
  });
  const [registerDesc] = useDescription({
    data: ${entityName?uncap_first}Data,
    schema: ${entityName?uncap_first}Desc.viewSchema,
    column: 2
  });
</script>
