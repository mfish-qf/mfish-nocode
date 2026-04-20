---
name: frontend-crud
description: 为 mfish-nocode-pro 项目前端（Vue3 + TypeScript）生成标准增删改查页面代码，包括 Model、API、data、index.vue、Modal、ViewModal 六个文件。当用户说"帮我生成前端增删改查"、"新增前端页面"、"生成前端CRUD"时使用此 skill。
---

# 前端 CRUD 代码生成器

## 项目前端架构

```
mfish-nocode-view/src/
├── api/
│   └── {apiPrefix}/              # 如：sys、demo、nocode
│       ├── model/
│       │   └── {类名}Model.ts    # 接口类型定义
│       └── {类名}.ts             # API 请求函数
└── views/
    └── {apiPrefix}/
        └── {entity-kebab-case}/  # 如：demo-order、sys-dict
            ├── {变量名}.data.ts   # 表格列、搜索表单、表单Schema、详情Schema
            ├── index.vue          # 列表页主页面
            ├── {类名}Modal.vue    # 新增/编辑弹窗
            └── {类名}ViewModal.vue # 详情查看弹窗
```

## 技术栈约定

- 框架：**Vue 3** + **TypeScript** + `<script lang="ts" setup>`
- HTTP：`defHttp`（来自 `@mfish/core/utils/http/axios`）
- 表格：`BasicTable` + `useTable`（来自 `@mfish/core/components/Table`）
- 弹窗：`BasicModal` + `useModal` / `useModalInner`（来自 `@mfish/core/components/Modal`）
- 表单：`BasicForm` + `useForm`（来自 `@mfish/core/components/Form`）
- 详情：`Description` + `useDescription`（来自 `@mfish/core/components/Description`）
- 字典：`buildDictTag` + `getDictProps`（来自 `@mfish/core/components/DictTag`）
- 权限：`v-auth="'{apiPrefix}:{entityName}:操作'"`（insert/update/delete/query/export）
- 基础类型：`BaseEntity<string>`、`PageResult<T>`、`ReqPage`（来自 `@mfish/core/api`）
- ID 类型：默认 `string`，数值型主键时为 `number`

---

## 生成步骤

### 第一步：收集信息

询问用户（如未提供）：
1. **模块名（apiPrefix）**（如：`demo`、`sys`、`nocode`）
2. **类名（PascalCase）**（如：`DemoOrder`）
3. **中文名称**（如：销售订单）
4. **字段列表**：字段名(camelCase)、TS 类型（`string`/`number`/`boolean`）、中文描述、是否可选
5. **搜索字段**：哪些字段出现在搜索表单中（及组件类型：`Input` / `ApiSelect`+字典编码 / `DatePicker`）
6. **表单字段**：哪些字段出现在新增/编辑表单中（及组件类型，是否必填）
7. **字典字段**：哪些字段使用字典渲染（需提供字典编码）

---

### 第二步：生成六个文件

按以下顺序生成，所有文件路径基于 `mfish-nocode-view/src/` 目录。

#### 1. Model 类型定义（`api/{apiPrefix}/model/{类名}Model.ts`）

```typescript
import { BaseEntity, PageResult, ReqPage } from "@mfish/core/api";

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */
export interface {类名} extends BaseEntity<string> {
  //{字段注释}
  {字段名}?: {TS类型};
  // ... 更多字段
}

export interface Req{类名} extends ReqPage {
  //{搜索字段注释}
  {搜索字段名}?: {TS类型};
  // ... 更多搜索字段
}

//分页结果集
export type {类名}PageModel = PageResult<{类名}>;
```

**字段类型映射规则：**

| Java/DB 类型 | TS 类型 |
|---|---|
| `String`、`Date` | `string` |
| `Integer`、`Long`、`Short`、`Double`、`BigDecimal` | `number` |
| `Boolean` | `boolean` |

---

#### 2. API 请求文件（`api/{apiPrefix}/{类名}.ts`）

