---
name: workflow-audit
description: 为 mfish-nocode-pro 项目集成 Flowable 工作流审批能力，包括注册 FlowKey、实体审批状态字段、Service 启动/撤回流程、Controller 审批回调接口、Feign 回调接口注册五个步骤的完整代码模板。当用户说"带工作流审批"、"发布审核流程"、"集成工作流"、"审批回调"时使用此 skill。
---

# 工作流审批集成

适用于需要**发布/审核**流程的业务模块（如大屏发布、内容审核等），在标准 CRUD 基础上叠加工作流能力。

## 核心概念

| 组件 | 说明 |
|------|------|
| `FlowableParam<T>` | 启动工作流参数：`key`(流程定义key)、`id`(业务id)、`prefix`(回调URL前缀)、`callback`(回调Feign接口全路径) |
| `FlowKey` 枚举 | 流程定义key枚举，新业务须在此注册，路径：`mf-api/mf-workflow-api/.../FlowKey.java` |
| `RemoteAuditApi<T>` | 审批回调接口，Controller 需实现 `approved`/`rejected`/`canceled` 三个方法 |
| `WorkflowCompleteResult` | 回调结果体：`processInstanceId`、`comment`、`eventName` |
| `RemoteWorkflowService` | Feign 客户端，用于启动/删除流程实例 |

## 审批状态约定

| auditState 值 | 含义 | 触发时机 |
|--------------|------|---------|
| `0` | 审核中 | 发布提交时设置 |
| `1` | 已通过 | `approved` 回调时设置 |
| `2` | 未通过 | `rejected` 回调时设置 |
| `null` | 已取消 | `canceled` 回调时设置 |

---

## 集成步骤

### 第一步：注册 FlowKey

在 `FlowKey.java` 枚举中添加新流程定义 key：

```java
// mf-api/mf-workflow-api/src/main/java/cn/com/mfish/common/workflow/api/enums/FlowKey.java
新业务名称 ("xxx_release"),  // key 需与 BPMN 文件中的 process id 一致
```

### 第二步：实体新增审批状态字段

```java
@Schema(description = "审核状态 null 未发布 0 审核中 1 已通过 2 未通过")
private Integer auditState;
```

**注意**：实体类必须添加完整的 Swagger 注解：
- **类级别**：`@Schema(description = "描述信息", name = "类名")`
- **字段级别**：每个字段都需添加 `@Schema(description = "字段描述")`
- 便于生成完整的 API 文档和 Swagger UI 展示

### 第三步：Service 中启动和撤回工作流

```java
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.FlowableParam;
import cn.com.mfish.common.workflow.api.enums.FlowKey;
import cn.com.mfish.common.workflow.api.remote.RemoteWorkflowService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Service
public class {类名}ServiceImpl extends ServiceImpl<{类名}Mapper, {类名}> implements {类名}Service {

    @Resource
    RemoteWorkflowService remoteWorkflowService;

    /** 发布（新增并启动工作流） */
    @Override
    @Transactional
    public Result<{类名}> insert{类名}({类名} entity) {
        // 业务唯一性校验：避免重复发布
        if (baseMapper.exists(new LambdaQueryWrapper<{类名}>()
                .eq({类名}::getSourceId, entity.getSourceId()))) {
            throw new MyRuntimeException("错误:已存在，不能重复发布!");
        }
        entity.setAuditState(0);
        if (save(entity)) {
            startProcess(entity);
            return Result.ok(entity, "{中文名称}-发布成功!");
        }
        return Result.fail(entity, "错误:{中文名称}-发布失败!");
    }

    /** 撤回（删除记录并撤销工作流） */
    @Override
    @Transactional
    public Result<Boolean> delete{类名}(String id) {
        {类名} entity = getById(id);
        if (entity == null) {
            return Result.ok(false, "错误:记录不存在!");
        }
        if (remove(new LambdaQueryWrapper<{类名}>().eq({类名}::getId, id))) {
            Result<String> result = remoteWorkflowService.delProcessByBusinessKey(
                    RPCConstants.INNER, entity.getId(), "用户撤回");
            if (!result.isSuccess()) {
                throw new MyRuntimeException(result.getMsg());
            }
            return Result.ok(true, "撤回成功!");
        }
        return Result.fail(false, "错误：撤回失败!");
    }

    /** 审批回调：更新审批状态 */
    @Override
    public Result<String> audit(String id, Integer auditState, WorkflowCompleteResult result) {
        {类名} entity = baseMapper.selectById(id);
        if (entity == null) throw new MyRuntimeException("错误:记录不存在!");
        entity.setAuditState(auditState);
        if (!updateById(entity)) throw new MyRuntimeException("错误:审批操作异常!");
        return Result.ok(id, "审批操作成功!");
    }

    /** 启动工作流 */
    private void startProcess({类名} entity) {
        Result<String> result = remoteWorkflowService.startProcess(RPCConstants.INNER,
            new FlowableParam<String>()
                .setKey(FlowKey.{对应枚举}.toString())
                .setId(entity.getId())            // 业务id 作为 businessKey
                .setPrefix("{回调URL前缀}")         // Controller @RequestMapping 路径（不含 /）
                .setCallback("{Feign接口全路径}")   // 例如：cn.com.mfish.xxx.api.remote.RemoteXxxService
        );
        if (!result.isSuccess()) throw new MyRuntimeException(result.getMsg());
    }
}
```

