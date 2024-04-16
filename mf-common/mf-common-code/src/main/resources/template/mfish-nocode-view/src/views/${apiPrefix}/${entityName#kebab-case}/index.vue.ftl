<!--
 @description: ${tableInfo.tableComment}
 @author: mfish
 @date: ${.now?string["yyyy-MM-dd"]}
 @version: V1.2.0
-->
<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate" v-auth="'sys:${entityName?uncap_first}:insert'">新增</a-button>
        <a-button color="warning" @click="handleExport" v-auth="'sys:${entityName?uncap_first}:export'">导出</a-button>
        <a-button color="error" @click="handleBatchDelete" v-auth="'sys:${entityName?uncap_first}:delete'">批量删除</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
                  :actions="[
              {
                icon: 'ant-design:edit-outlined',
                onClick: handleEdit.bind(null, record),
                auth: 'sys:${entityName?uncap_first}:update',
                tooltip: '修改'
              },
              {
                icon: 'ant-design:delete-outlined',
                color: 'error',
                popConfirm: {
                  title: '是否确认删除',
                  placement: 'left',
                  confirm: handleDelete.bind(null, record)
                },
                auth: 'sys:${entityName?uncap_first}:delete',
                tooltip: '删除'
              }
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <${entityName}Modal @register="registerModal" @success="handleSuccess" />
  </div>
</template>
<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from "/@/components/general/Table";
  import { deleteBatch${entityName}, delete${entityName}, export${entityName}, get${entityName}List } from "/@/api/${apiPrefix}/${entityName}";
  import { useModal } from "/@/components/general/Modal";
  import ${entityName}Modal from "./${entityName}Modal.vue";
  import { columns, searchFormSchema } from "./${entityName?uncap_first}.data";
  import { ${entityName} } from "/@/api/${apiPrefix}/model/${entityName}Model";
  import { TableProps } from "ant-design-vue";
  import { ref } from "vue";
  import { useMessage } from "/@/hooks/web/UseMessage";

  defineOptions({ name: "${entityName}Management" });
  const [registerModal, { openModal }] = useModal();
  const selectedRowKeys = ref<string[]>([]);
  const rowSelection: TableProps["rowSelection"] = {
    onChange: (rowKeys: string[]) => {
      selectedRowKeys.value = rowKeys;
    }
  };
  const [registerTable, { reload, getForm }] = useTable({
    title: "${tableInfo.tableComment}列表",
    api: get${entityName}List,
    rowKey: "id",
    columns,
    formConfig: {
      name: "search_form_item",
      labelWidth: 100,
      schemas: searchFormSchema,
      autoSubmitOnEnter: true
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
    rowSelection: rowSelection,
    actionColumn: {
      width: 80,
      title: "操作",
      dataIndex: "action"
    }
  });
  const { createMessage } = useMessage();
  /**
   * 新建
   */
  function handleCreate() {
    openModal(true, {
      isUpdate: false
    });
  }

  /**
   *  导出自动生成支持导出1000条可自行修改
   */
  function handleExport() {
    export${entityName}({ ...getForm().getFieldsValue(), pageNum: 1, pageSize: 1000 });
  }

  /**
   * 修改
   * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
   */
  function handleEdit(${entityName?uncap_first}: ${entityName}) {
    openModal(true, {
      record: ${entityName?uncap_first},
      isUpdate: true
    });
  }

  /**
   * 删除
   * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
   */
  function handleDelete(${entityName?uncap_first}: ${entityName}) {
    if(${entityName?uncap_first}.id){
      delete${entityName}(${entityName?uncap_first}.id).then(() => {
        handleSuccess();
      });
    }
  }

  /**
   * 批量删除
   * @param ${entityName?uncap_first} ${tableInfo.tableComment}对象
   */
  function handleBatchDelete() {
    if (selectedRowKeys.value.length > 0) {
      deleteBatch${entityName}(selectedRowKeys.value.join(",")).then(() => {
        handleSuccess();
      });
    } else {
      createMessage.warning("请勾选要删除的数据");
    }
  }

  /**
   * 处理完成
   */
  function handleSuccess() {
    reload();
  }
</script>