```typescript
import { defHttp } from "@mfish/core/utils/http/axios";
import { {类名}, Req{类名}, {类名}PageModel } from "@/api/{apiPrefix}/model/{类名}Model";

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */
enum Api {
  {类名} = "/{apiPrefix}/{变量名}"
}

/**
 * 分页列表查询
 */
export const get{类名}List = (req{类名}?: Req{类名}) => {
  return defHttp.get<{类名}PageModel>({ url: Api.{类名}, params: req{类名} });
};

/**
 * 通过id查询
 */
export function get{类名}ById(id: string) {
  return defHttp.get<{类名}>({ url: `${Api.{类名}}/${id}` });
}

/**
 * 导出{中文名称}
 */
export function export{类名}(req{类名}?: Req{类名}) {
  return defHttp.download({ url: `${Api.{类名}}/export`, params: req{类名} });
}

/**
 * 新增{中文名称}
 */
export function insert{类名}({变量名}: {类名}) {
  return defHttp.post<{类名}>({ url: Api.{类名}, params: {变量名} }, { successMessageMode: "message" });
}

/**
 * 修改{中文名称}
 */
export function update{类名}({变量名}: {类名}) {
  return defHttp.put<{类名}>({ url: Api.{类名}, params: {变量名} }, { successMessageMode: "message" });
}

/**
 * 删除{中文名称}
 */
export function delete{类名}(id: string) {
  return defHttp.delete<boolean>({ url: `${Api.{类名}}/${id}` }, { successMessageMode: "message" });
}

/**
 * 批量删除{中文名称}
 */
export function deleteBatch{类名}(ids: string) {
  return defHttp.delete<boolean>({ url: `${Api.{类名}}/batch/${ids}` }, { successMessageMode: "message" });
}
```

> 若 ID 类型为数值型（`number`），`delete{类名}` 参数类型改为 `number`。

---

#### 3. data 配置文件（`views/{apiPrefix}/{entity-kebab-case}/{变量名}.data.ts`）

```typescript
import { BasicColumn, FormSchema } from "@mfish/core/components/Table";
import { DescItem } from "@mfish/core/components/Description";
// 有字典字段时引入（无字典字段则删除）
import { buildDictTag, getDictProps } from "@mfish/core/components/DictTag";

/**
 * @description: {中文名称}
 * @author: mfish
 * @date: {当前日期}
 * @version: V2.3.1
 */

// ========== 表格列定义 ==========
export const columns: BasicColumn[] = [
  // 普通字段
  {
    title: "{字段中文名}",
    dataIndex: "{字段名}",
    width: 120
  },
  // 字典字段（有字典时使用 customRender）
  {
    customRender: ({ record }) => {
      return buildDictTag("{字典编码}", record.{字段名});
    },
    title: "{字段中文名}",
    dataIndex: "{字段名}",
    width: 120
  }
];

// ========== 搜索表单 Schema ==========
export const searchFormSchema: FormSchema[] = [
  // 普通输入框
  {
    field: "{字段名}",
    label: "{字段中文名}",
    component: "Input",
    colProps: { xl: 5, md: 6 }
  },
  // 字典下拉（单选）
  {
    field: "{字段名}",
    label: "{字段中文名}",
    component: "ApiSelect",
    componentProps: getDictProps("{字典编码}"),
    colProps: { xl: 5, md: 6 }
  },
  // 字典下拉（多选）
  {
    field: "{字段名}",
    label: "{字段中文名}",
    component: "ApiSelect",
    componentProps: { ...getDictProps("{字典编码}"), mode: "multiple" },
    colProps: { xl: 5, md: 6 }
  }
];

// ========== 新增/编辑表单 Schema ==========
export const {变量名}FormSchema: FormSchema[] = [
  {
    field: "id",
    label: "唯一ID",
    component: "Input",
    show: false
  },
  // 文本输入
  {
    field: "{字段名}",
    label: "{字段中文名}",
    component: "Input",
    required: true  // 必填时加上
  },
  // 数值输入
  {
    field: "{字段名}",
    label: "{字段中文名}",
    component: "InputNumber"
  },
  // 字典下拉
  {
    field: "{字段名}",
    label: "{字段中文名}",
    component: "ApiSelect",
    componentProps: getDictProps("{字典编码}")
  },
  // 日期（仅日期）
  {
    field: "{字段名}",
    label: "{字段中文名}",
    component: "DatePicker",
    componentProps: {
      valueFormat: "YYYY-MM-DD",
      format: "YYYY-MM-DD",
      getPopupContainer: () => document.body
    }
  },
  // 日期时间
  {
    field: "{字段名}",
    label: "{字段中文名}",
    component: "DatePicker",
    componentProps: {
      valueFormat: "YYYY-MM-DD HH:mm:ss",
      format: "YYYY-MM-DD HH:mm:ss",
      showTime: { format: "HH:mm:ss" },
      getPopupContainer: () => document.body
    }
  }
];

// ========== 详情查看 Schema ==========
export class {类名}Desc {
  viewSchema: DescItem[] = [
    {
      label: "id",
      field: "id",
      show: () => false
    },
    // 普通字段
    {
      field: "{字段名}",
      label: "{字段中文名}"
    },
    // 字典字段
    {
      render: (val) => {
        if (val === undefined) return;
        return buildDictTag("{字典编码}", val);
      },
      field: "{字段名}",
      label: "{字段中文名}"
    }
  ];
}
```