### 第四步：Controller 追加审批回调接口

工作流引擎审批完成后，通过 Feign 回调这三个接口，**无需 `@RequiresPermissions`**：

```java
import cn.com.mfish.common.core.entity.WorkflowCompleteResult;

// 追加到现有 Controller 末尾

@PostMapping("/approved/{id}")
public Result<String> approved(
        @PathVariable String id,
        @RequestBody WorkflowCompleteResult result) {
    return {变量名}Service.audit(id, 1, result);
}

@PostMapping("/rejected/{id}")
public Result<String> rejected(
        @PathVariable String id,
        @RequestBody WorkflowCompleteResult result) {
    return {变量名}Service.audit(id, 2, result);
}

@PostMapping("/canceled/{id}")
public Result<String> canceled(
        @PathVariable String id,
        @RequestBody WorkflowCompleteResult result) {
    return {变量名}Service.audit(id, null, result);
}
```

### 第五步：注册 Feign 回调接口（微服务模式）

在 `mf-api/mf-xxx-api` 模块中定义回调 Feign 接口，继承 `RemoteAuditApi<String>`：

```java
import cn.com.mfish.common.core.entity.RemoteAuditApi;

@FeignClient(
    contextId = "remote{类名}Service",
    value = ServiceConstants.XXX_SERVICE,
    fallbackFactory = Remote{类名}FallBack.class
)
public interface Remote{类名}Service extends RemoteAuditApi<String> {
    // 其他跨服务调用方法...
}
```

> - `FlowableParam.callback` 填写此接口的**全路径类名**
> - `FlowableParam.prefix` 填写 Controller 的 `@RequestMapping` 路径（**不含 `/`**）

---

## 注意事项

- `prefix` 与 Controller `@RequestMapping` 必须一致，回调 URL 拼接规则：`/{prefix}/approved/{id}`
- 编辑已发布记录前需校验审核状态：`auditState=1` 的记录禁止直接编辑，需先撤回
- 重复发布校验：发布前通过 `sourceId` 或业务唯一键检查是否已存在记录
- 撤回时需同步调用 `remoteWorkflowService.delProcessByBusinessKey(...)` 删除工作流实例
- Service 接口中需声明 `audit(String id, Integer auditState, WorkflowCompleteResult result)` 方法
- **异常处理规范**：人为抛出的业务异常统一采用 `MyRuntimeException` 处理
  - **引入包**：`import cn.com.mfish.common.core.exception.MyRuntimeException;`
  - **使用场景**：记录不存在、重复提交、状态校验失败等业务异常情况
  - **示例代码**：
    ```java
    // 记录不存在
    if (entity == null) {
        throw new MyRuntimeException("错误：记录不存在!");
    }
    
    // 重复发布校验
    if (baseMapper.exists(new LambdaQueryWrapper<Entity>()
            .eq(Entity::getSourceId, sourceId))) {
        throw new MyRuntimeException("错误：已存在，不能重复发布!");
    }
    
    // 审批状态校验
    if (!"approved".equals(entity.getAuditState())) {
        throw new MyRuntimeException("错误：审批状态不正确!");
    }
    ```
  - **消息格式**：建议以 `"错误:"` 开头，便于前端统一处理和识别
  - **不要使用**：避免直接使用 `RuntimeException` 或其他自定义异常
- **安全规范 - 异常信息不暴露给前端**：
  - **核心原则**：返回给前端的错误消息必须是友好的、通用的提示，不能包含具体的异常堆栈或技术细节
  - **错误示例**：`return Result.fail(false, "错误：流程配置不正确，" + e.getMessage());` ❌
  - **正确示例**：`return Result.fail(false, "错误：流程配置不正确，请检查流程设计是否完整且符合规范");` ✅
  - **日志记录**：详细的异常信息应通过 `log.error()` 记录到日志文件中，便于开发人员排查
  - **实现模式**：
    ```java
    try {
        // 业务逻辑
        BpmnConverter.convertToBpmn(...);
    } catch (Exception e) {
        // 详细异常信息记录到日志
        log.error("流程格式化失败：{}", e.getMessage(), e);
        // 返回给前端的是友好的提示信息
        return Result.fail(false, "错误：流程配置不正确，请检查流程设计是否完整且符合规范");
    }
    ```

---

## 相关参考

- Controller 参考：[ScreenResourceController.java](mf-business/mf-nocode/src/main/java/cn/com/mfish/nocode/controller/ScreenResourceController.java)
- Service 参考：[ScreenResourceServiceImpl.java](mf-business/mf-nocode/src/main/java/cn/com/mfish/nocode/service/impl/ScreenResourceServiceImpl.java)
- FlowKey 枚举：[FlowKey.java](mf-api/mf-workflow-api/src/main/java/cn/com/mfish/common/workflow/api/enums/FlowKey.java)
- 工作流参数：[FlowableParam.java](mf-api/mf-workflow-api/src/main/java/cn/com/mfish/common/workflow/api/entity/FlowableParam.java)
- 审批回调接口：[RemoteAuditApi.java](mf-common/mf-common-core/src/main/java/cn/com/mfish/common/core/entity/RemoteAuditApi.java)
- 工作流 Feign 客户端：[RemoteWorkflowService.java](mf-api/mf-workflow-api/src/main/java/cn/com/mfish/common/workflow/api/remote/RemoteWorkflowService.java)