**组件选择规则：**

| 字段类型 | 表单组件 |
|---|---|
| `string`（普通文本） | `Input` |
| `number`（整数/小数） | `InputNumber` |
| `string`（日期） | `DatePicker`（dateFormat: YYYY-MM-DD）|
| `string`（日期时间） | `DatePicker`（showTime）|
| 有字典 | `ApiSelect` + `getDictProps("{字典编码}")` |

---

#### 4. 列表主页面（`views/{apiPrefix}/{entity-kebab-case}/index.vue`）

```vue
<!--
 @description: {中文名称}
 @author: mfish
 @date: {当前日期}
 @version: V2.3.1
-->
<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <AButton type="primary" @click="handleCreate" v-auth="'{apiPrefix}:{变量名}:insert'">新增</AButton>
        <AButton color="warning" @click="handleExport" v-auth="'{apiPrefix}:{变量名}:export'">导出</AButton>
        <AButton color="error" @click="handleBatchDelete" v-auth="'{apiPrefix}:{变量名}:delete'">批量删除</AButton>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
              {
                icon: 'ant-design:info-circle-outlined',
                onClick: handleQuery.bind(null, record),
                auth: '{apiPrefix}:{变量名}:query',
                color: 'success',
                tooltip: '查看'
              },
              {
                icon: 'ant-design:edit-outlined',
                onClick: handleEdit.bind(null, record),
                auth: '{apiPrefix}:{变量名}:update',
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
                auth: '{apiPrefix}:{变量名}:delete',
                tooltip: '删除'
              }
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <{类名}Modal @register="registerModal" @success="handleSuccess" />
    <{类名}ViewModal @register="registerViewModal" />
  </div>
</template>
<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from "@mfish/core/components/Table";
  import { useModal } from "@mfish/core/components/Modal";
  import { Button as AButton } from "@mfish/core/components/Button";
  import { deleteBatch{类名}, delete{类名}, export{类名}, get{类名}List } from "@/api/{apiPrefix}/{类名}";
  import {类名}Modal from "./{类名}Modal.vue";
  import {类名}ViewModal from "./{类名}ViewModal.vue";
  import { columns, searchFormSchema } from "./{变量名}.data";
  import { {类名} } from "@/api/{apiPrefix}/model/{类名}Model";
  import { ref } from "vue";
  import { useMessage } from "@mfish/core/hooks";

  defineOptions({ name: "{类名}Management" });
  const [registerModal, { openModal }] = useModal();
  const [registerViewModal, { openModal: openViewModal }] = useModal();
  const selectedRowKeys = ref<any[]>([]);
  const [registerTable, { reload, getForm }] = useTable({
    title: "{中文名称}列表",
    api: get{类名}List,
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
    rowSelection: {
      onChange: (rowKeys: any[]) => {
        selectedRowKeys.value = rowKeys;
      }
    },
    actionColumn: {
      width: 120,
      title: "操作",
      dataIndex: "action"
    }
  });
  const { createMessage } = useMessage();

  function handleCreate() {
    openModal(true, { isUpdate: false });
  }

  function handleExport() {
    export{类名}({ ...getForm().getFieldsValue(), pageNum: 1, pageSize: 1000 });
  }

  function handleQuery({变量名}: {类名}) {
    openViewModal(true, { record: {变量名} });
  }

  function handleEdit({变量名}: {类名}) {
    openModal(true, { record: {变量名}, isUpdate: true });
  }

  function handleDelete({变量名}: {类名}) {
    if ({变量名}.id) {
      delete{类名}({变量名}.id).then(() => {
        handleSuccess();
      });
    }
  }

  function handleBatchDelete() {
    if (selectedRowKeys.value.length > 0) {
      deleteBatch{类名}(selectedRowKeys.value.join(",")).then(() => {
        handleSuccess();
      });
    } else {
      createMessage.warning("请勾选要删除的数据");
    }
  }

  function handleSuccess() {
    reload();
  }
</script>
```

---

#### 5. 新增/编辑弹窗（`views/{apiPrefix}/{entity-kebab-case}/{类名}Modal.vue`）

```vue
<!--
 @description: {中文名称}
 @author: mfish
 @date: {当前日期}
 @version: V2.3.1
-->
<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <BasicForm @register="registerForm" @submit="handleSubmit" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, computed, unref } from "vue";
  import { BasicForm, useForm } from "@mfish/core/components/Form";
  import { {变量名}FormSchema } from "./{变量名}.data";
  import { BasicModal, useModalInner } from "@mfish/core/components/Modal";
  import { insert{类名}, update{类名} } from "@/api/{apiPrefix}/{类名}";

  defineOptions({ name: "{类名}Modal" });
  const emit = defineEmits(["success", "register"]);
  const isUpdate = ref(true);
  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    name: "model_form_item",
    labelWidth: 100,
    baseColProps: { span: 12 },
    schemas: {变量名}FormSchema,
    showActionButtonGroup: false,
    autoSubmitOnEnter: true
  });
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields().then();
    setModalProps({ confirmLoading: false, width: "800px" });
    isUpdate.value = !!data?.isUpdate;
    if (unref(isUpdate)) {
      setFieldsValue({ ...data.record }).then();
    }
  });
  const getTitle = computed(() => (unref(isUpdate) ? "编辑{中文名称}" : "新增{中文名称}"));

  async function handleSubmit() {
    const values = await validate();
    setModalProps({ confirmLoading: true });
    if (unref(isUpdate)) {
      save{类名}(update{类名}, values);
    } else {
      save{类名}(insert{类名}, values);
    }
  }

  function save{类名}(save, values) {
    save(values)
      .then(() => {
        emit("success");
        closeModal();
      })
      .finally(() => {
        setModalProps({ confirmLoading: false });
      });
  }
</script>
```

---

#### 6. 详情查看弹窗（`views/{apiPrefix}/{entity-kebab-case}/{类名}ViewModal.vue`）

```vue
<!--
 @description: {中文名称}查看
 @author: mfish
 @date: {当前日期}
 @version: V2.3.1
-->
<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="{中文名称}信息">
    <Description @register="registerDesc" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { BasicModal, useModalInner } from "@mfish/core/components/Modal";
  import { Description, useDescription } from "@mfish/core/components/Description";
  import { ref } from "vue";
  import { {类名}Desc } from "./{变量名}.data";

  defineOptions({ name: "{类名}ViewModal" });
  const {变量名}Data = ref();
  const {变量名}Desc = new {类名}Desc();
  const [registerModal, { setModalProps }] = useModalInner(async (data) => {
    setModalProps({
      confirmLoading: false,
      width: "800px",
      cancelText: "关闭",
      showOkBtn: false
    });
    {变量名}Data.value = data.record;
  });
  const [registerDesc] = useDescription({
    data: {变量名}Data,
    schema: {变量名}Desc.viewSchema,
    column: 2
  });
</script>
```

---

## 命名约定

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `{类名}` | PascalCase 类名 | `DemoOrder` |
| `{变量名}` | camelCase 变量名 | `demoOrder` |
| `{apiPrefix}` | 模块路径（小写） | `demo` |
| `{entity-kebab-case}` | kebab-case 目录名 | `demo-order` |

---

## 注意事项

1. **字典字段**：`columns` 和 `viewSchema` 中使用 `buildDictTag`，`searchFormSchema` 和 `FormSchema` 中使用 `getDictProps`；只要有字典字段就需要在文件顶部引入 `buildDictTag` 和 `getDictProps`
2. **无搜索表单字段**：`searchFormSchema` 可以为空数组 `[]`，`useSearchForm` 设为 `false`
3. **数值型主键**：`delete{类名}` 参数类型改为 `number`，`Model` 中 `BaseEntity<number>`
4. **导出功能可选**：若不需要导出，删除 `handleExport`、`export{类名}` 的引入和 toolbar 中的导出按钮
5. **批量删除可选**：若不需要批量删除，删除 `handleBatchDelete`、`deleteBatch{类名}` 的引入、`rowSelection` 配置和 toolbar 中的批量删除按钮
6. **配合后端 CRUD**：前端文件路径中的 `{apiPrefix}/{变量名}` 对应后端 Controller 的 `@RequestMapping` 路径

---

## 参考实现

- 含字典的完整示例：[demoOrder.data.ts](mfish-nocode-view/src/views/demo/demo-order/demoOrder.data.ts)
- API 文件示例：[DemoOrder.ts](mfish-nocode-view/src/api/demo/DemoOrder.ts)
- Model 示例：[DemoOrderModel.ts](mfish-nocode-view/src/api/demo/model/DemoOrderModel.ts)
- 无字典简单示例：[demoDataScope.data.ts](mfish-nocode-view/src/views/demo/demo-data-scope/demoDataScope.data.ts)
- 后端 CRUD 配套请使用 **crud-generator** skill
